package com.example.tuancan.service;

import com.example.tuancan.dao.MessageInfo;

public class CommonRpcTest {

    public static void main(String[] args) {
        SMSVerification smsVerification = new SMSVerification();
        smsVerification.setRequestPhoneNumbers("15982224307");
        MessageInfo messageInfo = smsVerification.sendMessageVerification();
        System.out.println();
    }
}
