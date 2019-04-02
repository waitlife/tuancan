package com.example.tuancan.controller;

import com.example.tuancan.dto.Result;
import com.example.tuancan.enums.StatusEnum;
import com.example.tuancan.model.DeliveringCompany;
import com.example.tuancan.model.DiningStandard;
import com.example.tuancan.model.GroupMealContract;
import com.example.tuancan.model.GroupMealUnit;
import com.example.tuancan.service.DeliveringCompanyService;
import com.example.tuancan.service.DiningStandardService;
import com.example.tuancan.service.GroupMealContractService;
import com.example.tuancan.utils.JsonUtil;
import com.example.tuancan.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author xiaoqy
 * @Date 2018/8/3
 * @Description 合同
 */
@Controller
@Slf4j
@RequestMapping("groupMealContract")
public class GroupMealContractController {

    @Autowired
    private GroupMealContractService groupMealContractService;

    @Autowired
    private DeliveringCompanyService deliveringCompanyService;

    @Autowired
    private DiningStandardService diningStandardService;

    @RequestMapping(value = {"/yes_list"})
    @ResponseBody
    public List<DeliveringCompany> dcStausYeslist(){
        List<DeliveringCompany> deliveringCompanies = deliveringCompanyService.selectAllByStatus(StatusEnum.StatusUP.getCode());
        return deliveringCompanies;
    }

    /**
     * 查询团餐公司设定的餐标列表
     * @param dcno
     * @return
     */
    @RequestMapping(value = "/getdining",method = {RequestMethod.POST})
    @ResponseBody
    public List<DiningStandard> getDiner(@RequestParam(value = "dcno")Integer dcno){

        List<DiningStandard> diningStandards = diningStandardService.selectAllByCompanyNo(dcno);

        return diningStandards;
    }

    /**
     *用餐公司查看合同信息
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/show",method = {RequestMethod.GET})
    public String showContract(Model model, HttpServletRequest request){
         /*获取登录的用餐公司id*/
        String unitIDstr = String.valueOf(request.getSession().getAttribute("unitID"));
        if (StringUtils.isEmpty(unitIDstr) || "".equals(unitIDstr)){
            return "redirect:/login";
        }
        log.info("unitID:{}>>>session>>",unitIDstr);
        Integer unitID=Integer.parseInt(unitIDstr);
        List<GroupMealContract> groupMealContracts = groupMealContractService.selectOneByUnitId(unitID);
        log.info("groupMealContracts:{}",groupMealContracts);
        if (CollectionUtils.isEmpty(groupMealContracts)){
            //以前未签订合同  直接展示空页面提示无合同信息
            //TODO
            return "/unitmealmanager/contract_details";

        }else {
            //有合同 直接展示合同页面
            Date expirydate = groupMealContracts.get(0).getGmlContractExpirydate();
            Date date = new Date();
            log.info("expirydate:{}>>now:{}",expirydate.getTime(),date.getTime());
            if(expirydate.getTime()>date.getTime()){
                //合同在有效期内 直接展示或者计算还有多久过期并提示
                Long datenum=(expirydate.getTime()-date.getTime())/(24*60*60*1000);
                model.addAttribute("msg","您的合同有效期还有"+datenum);
                model.addAttribute("gc",groupMealContracts.get(0));

            }else {
                //合同过期 页面提示过期
                //TODO
                return "redirect:/msg?msg="+"您的合同已过期";
            }
        }
        return "/unitmealmanager/contract_details";
    }


    /**
     * 用餐公司保存合同信息，状态待确认
     * @param model
     * @param groupMealContract
     * @param expireStr
     * @return
     */
    @RequestMapping(value = "/save",method = {RequestMethod.POST})
    @ResponseBody
    public Result setContract(HttpServletRequest httpServletRequest, Model model,
                              GroupMealContract groupMealContract,
                              @RequestParam(value = "expireStr")String expireStr){
        Integer unitID = (Integer) httpServletRequest.getSession().getAttribute("unitID");

        log.info(JsonUtil.toJson(groupMealContract));
        log.info("expireStr:{}",expireStr);
        List<GroupMealContract> groupMealContracts = groupMealContractService.selectOneByUnitId(unitID);
        if (CollectionUtils.isEmpty(groupMealContracts) || groupMealContracts.size() == 0){
            //以前未签订合同

            Date from = null;
            try {
                from = new SimpleDateFormat("yyyy-MM-dd").parse(expireStr);
                log.info("expireStr:{}",from);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //设置合同有效期
            groupMealContract.setGmlContractExpirydate(from);
            GroupMealUnit groupMealUnit = new GroupMealUnit();
            groupMealUnit.setGroupMealUnitId(unitID);
            groupMealContract.setGroupMealUnit(groupMealUnit);
            groupMealContract.setGmContractCreateDate(new Date());
            groupMealContract.setGmContractSigndate(new Date());
            groupMealContract.setGmlContractStatus(StatusEnum.STATUS_AVA.getCode());
            int insertOne = groupMealContractService.insertOne(groupMealContract);
            log.info("insertOne>>{}",JsonUtil.toJson(groupMealContract));
            //TODO websocket通知团餐公司确认合同信息
        }else {

        }

        return ResultUtil.status(200,"ok");
    }
}
