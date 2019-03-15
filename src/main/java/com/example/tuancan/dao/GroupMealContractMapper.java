package com.example.tuancan.dao;

import com.example.tuancan.model.DiningStandard;
import com.example.tuancan.model.GroupMealContract;
import com.example.tuancan.model.GroupMealUnit;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface GroupMealContractMapper  extends Mapper<GroupMealContract>{

    /**
     * 根据id 查询合同
     * @param id
     * @return
     */
    @Select({"select * from groupmealcontract where GMContract_id=#{id}"})
    @Results(id="selectOne",value = {
            @Result(column = "Standard_id",property = "standard",javaType = DiningStandard.class,
                    one = @One(select = "com.example.tuancan.dao.DiningStandardMapper.selectOneWithCompany")),
            @Result(column = "GroupMealUnit_id",property = "groupMealUnit",javaType = GroupMealUnit.class,
                    one = @One(select = "com.example.tuancan.dao.GroupMealUnitMapper.selectByPrimaryKey"))
    })
    public GroupMealContract selectOneById(Integer id);


    /*根据单位编号查询数据*/
    @Select("select * from  groupmealcontract where GroupMealUnit_id=#{id} and GMlContract_status=1")
    @ResultMap(value = "selectOne")
    public List<GroupMealContract> selectOneByUnitId(Integer id);


    /*根据餐标查询数据*/
    @Select("select * from groupmealcontract where Standard_id=#{id}")
    @ResultMap(value = "selectOne")
    public List<GroupMealContract> selectOneByReipeId(Integer id);

    /**
     * 查询合同所有数据
     * @return
     */
    @Select({"select * from groupmealcontract"})
    public List<GroupMealContract> findAll();

    /**
     * 根据状态查询所有合同
     * @param status
     * @return
     */
    @Select({"select * from groupmealcontract where GMlContract_status=#{status}"})
    public List<GroupMealContract> selectByStatus(int status);

    /**
     * 查询在有效期内的合同
     * @return
     */
    @Select({"select * from groupmealcontract HAVING NOW() < GMlContract_expirydate"})
    public List<GroupMealContract> selectByExpireDate();

    /**
     * 新建合同数据
     * @param groupMealContract
     * @return
     */
    @Insert({"insert into groupmealcontract values(null,#{standard.standardId},#{groupMealUnit.groupMealUnitId},#{gmlContractName}," +
            "#{gmlContractDisc},#{gmlContractExpirydate},#{gmContractMeatnumber},#{gmlContractVegetablenumber}," +
            "#{gmlContractStatus},#{gmContractSigndate},#{gmlContractGroupA},#{gmlContractGroupB},#{gmContractCreateDate})"})
    @Options(useGeneratedKeys = true,keyColumn = "GMContract_id",keyProperty = "gmContractId")
    public int insertOne(GroupMealContract groupMealContract);


    /**
     * 更新合同数据
     * @param groupMealContract
     * @return
     */
    @Update({"update groupmealcontract set Standard_id=#{standard.standardId},GroupMealUnit_id=#{groupMealUnit.groupMealUnitId},GMlContract_name=#{gmlContractName}," +
            "GMlContract_disc=#{gmlContractDisc},GMlContract_expirydate=#{gmlContractExpirydate},gmContract_meatnumber=#{gmContractMeatnumber},GMlContract_vegetablenumber=#{gmlContractVegetablenumber}," +
            "GMlContract_status=#{gmlContractStatus} where GMContract_id=#{gmContractId}"})
    @Options(useGeneratedKeys = true,keyColumn = "GMContract_id",keyProperty = "gmContractId")
    public int updateOne(GroupMealContract groupMealContract);

    @Update({"update groupmealcontract set GMlContract_status=#{gmlContractStatus} where GMContract_id=#{gmContractId}"})
    public int updateByStatus(@Param("gmlContractStatus") Integer status,@Param("gmContractId") Integer id);

}
