package com.example.tuancan.controller;

import com.example.tuancan.dto.Result;
import com.example.tuancan.enums.StatusEnum;
import com.example.tuancan.model.GroupMealStaff;
import com.example.tuancan.model.GroupMealUnit;
import com.example.tuancan.service.GroupMealStaffService;
import com.example.tuancan.utils.CookieUtil;
import com.example.tuancan.utils.JsonUtil;
import com.example.tuancan.utils.PageUtil;
import com.example.tuancan.utils.ResultUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
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
@Controller
@RequestMapping("/groupmealstaff")
public class GroupMealStaffController {

    @Resource
    private GroupMealStaffService groupMealStaffService;

    /*跳转到员工列表页面*/
    @RequestMapping(value = {"/manager/{pagenum}","/manager"},method = {RequestMethod.POST})
    public String getAllStaff(Model model,@PathVariable(value = "pagenum",required = false) Integer pageNum, HttpServletRequest httpServletRequest){
        //存储公司登陆信息
        //TODO 也许会用redis
        //httpServletRequest.getSession().setAttribute("unitID",2);
        //获取公司id
        if (pageNum==null||pageNum<=0){
            pageNum=1;
        }
        Page<Object> page = PageHelper.startPage(pageNum, 10);
        Integer companyId = CookieUtil.getSessionId(httpServletRequest);
        List<GroupMealStaff> groupMealStaffs = groupMealStaffService.selectByUnitId(companyId);

        PageInfo<GroupMealStaff> pageInfo = new PageInfo<GroupMealStaff>(groupMealStaffs);
        PageUtil.setPageModel(model,pageInfo,"/groupmealstaff/manager/"+(++pageNum));

        log.info(JsonUtil.toJson(groupMealStaffs));

         return "unitmealmanager/unitstaff_list.html :: #manager";
    }

    /*查询员工*/
    @RequestMapping(value = {"/search/{name}","/search/{name}/{pagenum}"},method = {RequestMethod.POST,RequestMethod.GET})
    public String searchStaffs(Model model,HttpServletRequest httpServletRequest,
                               @PathVariable(value = "name",required = false) String name,
                               @PathVariable(value = "pagenum",required = false) Integer pageNum,
                               @RequestParam(value = "asyc",required = false,defaultValue = "false")Boolean asyc){
        Integer companyId = CookieUtil.getSessionId(httpServletRequest);
        if (Objects.isNull(companyId)){
            return  JsonUtil.toJson(ResultUtil.status(401,"参数错误"));
        }
        log.info(name);
        //PageUtil.checkPageNumAndCriteria(pageNum,name);
        if (pageNum==null||pageNum<=0){
            pageNum=1;
        }
        if (StringUtils.isEmpty(name)||name.equals("*")){
            name="";
        }
        log.info("公司id:"+companyId + "账号:" + name);

        Page<Object> page = PageHelper.startPage(pageNum, 5);
        List<GroupMealStaff> groupMealStaffs = groupMealStaffService.selectByName(name,companyId);
        PageInfo<GroupMealStaff> pageInfo = new PageInfo<GroupMealStaff>(groupMealStaffs);

        log.info(JsonUtil.toJson(groupMealStaffs));
        PageUtil.setPageModel(model,pageInfo,"/groupmealstaff/search/"+name+"/"+(++pageNum));

        model.addAttribute("groupMealStaffs",groupMealStaffs);

        return "/unitmealmanager/unitstaff_list.html :: #manager";
    }
    /*添加员工*/
    @RequestMapping(value = "/addstaff",method = {RequestMethod.POST})
    @ResponseBody
    public Result addStaff(HttpServletRequest httpServletRequest, Model model
            ,@RequestParam(value = "gMStaffName")String staffname
            ,@RequestParam(value = "gMStaffSex")String sex,@RequestParam(value = "gMStafMobile")String mobile){

        log.info(JsonUtil.toJson(httpServletRequest.getParameterMap()));
        Integer companyId = CookieUtil.getSessionId(httpServletRequest);
        log.info("公司id:"+ companyId  );
        GroupMealStaff groupMealStaff=new GroupMealStaff();
        GroupMealUnit groupMealUnit = new GroupMealUnit();
        groupMealUnit.setGroupMealUnitId(companyId);

        groupMealStaff.setGMStaffName(staffname);
        groupMealStaff.setGMStaffSex(sex);
        groupMealStaff.setGMStafMobile(mobile);
        groupMealStaff.setGroupMealUnitId(groupMealUnit);
        groupMealStaff.setGMStafIsdefualt(StatusEnum.IsDefaultAcoount.getCode());
        groupMealStaff.setGMStafPassword("123456");
        groupMealStaff.setGMStafCreateDate(new Date());
        groupMealStaff.setGMStafLoginname(groupMealStaff.getGMStaffName());

        groupMealStaffService.insertOne(groupMealStaff);
        log.info(JsonUtil.toJson(groupMealStaff));
        return ResultUtil.status(200,"");
    }

    /*更改状态*/
    @RequestMapping("/updatestatus/{id}")
    @ResponseBody
    public Result updateStaffStatus(@PathVariable("id")Integer id){
        log.info("公司id:"+id );
        GroupMealStaff groupMealStaff = groupMealStaffService.selectOneById(id);
        Integer staffStatus = groupMealStaff.getGMStaffStatus();
        if(staffStatus.equals(1)){
            groupMealStaff.setGMStaffStatus(StatusEnum.StatusDOWN.getCode());
        }else {
            groupMealStaff.setGMStaffStatus(StatusEnum.STATUS_AVA.getCode());

        }
        groupMealStaffService.updateStatusById(id,groupMealStaff.getGMStaffStatus());
        return ResultUtil.status(200,"更改成功！");

    }
}
