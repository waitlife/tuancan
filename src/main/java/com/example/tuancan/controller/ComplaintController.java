package com.example.tuancan.controller;

import com.example.tuancan.dto.Result;
import com.example.tuancan.model.Complaint;
import com.example.tuancan.service.ComplaintService;
import com.example.tuancan.utils.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @author xiao
 */
@Controller
@Slf4j
@RequestMapping("/complaint")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @RequestMapping(value = {"/yes_list/{pagenum}","/yes_list"})
    public String  yeslist(Model model, @PathVariable(value = "pagenum",required = false) Integer pageNum){
        if (pageNum==null||pageNum<=0){
            pageNum=1;
        }
        Page<Object> page = PageHelper.startPage(pageNum, 9);
        List<Complaint> complaints = complaintService.selectAll();
        PageInfo<Complaint> pageInfo = new PageInfo<Complaint>(complaints);

        log.info(pageInfo.getPages()+"xx"+pageInfo.getNextPage()+"xx"+
                pageInfo.isIsFirstPage()+pageInfo.isHasPreviousPage());
        log.info(JsonUtil.toJson(page.getResult()));
        PageUtil.setPageModel(model,pageInfo,"/complaint/yes_list");

        return "/manager/cp_list";
    }
    @RequestMapping(value = {"/no_list/{pagenum}","/no_list"})
    public String Nolist(Model model,@PathVariable(value = "pagenum",required = false) Integer pageNum){
        if (pageNum==null||pageNum<=0){
            pageNum=1;
        }
        Page<Object> page = PageHelper.startPage(pageNum, 5);
        List<Complaint> complaints = complaintService.selectNULL();
        PageInfo<Complaint> pageInfo = new PageInfo<Complaint>(complaints);

        log.info(JsonUtil.toJson(page.getResult()));
        PageUtil.setPageModel(model,pageInfo,"/complaint/no_list");

        return "/manager/cp_list";
    }


    @AuthorizedAnnotation
    @RequestMapping(value = "/cp_details/{id}",method = {RequestMethod.GET})
    public String getOne(@PathVariable(value = "id")Integer id,Model model){
        log.info(id+">>>>cpdetails");
        Complaint complaint = complaintService.selectById(id);
        model.addAttribute("cp",complaint);
        return "/unitmealmanager/cp_update";
    }

    /**
     * 平台投诉
     * @param model
     * @param complaint
     * @return
     */
    @AuthorizedAnnotation
    @RequestMapping(value = "/save",method = {RequestMethod.POST,RequestMethod.GET})
    public String  saveorupdate(Model model,Complaint  complaint){
        log.info(JsonUtil.toJson(complaint));
        if (!StringUtil.isNullOrEmpty(complaint.getComplaintContent())){
            //更新
            complaint.setComplaintDate(new Date());
            int updateOne = complaintService.insertOne(complaint);
            log.info(updateOne+"updateOne");
            return "redirect:/complaint/unitcomplaintlist/1";
        }else {
           return "unitmealmanager/cp_update.html";
        }
    }

    @RequestMapping(value = "/delete/{id}",method = {RequestMethod.POST})
    @ResponseBody
    public String  delete(Model model,@PathVariable("id") Integer id){
        int i = complaintService.deleteOne(id);
        log.info(id+">>delete"+i);
        return "ok";
    }

    @RequestMapping(value = {"/search/{name}","/search/{name}/{pagenum}"},method = {RequestMethod.POST,RequestMethod.GET})
    public String  search(Model model,@PathVariable(value = "name",required = false) String name,
                          @PathVariable(value = "pagenum",required = false) Integer pageNum,
                          @RequestParam(value = "asyc",required = false,defaultValue ="false")Boolean asyc){
        if (pageNum==null||pageNum<=0){
            pageNum=1;
        }
        if (StringUtils.isEmpty(name)||name.equals("*")){
            name="";
        }
        log.info(name);
        String decode=null;
        String encode=null;
        try {
            //先解码 在编码
            decode= URLDecoder.decode(name, "utf-8");
            decode= URLDecoder.decode(decode, "utf-8");
            encode = URLEncoder.encode(decode,"utf-8");
            encode = URLEncoder.encode(encode,"utf-8");
            log.info(encode+"++"+decode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Page<Object> page = PageHelper.startPage(pageNum, 9);
        List<Complaint> complaints = complaintService.selectAllBByComplaintSettleOrderBySettleDate(decode);
        PageInfo<Complaint> pageInfo = new PageInfo<Complaint>(complaints);
        log.info(JsonUtil.toJson(complaints));
        PageUtil.setPageModel(model,pageInfo,"/complaint/search/"+encode);

        if (!asyc){
            return "/manager/cp_list";
        }
        return "/manager/cp_list :: #searchtable";
    }

    /*主页*/
    @RequestMapping("/new")
    public String newComplaint() {
        return "/groupmanager/complaint";
    }

    /*新建*/
    @ResponseBody
    @RequestMapping("/creat")
    public Result creatComplaint(@RequestParam("text") String text, @RequestParam(value = "name",required = false) String name) {
        log.info("text:" + text + " name:" + name);
        //TODO 获取当前登录系统的用户
        Complaint complaint = new Complaint();
        complaint.setComplainter(name);
        complaint.setComplaintContent(text);
        Date date = new Date();
        complaint.setComplaintDate(date);

        complaintService.insertOne(complaint);
        log.info(JsonUtil.toJson(complaint));
        return ResultUtil.status(200,"msg");

    }

    /**
     * 用餐员工查看个人投诉情况
     * @param model
     * @param pageNum
     * @return
     */
    @RequestMapping(value = {"/unitcomplaintlist/{pagenum}","/unitcomplaintlist"})
    public String  unitcplist(Model model, @PathVariable(value = "pagenum",required = false) Integer pageNum){
        if (pageNum==null||pageNum<=0){
            pageNum=1;
        }
        //TODO 获取当前用户
        Page<Object> page = PageHelper.startPage(pageNum, 5);
        List<Complaint> complaints = complaintService.selectAllByComplainter("当前用户");
        PageInfo<Complaint> pageInfo = new PageInfo<Complaint>(complaints);

        log.info(JsonUtil.toJson(page.getResult()));
        PageUtil.setPageModel(model,pageInfo,"/complaint/unitcomplaintlist");

        return "/unitmealmanager/cp_list";
    }
}
