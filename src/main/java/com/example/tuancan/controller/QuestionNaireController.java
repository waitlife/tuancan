package com.example.tuancan.controller;

import com.example.tuancan.model.QuestionNaire;
import com.example.tuancan.service.QuestionNaireService;
import com.example.tuancan.utils.JsonUtil;
import com.example.tuancan.utils.PageUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @Author xiaoqy
 * @Date 2018/8/3
 * @Description
 */
@Slf4j
@RequestMapping("questionNaire")
@Controller
public class QuestionNaireController {

    @Autowired
    private QuestionNaireService questionNaireService;


    @RequestMapping(value = {"/show/{pagenum}","/show"},method = {RequestMethod.GET,RequestMethod.POST})
    public String show(Model model,@PathVariable(value = "pagenum",required = false) Integer pageNum){
        if (pageNum==null||pageNum<=0){
            pageNum=1;
        }
        //Page<Object> page = PageHelper.startPage(pageNum, 5);
        List<QuestionNaire> questionNaires = questionNaireService.selectAll();
        PageInfo<QuestionNaire> pageInfo = new PageInfo<QuestionNaire>(questionNaires);

        log.info(JsonUtil.toJson(pageInfo.getList()));
        PageUtil.setPageModel(model,pageInfo,"/questionNaire/show");

        return "unitmealmanager/question_list";
    }
}
