package com.example.tuancan.service.impl;

import com.example.tuancan.model.DeliveringCompany;
import com.example.tuancan.model.DeliveringMaster;
import com.example.tuancan.model.GroupMealUnit;
import com.example.tuancan.service.DeliveringMasterService;
import com.example.tuancan.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DeliveringMasterServiceImplTest {

    @Autowired
    private DeliveringMasterService service;

    @Test
    public void selectOneById() throws Exception {
        DeliveringMaster deliveringMaster = new DeliveringMaster();
        deliveringMaster.setDeliveringMaster_id(1);
        DeliveringMaster deliveringMaster1 = service.selectOneById(deliveringMaster);
        log.info(JsonUtil.toJson(deliveringMaster1));
        System.out.println(deliveringMaster1);
    }

    @Test
    public void selectAllWithDeliverComAndMealUnit() throws Exception {

        List<DeliveringMaster> deliveringMasters = service.selectAllWithDeliverComAndMealUnit();
        log.info(JsonUtil.toJson(deliveringMasters));
    }

    @Test
    public void updateOne() throws Exception {
        DeliveringMaster deliveringMaster = new DeliveringMaster();

        DeliveringCompany deliveringCompany = new DeliveringCompany();
        deliveringCompany.setDeliveringCompanyNo(1);

        GroupMealUnit groupMealUnit = new GroupMealUnit();
        groupMealUnit.setGroupMealUnitId(1);

        deliveringMaster.setDeliveringMaster_id(1);
        deliveringMaster.setDeliveringCompany(deliveringCompany);
        deliveringMaster.setGroupMealUnit(groupMealUnit);
        deliveringMaster.setDeliveringMaster_amount(15);
        deliveringMaster.setDeliveringMaster_price(new BigDecimal(12.55));
        deliveringMaster.setDeliveringMaster_confirmer("xiao");
        deliveringMaster.setDeliveringMaster_creater("wang");
        deliveringMaster.setDeliveringMaster_isEmergency(1);
        deliveringMaster.setDeliveringMaster_memo("jiandandian");
        deliveringMaster.setDeliveringMaster_status(1);

        int i = service.updateOne(deliveringMaster);
        System.out.println(i);
        System.out.println(JsonUtil.toJson(deliveringMaster));
    }

    @Test
    public void insertOne() throws Exception {
        DeliveringMaster deliveringMaster = new DeliveringMaster();

        DeliveringCompany deliveringCompany = new DeliveringCompany();
        deliveringCompany.setDeliveringCompanyNo(1);

        GroupMealUnit groupMealUnit = new GroupMealUnit();
        groupMealUnit.setGroupMealUnitId(1);

        deliveringMaster.setDeliveringCompany(deliveringCompany);
        deliveringMaster.setGroupMealUnit(groupMealUnit);
        deliveringMaster.setDeliveringMaster_amount(10);
        deliveringMaster.setDeliveringMaster_price(new BigDecimal(12.50));
        deliveringMaster.setDeliveringMaster_confirmer("xiao");
        deliveringMaster.setDeliveringMaster_creater("wang");
        deliveringMaster.setDeliveringMaster_isEmergency(0);
        deliveringMaster.setDeliveringMaster_memo("jiandandian");
        deliveringMaster.setDeliveringMaster_status(0);


        int i = service.insertOne(deliveringMaster);
        System.out.println(i);
        System.out.println(JsonUtil.toJson(deliveringMaster));
    }

}