package com.example.tuancan.service;

import com.example.tuancan.model.DeliveringMaster;

import java.util.Date;
import java.util.List;

public interface DeliveringMasterService {

    public DeliveringMaster selectOneById(Integer deliveringMasterId);

    public List<DeliveringMaster> selectAllWithDeliverComAndMealUnit();

    public List<DeliveringMaster> selectByUnitAndCompanyAndOrderByDeliverdate(Integer unitid, Integer companyid);

    public List<DeliveringMaster> selectByDeliverdate(Integer unitid,Integer companyid);
    /*通过用餐公司id查询*/
    public List<DeliveringMaster> selectByUnitId(Integer unitId);

    /*通过团餐id*/
    public List<DeliveringMaster> selectBydcNo(Integer dcNo);

    public List<DeliveringMaster> selectByIsEmergency(DeliveringMaster deliveringMaster);

    public int updateOne(DeliveringMaster deliveringMaster);

    public int updateNumById(Integer id,Integer num,String confirmer);

    public List<DeliveringMaster> selectByUnitAndCompanyAndByDeliverdate ( Integer unitid, Integer companyid, Date startTime, Date endTime);

    public int insertOne(DeliveringMaster deliveringMaster);

}
