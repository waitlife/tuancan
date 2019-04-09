package com.example.tuancan.controller;

import com.example.tuancan.model.DeliveringCompanyStaff;
import com.example.tuancan.model.GroupMealUnit;
import com.example.tuancan.service.DeliveringCompanyStaffService;
import com.example.tuancan.service.GroupMealUnitService;
import com.example.tuancan.utils.JsonUtil;
import com.example.tuancan.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Controller
@Slf4j
public class IndexController {

    @Autowired
    private DeliveringCompanyStaffService deliveringCompanyStaffService;

    @Autowired
    private GroupMealUnitService groupMealUnitService;

    private static Integer ENABLE_MANAGER = 1;

    /*主页*/
    @RequestMapping("/index")
    public String index() {

        return "/groupmanager/index";
    }

    //登录
    @RequestMapping("/login")
    public String login() {
        return "/groupmanager/login";
    }

    //修改密码
    @RequestMapping("/updatePassword")
    public String updatePassword(Model model, HttpServletRequest httpServletRequest) {

        if (httpServletRequest.getSession().getAttribute("staffUsername") == null) {
            return null;
        }
        String username = httpServletRequest.getSession().getAttribute("staffUsername").toString();
        model.addAttribute("username", username);
        return "/groupmanager/updatePassword";
    }


    //执行修改密码
    @RequestMapping("/doUpdatePassword")
    public String doUpdatePassword( @RequestParam(name = "username") String username,@RequestParam(name = "newpassword") String newpassword,
                                   @RequestParam(name = "password") String password,Model model) {
        if(StringUtil.stringEquals(newpassword,password)){
            DeliveringCompanyStaff deliveringCompanyStaff = deliveringCompanyStaffService.selectStaffByName(username);
            deliveringCompanyStaff.setDCompanyStaffPassword(newpassword);
            //第一次修改密码，更新账号的状态
            deliveringCompanyStaff.setDCompanyStaffDefault(0);
            deliveringCompanyStaffService.updateOneById(deliveringCompanyStaff);
            model.addAttribute("message", "密码成功修改，请重新登录！");
            model.addAttribute("username", username);
            return "/groupmanager/login";
        }else {
            model.addAttribute("message", "两次输入的密码不一致！");
            return "/groupmanager/updatePassword";
        }
    }

    @RequestMapping("/doLogin")
    public String doLogin(@RequestParam(name = "username") String name, @RequestParam(name = "password") String password,
                          Model model, HttpServletRequest httpServletRequest) {

        DeliveringCompanyStaff deliveringCompanyStaff = deliveringCompanyStaffService.selectStaffByName(name);
        if (deliveringCompanyStaff == null) {
            model.addAttribute("message", "密码或账号错误！");
            return "/groupmanager/login";
        }
        if (deliveringCompanyStaff.getDCompanyStaffStatus() == ENABLE_MANAGER) {
            if (StringUtil.stringEquals(deliveringCompanyStaff.getDCompanyStaffPassword(), password)) {
                httpServletRequest.getSession().setAttribute("companyId", deliveringCompanyStaff.getDeliveringCompany().getDeliveringCompanyNo());
                httpServletRequest.getSession().setAttribute("staffUsername", deliveringCompanyStaff.getDCompanyStaffLoginname());
                return "redirect:/index";
            } else {
                model.addAttribute("message", "密码或账号错误！");
                return "/groupmanager/login";
            }
        } else {
            model.addAttribute("message", "该账号已被停用！");
            return "/groupmanager/login";
        }
    }

    /**
     * 微信首页
     * @param request
     * @return
     */
    @RequestMapping("/wxindex")
    public String weixinindex(HttpServletRequest request,@RequestParam(value = "openId",required = false) String openId){
        if (Objects.nonNull(openId)){
            log.info("paramopenid:{}",openId);
            request.getSession().setAttribute("openId","o81TH1TH9szWjykZQcY2-1S1_eWI");
        }
        if (StringUtil.isNullOrEmpty(openId)){
            openId=request.getSession().getAttribute("openId").toString();
            log.info("openid:{}",openId);
        }

        GroupMealUnit groupMealUnit = groupMealUnitService.selectByQrCode(openId);
        if (Objects.nonNull(groupMealUnit)){
            log.info("主管登录：{}，{}",openId, JsonUtil.toJson(groupMealUnit));
            request.getSession().setAttribute("unitID",groupMealUnit.getGroupMealUnitId());
            return "/unitmealmanager/index";
        }
        return "/unitmealmanager/index";

        //return "redirect:/msg";
    }

    @RequestMapping("/msg")
    public String weixinmsg(HttpServletRequest request,Model model){
        String msg = request.getParameter("msg");
        if (StringUtil.isNullOrEmpty(msg) || msg.equals("")){
            msg="您的消息已发送，我们会尽快处理,即将自动跳转首页";
        }
        //TODO 连接和消息
        model.addAttribute("msg",msg);
        model.addAttribute("href","http://localhost/wxindex");
        return "/unitmealmanager/msg";
    }

    @RequestMapping("/kefu")
    public String weixinkefu(HttpServletRequest request,Model model){
        return "/unitmealmanager/kefu";
    }
}
