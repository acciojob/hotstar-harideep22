package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription=new Subscription();

        User user=userRepository.findById(subscriptionEntryDto.getUserId()).get();

        long millis=System.currentTimeMillis();
        java.sql.Date date=new java.sql.Date(millis);

        subscription.setStartSubscriptionDate(date);
        subscription.setUser(user);
        subscription.setNoOfScreensSubscribed(subscription.getNoOfScreensSubscribed());
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());

        SubscriptionType type=subscriptionEntryDto.getSubscriptionType();
        Integer finalAmount=0;
        if(type==SubscriptionType.BASIC){
            finalAmount=500+200*subscription.getNoOfScreensSubscribed();
        } else if (type==SubscriptionType.PRO) {
            finalAmount=800+250*subscription.getNoOfScreensSubscribed();
        }
        else {
            finalAmount=1000+350*subscription.getNoOfScreensSubscribed();
        }
        subscription.setTotalAmountPaid(finalAmount);
        user.setSubscription(subscription);
        userRepository.save(user);

        return finalAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user=userRepository.findById(userId).get();

        Subscription subscription=user.getSubscription();
        SubscriptionType type=subscription.getSubscriptionType();
        Integer differeceAmount=0;

        if(type==SubscriptionType.BASIC){
            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(800+250*subscription.getNoOfScreensSubscribed());
            differeceAmount=subscription.getTotalAmountPaid()-(500+(200*subscription.getNoOfScreensSubscribed()));
        }
        else if(type==SubscriptionType.PRO){
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(1000+350*subscription.getNoOfScreensSubscribed());
            differeceAmount=subscription.getTotalAmountPaid()-(800+(250*subscription.getNoOfScreensSubscribed()));
        }
        else {
            throw new Exception("Already the best Subscription");
        }

        return differeceAmount;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList=subscriptionRepository.findAll();
        Integer revenue=0;
        for(Subscription subscription:subscriptionList){
            revenue+=subscription.getTotalAmountPaid();
        }

        return revenue;
    }

}
