package com.example.tuancan.controller;

import com.example.tuancan.convert.ConvertToDcdetailVO;
import com.example.tuancan.dto.DeliverDetailVO;
import com.example.tuancan.enums.StatusEnum;
import com.example.tuancan.model.*;
import com.example.tuancan.service.*;
import com.example.tuancan.utils.CookieUtil;
import com.example.tuancan.utils.JsonUtil;
import com.example.tuancan.utils.PageUtil;
import com.example.tuancan.utils.ResultUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


/**
 *
 * @author xiao
 */
@Slf4j
@Controller
@RequestMapping("deliveringMaster")
public class DeliveringMasterController {

    @Autowired
    private DeliveringMasterService deliveringMasterService;

    @Autowired
    private DeliveringDetailService detailService;

    @Autowired
    private GroupMealUnitService groupMealUnitService;

    @Autowired
    private GroupMealContractService groupMealContractService;


    @Autowired
    private DeliveringCompanyService deliveringCompanyService;

    public void pageFun(Model model,List<DeliveringMaster> deliveringMasters,String path){
        PageInfo<DeliveringMaster> pageInfo = new PageInfo<DeliveringMaster>(deliveringMasters);

        log.info(pageInfo.getPages()+"xx"+pageInfo.getNextPage()+"xx"+
                pageInfo.isIsFirstPage()+pageInfo.isHasPreviousPage());
        log.info(JsonUtil.toJson(pageInfo.getList()));
        PageUtil.setPageModel(model,pageInfo,path);
    }
    /*平台查看配送情况细节*/
    @RequestMapping(value = "/dm_details/{id}",method = {RequestMethod.GET})
    public String getOne(@PathVariable(value = "id")Integer id,Model model){
        detailFun(id,model);
        return "/manager/dm_update";
    }
    /*平台查看所有配送情况*/
    @RequestMapping(value = {"/list/{pagenum}","/list"})
    public String  rtlist(Model model, @PathVariable(value = "pagenum",required = false) Integer pageNum){
        if (pageNum==null||pageNum<=0){
            pageNum=1;
        }
        Page<Object> page = PageHelper.startPage(pageNum, 10);
        List<DeliveringMaster> deliveringMasters = deliveringMasterService.selectAllWithDeliverComAndMealUnit();
        pageFun(model,deliveringMasters,"/deliveringMaster/list");

        return "/manager/dm_list";
    }

    /**
     * 处理配送单细节
     * @param id
     * @param model
     */
    public void detailFun(Integer id,Model model){
        log.info(id+">>>>dmdetails");

        DeliveringMaster deliveringMaster = deliveringMasterService.selectOneById(id);
        List<DeliveringDetail> deliveringDetails = detailService.selectByDeliveringMasterId(id);
        List<DeliverDetailVO> deliverDetailVOS = ConvertToDcdetailVO.convertToDcdetailVO(deliveringDetails);

        log.info(JsonUtil.toJson(deliveringMaster));
        log.info(JsonUtil.toJson(deliverDetailVOS));
        model.addAttribute("details",deliverDetailVOS);
        model.addAttribute("dm",deliveringMaster);
    }

    /*用餐单位查看配送情况细节*/
    @RequestMapping(value = "/dm_unit_details/{id}",method = {RequestMethod.GET})
    public String getUnitdm(HttpServletRequest request, @PathVariable(value = "id")Integer id,Model model){
        //Integer unitID = (Integer) httpServletRequest.getSession().getAttribute("unitID");
        Integer unitID= CookieUtil.getSessionId(request);
        log.info("公司id:"+ unitID);
        detailFun(id,model);
        return "/unitmealmanager/dm_unit_update";
    }

    /*用餐单位查看配送情况*/
    @RequestMapping(value = {"/unitlist/{pagenum}","/unitlist"})
    public String unitDelivermaster(HttpServletRequest request,Model model, @PathVariable(value = "pagenum",required = false) Integer pageNum){
        /*获取登录的用餐公司id*/
        Integer unitID= CookieUtil.getSessionId(request);
        log.info("unitID>>>session>>"+unitID);
        if (pageNum == null || pageNum <= 0){
            pageNum=1;
        }
        Page<Object> page = PageHelper.startPage(pageNum, 1);

        List<DeliveringMaster> deliveringMasters = deliveringMasterService.selectByUnitId(unitID);
        pageFun(model,deliveringMasters,"/deliveringMaster/unitlist");

        GroupMealUnit groupMealUnit = groupMealUnitService.selectOneById(unitID);
        model.addAttribute("unitVO",groupMealUnit);

        return "/unitmealmanager/index.html :: #delivers";
    }

    /**
     * 新建应急配送单
     * @param deliveringMaster
     * @return
     */
    @RequestMapping(value = {"/emergency"})
    @ResponseBody
    public String emergencyMaster(@RequestBody DeliveringMaster deliveringMaster,HttpServletRequest request){
       /* String unitIDstr = String.valueOf(request.getSession().getAttribute("unitID"));
        if (Objects.isNull(unitIDstr)){
            return "login";
        }*/
        Integer unitID= CookieUtil.getSessionId(request);
        log.info("unitID>>>session>>"+unitID);
        deliveringMaster.setDeliveringMasterCreater("登陆用户");
        deliveringMaster.setDeliveringMasterIsEmergency(StatusEnum.ISEmergency.getCode());
        deliveringMaster.setDeliveringMasterCreatedate(new Date());
        deliveringMaster.setDeliveringMasterStatus(StatusEnum.MasterStatusNEW.getCode());
        GroupMealUnit unit = new GroupMealUnit();
        unit.setGroupMealUnitId(unitID);
        deliveringMaster.setGroupMealUnit(unit);
        List<GroupMealContract> mealContracts = groupMealContractService.selectOneByUnitId(unitID);
        DeliveringCompany deliveringCompany = mealContracts.get(0).getStandard().getDeliveringCompany();
        deliveringMaster.setDeliveringCompany(deliveringCompany);
        //需要对方处理的是确认人，配送单状态
        deliveringMasterService.insertOne(deliveringMaster);
        log.info(JsonUtil.toJson(deliveringMaster));
        return "";
    }

    /**
     * 确定配送单人数
     * @param id
     * @param num
     * @return
     */
    @RequestMapping(value = "updatenum",method = {RequestMethod.POST})
    @ResponseBody
    public String updateNum(@RequestParam(value = "id")Integer id,@RequestParam(value = "num")Integer num){
        int updateNumById = deliveringMasterService.updateNumById(id, num);

        return JsonUtil.toJson(ResultUtil.status(200,"确认成功！"));
    }

    /*团餐餐单位查看配送情况*/
    @RequestMapping(value = {"/deliverlist/{pagenum}","/deliverlist"})
    public String deliverDelivermaster(HttpServletRequest request,Model model, @PathVariable(value = "pagenum",required = false) Integer pageNum){
        /*获取登录的团餐餐公司id*/
        String dcnoStr = (String) request.getSession().getAttribute("dcno");
        log.info("dcno>>>session>>"+dcnoStr);
        Integer dcno=Integer.parseInt(dcnoStr);
        if (pageNum==null||pageNum<=0){
            pageNum=1;
        }
        Page<Object> page = PageHelper.startPage(pageNum, 10);

        List<DeliveringMaster> deliveringMasters = deliveringMasterService.selectBydcNo(dcno);
        pageFun(model,deliveringMasters,"/deliveringMaster/deliverlist");

        DeliveringCompany deliveringCompany = deliveringCompanyService.selectByIdWithGrade(dcno);
        model.addAttribute("dc",deliveringCompany);
        return "/unitmealmanager/dm_deliver_list";
    }
}
