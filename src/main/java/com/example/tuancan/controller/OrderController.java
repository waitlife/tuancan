package com.example.tuancan.controller;

import com.example.tuancan.config.ProjectUrlConfig;
import com.example.tuancan.convert.ConvertToCMV;
import com.example.tuancan.dto.CommendOrMealOrVegetable;
import com.example.tuancan.dto.Result;
import com.example.tuancan.model.*;
import com.example.tuancan.service.*;
import com.example.tuancan.utils.JsonUtil;
import com.example.tuancan.utils.PageUtil;
import com.example.tuancan.utils.ResultUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private GroupMealStaffService groupMealStaffService;

    @Autowired
    private TomorrowMenuDetailService tomorrowMenuDetailService;

    @Autowired
    private TomorrowMenuMasterService tomorrowMenuMasterService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    @Autowired
    private StaffOrderService staffOrderService;

    /**
     * 用餐员工查看个人订单情况
     * @param model
     * @param pageNum
     * @return
     */
    @RequestMapping(value = {"/unitorderlist/{pagenum}","/unitorderlist"})
    public String  unitcplist(HttpServletRequest request,Model model, @PathVariable(value = "pagenum",required = false) Integer pageNum){
        if (pageNum==null||pageNum<=0){
            pageNum=1;
        }
        //TODO 获取当前用户
        Page<Object> page = PageHelper.startPage(pageNum, 5);
        List<StaffOrder> staffOrders = staffOrderService.selectOneByStaffId(1);
        Map<Date, List<StaffOrder>> collect = staffOrders.stream().collect(Collectors.groupingBy(StaffOrder::getStaffOrderDate));

        PageInfo<StaffOrder> pageInfo = new PageInfo<StaffOrder>(staffOrders);
        model.addAttribute("ordermap",collect);
        log.info(JsonUtil.toJson(collect));
        PageUtil.setPageModel(model,pageInfo,"/order/unitorderlist");

        return "/unitmealmanager/order_list";
    }

    @RequestMapping(value = {"/save"},method = {RequestMethod.POST})
    @ResponseBody
    public Result save(@RequestParam(value = "recipeids") String recipeids){
        log.info(recipeids);
        String[] ids = recipeids.split(",");
        StaffOrder staffOrder;
        for (String id : ids) {
            staffOrder=new StaffOrder();
            staffOrder.setStaffOrderUsedate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()));
            staffOrder.setStaffOrderDate(new Date());
            Recipe recipe = new Recipe();
            recipe.setRecipeId(Integer.parseInt(id));
            staffOrder.setRecipe(recipe);
            //TODO 当前微信登录用户
            staffOrder.setGMStaff(groupMealStaffService.selectByOpenid("xxx123"));

            log.info(JsonUtil.toJson(staffOrder));

            int insertOne = staffOrderService.insertOne(staffOrder);
            log.info(insertOne+">>");
        }

        return ResultUtil.status(200,"msg");
    }

    @RequestMapping("/get")
    //@ResponseBody
    public String index( HttpServletRequest request, Model model){
        /*Cookie cookie = CookieUtil.get(request, "openid");
        String openid = cookie.getValue();*/
        //session
        request.getSession(true).setAttribute("openId","xxx123");
        String openId = (String) request.getSession(true).getAttribute("openId");
        log.info("openid:"+openId);
        if (StringUtils.isEmpty(openId)){
            //没有openid 重新授权
            return "redirect:"+projectUrlConfig.getWhchatAuthorize()+"/wechat/authorize";
        }
        //查公司
        GroupMealStaff groupMealStaff = groupMealStaffService.selectByOpenid(openId);
        Integer unitId = groupMealStaff.getGroupMealUnitId().getGroupMealUnitId();

        //查明日菜单主表
        List<TomorrowMenuMaster> tomorrowMenuMasters = tomorrowMenuMasterService.selectByUnitIdAndUseDateAndExpireDate(unitId);

        Integer menuId=tomorrowMenuMasters.get(tomorrowMenuMasters.size()-1).getTomorrowMenuMasterId();
        log.info("menuId:"+menuId);

        //通过主表id 查明日菜单明细表
        List<TomorrowMenudetail> tomorrowMenudetails = tomorrowMenuDetailService.selectByMenuMasterId(menuId);

        log.info("明日荤素菜单明细");
        log.info(JsonUtil.toJson(tomorrowMenudetails));

        DeliveringCompany deliveringCompany=recipeService.selectOneById(tomorrowMenudetails.get(0).getRecipe().getRecipeId()).getDeliveringCompanyNo();

        //装配配推荐，荤素菜
        CommendOrMealOrVegetable commendOrMealOrVegetable = ConvertToCMV.convertCommendOrMealOrVegetable(tomorrowMenudetails,deliveringCompany, groupMealStaff.getGroupMealUnitId());

        log.info(JsonUtil.toJson(commendOrMealOrVegetable));


        model.addAttribute("cmv",commendOrMealOrVegetable);
        return "/webhtml/order";
        //return "redirect:"+projectUrlConfig.getWhchatAuthorize()+"/order/getMeal";
    }

}
