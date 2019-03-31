package com.example.tuancan.service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.example.tuancan.dao.MessageInfo;
import com.example.tuancan.model.Manager;
import com.google.gson.Gson;

public class SMSVerification {
    private static final String accessKeyId = "LTAI2sxci8HdRWWZ";
    private static final String accessKeySecret = "TniP03TAGmD9Z4F67xZYF0erLHX3jd";
    private static final String regionId = "cn-chengdu";
    private static final String AccessKeyId = "cn-LTAIF1v3Yi6nU0Er";
    private static final String regionIAccessKeySecretd = "mitxdwEozDdOPh83RFbhCmSJ09bT4h";
    private CommonRequest request;
    private IAcsClient client;

    public SMSVerification() {
        //初始化信息
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        this.client = new DefaultAcsClient(profile);
        this.request = new CommonRequest();
        this.request.setProtocol(ProtocolType.HTTPS);
        this.request.setDomain("dysmsapi.aliyuncs.com");
        this.request.setVersion("2017-05-25");
        this.request.setAction("SendSms");
        this.request.putQueryParameter("AccessKeyId", AccessKeyId);
        this.request.putQueryParameter("regionIAccessKeySecretd", regionIAccessKeySecretd);
        this.request.setMethod(MethodType.POST);
        //必填:短信签名-可在短信控制台中找到
        this.request.putQueryParameter("SignName", "团餐平台");
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        String templateParam = "{ \"code\":\"" + String.valueOf((int)(Math.random() * 999999)) + "\"}";
        this.request.putQueryParameter("TemplateParam", templateParam);
        //必填:短信模板-可在短信控制台中找到
        this.request.putQueryParameter("TemplateCode", "SMS_162199936");

    }

    public void setRequestPhoneNumbers(String phoneNumbers) {
        //必填:待发送手机号
        this.request.putQueryParameter("PhoneNumbers", phoneNumbers);
    }

    public void setRequestCode(String code) {
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        String templateParam = "{ \"code\":\"" + code + "\"}";
        this.request.putQueryParameter("TemplateParam", templateParam);
    }


    //发送短信
    public MessageInfo sendMessageVerification() {
        try {
            Gson gson = new Gson();
            CommonResponse response = client.getCommonResponse(request);
            MessageInfo messageInfo = gson.fromJson(response.getData(), MessageInfo.class);
            return messageInfo;
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

}
