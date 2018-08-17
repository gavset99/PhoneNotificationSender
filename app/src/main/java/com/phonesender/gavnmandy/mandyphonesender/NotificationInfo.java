package com.phonesender.gavnmandy.mandyphonesender;

public class NotificationInfo {
    public String nKey, nTitle, nBody;


    public NotificationInfo(){

    }

    public NotificationInfo(String key, String notification, String title){
        this.nBody = notification;
        this.nKey = key;
        this.nTitle = title;
    }
}
