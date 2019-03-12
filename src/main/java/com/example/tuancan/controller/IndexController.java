package com.example.tuancan.controller;

import com.example.tuancan.dao.ManagerMapper;
import com.example.tuancan.model.DeliveringCompanyStaff;
import com.example.tuancan.model.Manager;
import com.example.tuancan.service.DeliveringCompanyStaffService;
import com.example.tuancan.service.ManagerService;
import com.example.tuancan.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private DeliveringCompanyStaffService deliveringCompanyStaffService;

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


    @RequestMapping("/doLogin")
    public String doLogin(@RequestParam(name = "username") String name, @RequestParam(name = "password") String password,
                          Model model,HttpServletRequest httpServletRequest) {

        DeliveringCompanyStaff deliveringCompanyStaff = deliveringCompanyStaffService.selectStaffByName(name);
        if (deliveringCompanyStaff == null) {
            model.addAttribute("message", "密码或账号错误！");
            return "/groupmanager/login";
        }
        if (deliveringCompanyStaff.getDCompanyStaffStatus() == ENABLE_MANAGER) {
            if (StringUtil.stringEquals(deliveringCompanyStaff.getDCompanyStaffPassword(), password)) {
                httpServletRequest.getSession().setAttribute("companyId", deliveringCompanyStaff.getDeliveringCompany().getDeliveringCompanyNo());
                return "/groupmanager/index";
            } else {
                model.addAttribute("message", "密码或账号错误！");
                return "/groupmanager/login";
            }
        } else {
            model.addAttribute("message", "该账号已被停用！");
            return "redirect:/groupmanager/login";
        }
    }
}
