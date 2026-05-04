package com.example.camel.exercises.errorHandlers;

import java.util.Date;

/**
 * @author Heshan Karunaratne
 */
public class HelloBean {
    public HelloBean() {
        System.out.println("HellBean Constructor");
    }

    public String callGood() {
        System.out.println("Good Call for " + CommonErrorHandlerRoute.COUNTER.get());
        return "Good:" + new Date();
    }

    public String callBad() {
        System.out.println("Bad Call for " + CommonErrorHandlerRoute.COUNTER.get());
        throw new RuntimeException("Exception for " + CommonErrorHandlerRoute.COUNTER.get());
    }
}