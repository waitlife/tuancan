package com.example.tuancan.controller;

import com.example.tuancan.enums.StatusEnum;
import com.example.tuancan.model.GroupMealStaff;
import com.example.tuancan.model.GroupMealUnit;
import com.example.tuancan.service.GroupMealStaffService;
import com.example.tuancan.utils.JsonUtil;
import com.example.tuancan.utils.PageUtil;
import com.example.tuancan.utils.ResultUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author xiaoqianyong
 * @description
 * @create 2019-03-12-9:06
 */
@Slf4j
@RestController
@RequestMapping("/groupmealstaff")
public class GroupMealStaffController {

    @Resource
    private GroupMealStaffService groupMealStaffService;

    /*跳转到员工列表页面*/
    @RequestMapping("/manager")
    public String getAllStaff(Model model, HttpServletRequest httpServletRequest){
        //存储公司登陆信息
        //TODO 也许会用redis
        httpServletRequest.getSession().setAttribute("unitID",2);
        //获取公司id
        Integer companyId = (Integer) httpServletRequest.getSession().getAttribute("unitID");
        List<GroupMealStaff> groupMealStaffs = groupMealStaffService.selectByUnitId(companyId);

        //model.addAttribute("staff_list",groupMealStaffs);
        JsonUtil.toJson(groupMealStaffs);
        return "/groupmanager/companystaff_list";
    }

    /*查询员工*/
    @RequestMapping(value = {"/search/{name}","/search/{name}/{pagenum}"},method = {RequestMethod.POST,RequestMethod.GET})
    public String searchStaffs(Model model,HttpServletRequest httpServletRequest,
                               @PathVariable(value = "name",required = false) String name,
                               @PathVariable(value = "pagenum",required = false) Integer pageNum,
                               @RequestParam(value = "asyc",required = false,defaultValue = "false")Boolean asyc){
        Integer companyId = (Integer) httpServletRequest.getSession().getAttribute("unitID");
        if (Objects.isNull(companyId)){
            return  JsonUtil.toJson(ResultUtil.status(400,"参数错误"));
        }
        log.info(name);
        //PageUtil.checkPageNumAndCriteria(pageNum,name);
        if (pageNum==null||pageNum<=0){
            pageNum=1;
        }
        if (StringUtils.isEmpty(name)||name.equals("*") || name.equals("")){
            name="";
        }
        log.info("公司id:"+companyId + "账号:" + name);

        Page<Object> page = PageHelper.startPage(pageNum, 10);
        List<GroupMealStaff> groupMealStaff = groupMealStaffService.selectByName(name,companyId);
        PageInfo<GroupMealStaff> pageInfo = new PageInfo<GroupMealStaff>(groupMealStaff);

        log.info(JsonUtil.toJson(groupMealStaff));
        PageUtil.setPageModel(model,pageInfo,"/groupMealUnit/search/");

        model.addAttribute("groupMealStaffs",groupMealStaff);
        if (!asyc){
            return "/manager/gmu_list";
        }
        return "/manager/gmu_list :: #searchtable";
    }
    /*添加员工*/
    @RequestMapping("/addstaff")
    public String addStaff(HttpServletRequest httpServletRequest,GroupMealStaff groupMealStaff,@RequestParam("name") String name, Model model){
        Integer companyId = (Integer) httpServletRequest.getSession().getAttribute("unitID");
        log.info("公司id:"+ companyId + " name:"+name );
        GroupMealStaff mealStaff = new GroupMealStaff();

        GroupMealUnit groupMealUnit = new GroupMealUnit();
        groupMealUnit.setGroupMealUnitId(companyId);

        mealStaff.setGMStaffName(groupMealStaff.getGMStaffName());
        mealStaff.setGMStaffSex(groupMealStaff.getGMStaffSex());
        mealStaff.setGMStafMobile(groupMealStaff.getGMStafMobile());
        mealStaff.setGroupMealUnitId(groupMealUnit);
        mealStaff.setGMStafIsdefualt(StatusEnum.IsDefaultAcoount.getCode());
        mealStaff.setGMStafPassword("123456");
        mealStaff.setGMStafCreateDate(new Date());
        mealStaff.setGMStafLoginname(groupMealStaff.getGMStaffName());

        groupMealStaffService.insertOne(mealStaff);
        JsonUtil.toJson(mealStaff);
        return "redirect:/companystaff/manager";
    }

    /*更改状态*/
    @RequestMapping("/updatestatus")
    public String updateStaffStatus(@RequestParam("id")Integer id,@RequestParam("name")String name){
        log.info("公司id:"+id + " 账号:" + name );
        GroupMealStaff groupMealStaff = groupMealStaffService.selectOneById(id);
        int staffStatus = groupMealStaff.getGMStaffStatus();
        if(staffStatus == 1){
            groupMealStaff.setGMStaffStatus(StatusEnum.StatusDOWN.getCode());
        }else {
            groupMealStaff.setGMStaffStatus(StatusEnum.STATUS_AVA.getCode());

        }
        groupMealStaffService.updateStatusById(id,groupMealStaff.getGMStaffStatus());
        return "redirect:/companystaff/manager";

    }
}
