package com.example.tuancan.controller;

import com.example.tuancan.service.AnswerService;
import com.example.tuancan.utils.AuthorizedAnnotation;
import com.example.tuancan.utils.MyWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Controller
@RequestMapping("/answer")
@AuthorizedAnnotation
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private MyWebSocket myWebSocket;



    @RequestMapping("/")
    @ResponseBody
    public  String answer(HttpServletRequest request){
        try {
            String userClientId = String.valueOf(request.getSession().getAttribute("sessionId"));
            if (Objects.isNull(userClientId)){
                return "failed!";
            }
            myWebSocket.sendSingleMessage("sever answer", userClientId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }


    @RequestMapping("/index")
    public  String index(HttpServletRequest request){
        request.getSession(true).setAttribute("unitID","1");
        request.getSession(true).setAttribute("sessionId","1");
        request.getSession(true).setAttribute("dcno","1");
        return "/manager/pt_index";
    }

    @RequestMapping("/groupmanager")
    public  String getGroupManager(){
        return "/groupmanager/index";
    }


}
