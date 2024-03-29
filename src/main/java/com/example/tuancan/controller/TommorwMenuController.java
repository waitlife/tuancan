package com.example.tuancan.controller;

import com.example.tuancan.config.ProjectUrlConfig;
import com.example.tuancan.convert.ConvertToCMV;
import com.example.tuancan.dto.CommendOrMealOrVegetable;
import com.example.tuancan.dto.UnitAndStandard;
import com.example.tuancan.enums.StatusEnum;
import com.example.tuancan.model.*;
import com.example.tuancan.service.*;
import com.example.tuancan.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/tomorrowmenu")
public class TommorwMenuController {

    @Autowired
    private DiningStandardService diningStandardService;

    @Autowired
    private GroupMealContractService groupMealContractService;

    @Autowired
    private GroupMealMenumasterService groupMealMenumasterService;

    @Autowired
    private MenuDetailService menuDetailService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private TomorrowMenuMasterService tomorrowMenuMasterService;

    @Autowired
    private TomorrowMenuDetailService tomorrowMenuDetailService;

    @Autowired
    private GroupMealStaffService groupMealStaffService;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    @RequestMapping("/menu")
    public String menuAll(HttpServletRequest httpServletRequest, Model model){

        Integer companyId = (Integer) httpServletRequest.getSession().getAttribute("companyId");
        log.info("配送公司编号：" +companyId);
        List<DiningStandard> diningStandards = diningStandardService.selectAllByCompanyNo(companyId);
        List<UnitAndStandard> unit_Company = new ArrayList<>();
        for(DiningStandard standard : diningStandards){
            Integer standardId = standard.getStandardId();
            List<GroupMealContract> groupMealContracts = groupMealContractService.selectOneByReipeId(standardId);

            for(GroupMealContract groupMealContract : groupMealContracts){

                /*合同基本信息*/
                Integer unit_id = groupMealContract.getGroupMealUnit().getGroupMealUnitId();
                UnitAndStandard unitAndStandard = new UnitAndStandard();
                unitAndStandard.setHun_number(groupMealContract.getGmContractMeatnumber());
                unitAndStandard.setSu_number(groupMealContract.getGmlContractVegetablenumber());
                unitAndStandard.setUnitID(unit_id);
                unitAndStandard.setUnitName(groupMealContract.getGroupMealUnit().getGroupMealUnitName());
                /*是否已经添加 0：未添加 1：已添加*/
                unitAndStandard.setAdded(0);

                try {
                    /*防止数据库不存在此记录，抛出异常*/
                    List<TomorrowMenuMaster> tomorrowMenuMasters = tomorrowMenuMasterService.selectByUnitId(unit_id);
                    TomorrowMenuMaster tomorrowMenuMaster1 = tomorrowMenuMasters.get(0);

                    Date tomorrowMenuMasterUsedate = tomorrowMenuMaster1.getTomorrowMenuMasterUsedate();
                    if(tomorrowMenuMasterUsedate.getDate() == new Date().getDate() + 1) {
                        unitAndStandard.setAdded(1);
                    }
                }catch (Exception e){
                    log.info("可能不存在某条记录，导致数据溢出!");
                }


                /*荤素基本信息*/

                GroupMealMenumaster groupMealMenumaster = groupMealMenumasterService.selectByUnitId(unit_id);
                List<MenuDetail> menuDetails = menuDetailService.selectByMasterNo(groupMealMenumaster.getGroupMealMenumasterId());

                Map<Integer,String> huncai_number = new HashMap<>();
                Map<Integer,String> sucai_number = new HashMap<>();

//                /*循环得到餐谱细节 添加到具体的分类中*/
                for(MenuDetail menuDetail : menuDetails){
                    Recipe recipe = recipeService.selectOneById(menuDetail.getRecipe().getRecipeId());
                    if(recipe.getRecipeMeatOrVegetable() == StatusEnum.Meat.getCode()){
                        huncai_number.put(recipe.getRecipeId(),recipe.getRecipeName());
                    }else{
                        sucai_number.put(recipe.getRecipeId(),recipe.getRecipeName());
                    }
                    unitAndStandard.setHuncai(huncai_number);
                    unitAndStandard.setSucai(sucai_number);
                }
                unit_Company.add(unitAndStandard);
                }
            }
        model.addAttribute("mealUnit",unit_Company);
        return "/groupmanager/tomorrowmenu";
    }


    @ResponseBody
    @RequestMapping("/addmenu")
    public String addMenu(@RequestParam("unitid")String unitid,@RequestParam("foodsid") String foodsid){
        log.info("用餐id:"+unitid +" foodsId:"+foodsid);
        String[] foods = foodsid.split(",");

        /*数据库的明日菜单主表的写入*/
        TomorrowMenuMaster tomorrowMenuMaster = new TomorrowMenuMaster();
        GroupMealUnit groupMealUnit = new GroupMealUnit();
        groupMealUnit.setGroupMealUnitId(Integer.parseInt(unitid));
        tomorrowMenuMaster.setGroupMealUnit(groupMealUnit);
        Date date = new Date();
        /*此处过期时间都是 设置为24小时的*/
        tomorrowMenuMaster.setTomorrowMenuMasterUsedate(new Date(date.getTime() +  +24*60*60*1000));
        tomorrowMenuMaster.setTomorrowMenuMasterExpiredate(new Date(date.getTime() +  +24*60*60*1000));
        tomorrowMenuMaster.setTomorrowMenuMasterStatus(1);
        tomorrowMenuMasterService.insertOne(tomorrowMenuMaster);


        /*这是明日菜单详情表的写入*/
        //要获取最新的数据的id用于 副表
        List<TomorrowMenuMaster> tomorrowMenuMasters = tomorrowMenuMasterService.selectByUnitId(Integer.parseInt(unitid));
        /*获取到的最新数据的id*/
        TomorrowMenuMaster tomorrowMenuMaster1 = tomorrowMenuMasters.get(0);
        TomorrowMenudetail tomorrowMenudetail = new TomorrowMenudetail();

        tomorrowMenudetail.setTomorrowMenuMaster(tomorrowMenuMaster1);

         /*遍历所有的食谱*/
         for (String food : foods){
             Recipe recipe = new Recipe();
             recipe.setRecipeId(Integer.parseInt(food));
             tomorrowMenudetail.setRecipe(recipe);
             double v = Math.random() * 10 + 1;
             /*随机推荐 ，有需要再更换*/
             if(v > 5){
                 tomorrowMenudetail.setTomorrowMenuIsRecommend(1);
             }
             else {
                 tomorrowMenudetail.setTomorrowMenuIsRecommend(0);
             }
             tomorrowMenuDetailService.insertOne(tomorrowMenudetail);
         }
        return "5255";
    }

    /**
     * 查询明日菜单
     * @param request
     * @return
     */
    @RequestMapping(value = {"/show"},method = {RequestMethod.GET})
    public String showTomorrowMenu(HttpServletRequest request,Model model){
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

        if (Objects.isNull(tomorrowMenuMasters) || tomorrowMenuMasters.size() == 0){
            return "再无数据";
        }
        Integer menuId=tomorrowMenuMasters.get(0).getTomorrowMenuMasterId();
        log.info("menuId:"+menuId);

        //通过主表id 查明日菜单明细表
        List<TomorrowMenudetail> tomorrowMenudetails = tomorrowMenuDetailService.selectByMenuMasterId(menuId);

        if (Objects.isNull(tomorrowMenudetails) || tomorrowMenudetails.size() == 0){
            return "";
        }

        log.info("明日荤素菜单明细");
        log.info(JsonUtil.toJson(tomorrowMenudetails));

        DeliveringCompany deliveringCompany=recipeService.selectOneById(tomorrowMenudetails.get(0).getRecipe().getRecipeId()).getDeliveringCompanyNo();

        //装配配推荐，荤素菜
        CommendOrMealOrVegetable commendOrMealOrVegetable = ConvertToCMV.convertCommendOrMealOrVegetable(tomorrowMenudetails,deliveringCompany, groupMealStaff.getGroupMealUnitId());

        log.info(JsonUtil.toJson(commendOrMealOrVegetable));


        model.addAttribute("cmv",commendOrMealOrVegetable);
        return "unitmealmanager/order.html :: #ordercontent";
    }

}
