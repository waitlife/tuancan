package com.example.tuancan.dao;

import com.example.tuancan.model.DeliveringCompany;
import com.example.tuancan.model.DeliveringMaster;
import com.example.tuancan.model.GroupMealUnit;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

@Repository
public interface DeliveringMasterMapper extends Mapper<DeliveringMaster>{

    /**
     * 通过id 查询配送单信息 伴随团餐机构和用餐机构信息
     * @return
     */
    @Select({"select * from deliveringmaster where DeliveringMaster_id=#{deliveringMasterId}"})
    @Results(id = "selectOneById",value = {
            @Result(column = "GroupMealUnit_id",property = "groupMealUnit",javaType = GroupMealUnit.class,
            one = @One(select = "com.example.tuancan.dao.GroupMealUnitMapper.selectByPrimaryKey")),
            @Result(column = "DeliveringCompany_no",property = "deliveringCompany",javaType = DeliveringCompany.class,
            one = @One(select = "com.example.tuancan.dao.DeliveringCompanyMapper.selectByPrimaryKey"))
    })
    public DeliveringMaster selectOneById(@Param("deliveringMasterId") Integer deliveringMasterId);

    /**
     * 通过是否应急查询配送单信息 伴随团餐机构和用餐机构信息
     * @param deliveringMaster
     * @return
     */
    @Select({"select * from deliveringmaster where DeliveringMaster_isEmergency=#{DeliveringMasterIsEmergency}"})
    @ResultMap(value = "selectOneById")
    public List<DeliveringMaster> selectByIsEmergency(DeliveringMaster deliveringMaster);

    /*时间倒序查询所有*/
    @Select({"select * from deliveringmaster where GroupMealUnit_id=#{unitid} and DeliveringCompany_no = #{companyid} order by DeliveringMaster_delivedate DESC"})
    public List<DeliveringMaster> selectByDeliverdate(@Param("unitid") Integer unitid,@Param("companyid")Integer companyid);

    /*时间范围搜索*/
    @Select({"select * from deliveringmaster where GroupMealUnit_id=#{unitid} and DeliveringCompany_no = #{companyid} and DeliveringMaster_delivedate > #{startTime} and DeliveringMaster_delivedate < #{endTime}"})
    public List<DeliveringMaster> selectByUnitAndCompanyAndByDeliverdate(@Param("unitid") Integer unitid, @Param("companyid")Integer companyid, @Param("startTime") Date startTime, @Param("endTime")Date endTime);

    @Select({"select * from deliveringmaster where GroupMealUnit_id=#{unitid} and DeliveringCompany_no = #{companyid} order by DeliveringMaster_delivedate DESC"})
    @ResultMap(value = "selectOneById")
    public List<DeliveringMaster> selectByUnitAndCompanyAndOrderByDeliverdate(@Param("unitid") Integer unitid,@Param("companyid")Integer companyid);
    /**
     * 查询送单信息数据集 伴随团餐机构和用餐机构信息
     * @return
     */
    @Select({"select * from deliveringmaster order by DeliveringMaster_delivedate"})
    @ResultMap(value = "selectOneById")
    public List<DeliveringMaster> selectAllWithDeliverComAndMealUnit();

    /**
     * 更新数据
     * @param deliveringMaster
     * @return
     */
    @Update({"update deliveringmaster set GroupMealUnit_id=#{groupMealUnit.groupMealUnitId},DeliveringCompany_no=#{deliveringCompany.deliveringCompanyNo}," +
            "DeliveringMaster_delivedate=#{deliveringMasterDelivedate},DeliveringMaster_price=#{deliveringMasterPrice},DeliveringMaster_amount=#{deliveringMasterAmount}," +
            "DeliveringMaster_memo=#{deliveringMasterMemo},DeliveringMaster_status=#{deliveringMasterStatus},DeliveringMaster_isEmergency=#{deliveringMasterIsEmergency}," +
            "DeliveringMaster_creater=#{deliveringMasterCreater},DeliveringMaster_confirmer=#{deliveringMasterConfirmer} where DeliveringMaster_id=#{deliveringMasterId}"})
    public int updateOne(DeliveringMaster deliveringMaster);


    /**
     * 更新数量
     * @param id
     * @param num
     * @return
     */
    @Update({"update deliveringmaster set DeliveringMaster_amount=#{num},DeliveringMaster_status=1,DeliveringMaster_confirmer=#{confirmer} where DeliveringMaster_id=#{id}"})
    public int updateNumById(@Param("id") Integer id,@Param("num") Integer num,@Param("confirmer")String confirmer);

    /*通过用餐公司id查询*/
    @Select({"select * from deliveringmaster where GroupMealUnit_id=#{unitId} order by DeliveringMaster_delivedate desc"})
    @Results(id = "selectByUnitId",value = {
            @Result(column = "DeliveringCompany_no",property = "deliveringCompany",javaType = DeliveringCompany.class,
                    one = @One(select = "com.example.tuancan.dao.DeliveringCompanyMapper.selectByPrimaryKey"))
    })
    public List<DeliveringMaster> selectByUnitId(@Param("unitId") Integer unitId);

    /*通过团餐id*/
    @Select({"select * from deliveringmaster where DeliveringCompany_no=#{dcNo}"})
    @Results(id = "selectBydcNo",value = {
            @Result(column = "GroupMealUnit_id",property = "groupMealUnit",javaType = GroupMealUnit.class,
                    one = @One(select = "com.example.tuancan.dao.GroupMealUnitMapper.selectByPrimaryKey"))
    })
    public List<DeliveringMaster> selectBydcNo(@Param("dcNo") Integer dcNo);
    /**
     * 插入一条数据
     * @param deliveringMaster
     * @return
     */
    @Insert({"insert into deliveringmaster values(null,#{groupMealUnit.groupMealUnitId},#{deliveringCompany.deliveringCompanyNo}," +
            "#{deliveringMasterDelivedate},#{deliveringMasterPrice},#{deliveringMasterAmount},#{deliveringMasterMemo}," +
            "#{deliveringMasterStatus},#{deliveringMasterIsEmergency},#{deliveringMasterCreatedate},#{deliveringMasterCreater}," +
            "#{deliveringMasterConfirmer})"
            })
    @Options(useGeneratedKeys = true,keyProperty = "deliveringMasterId",keyColumn = "DeliveringMaster_id")
    public int insertOne(DeliveringMaster deliveringMaster);
}
