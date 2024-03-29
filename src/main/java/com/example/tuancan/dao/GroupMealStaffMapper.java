package com.example.tuancan.dao;

import com.example.tuancan.model.GroupMealStaff;
import com.example.tuancan.model.GroupMealUnit;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Repository
public interface GroupMealStaffMapper extends Mapper<GroupMealStaff>{

    /*  根据id 查询单个结果*/
    @Select("select * from groupmealstaff where GMStaff_id =#{id}")
    @Results(id="getone",value = {
            @Result(column = "GroupMealUnit_id",property = "groupMealUnitId",javaType = GroupMealUnit.class,
                    one = @One(select = "com.example.tuancan.dao.GroupMealUnitMapper.selectByPrimaryKey")),
            })
    public GroupMealStaff selectOneById(Integer id);

    /*查询所有员工*/
    @Select("select * from groupmealstaff ")
    @ResultMap(value = "getone")
    public List<GroupMealStaff> gettAll();

    /*查询公司对应的所有员工*/
    @Select("select * from groupmealstaff where GroupMealUnit_id = #{id}")
    @ResultMap(value = "getone")
    public List<GroupMealStaff> selectByUnitId(Integer id);

    /*根据name查询*/
    @Select("select * from groupmealstaff where GMStaff_name like '%${value}%' and GroupMealUnit_id=#{id}")
    public List<GroupMealStaff> selectByName(@Param("value") String name,@Param("id") Integer id);

    /*根据状态查询*/
    @Select("select * from groupmealstaff where GMStaff_status = #{status}")
    @ResultMap(value = "getone")
    public List<GroupMealStaff> selectByStatus(int status);

    /*查询是默认账号*/
    @Select("select * from groupmealstaff where GMStaf_isdefualt = #{isdefault}")
    @ResultMap(value = "getone")
    public List<GroupMealStaff> selectByIsDefault(int isdefault);

    /*根据登陆账号查询*/
    @Select("select * from groupmealstaff where GMStaf_loginname = #{loginName}")
    @ResultMap(value = "getone")
    public GroupMealStaff selectByLoginName(String loginName);

    /*根据openid查询*/
    @Select("select * from groupmealstaff where GMStaff_OpenId = #{openid}")
    @ResultMap(value = "getone")
    public GroupMealStaff selectByOpenid(String openid);

    /*根据ticket查询*/
    @Select("select * from groupmealstaff where Unit_ticker_id = #{ticket}")
    @ResultMap(value = "getone")
    public GroupMealStaff selectByTicket(String ticket);

    /*创建时间正序*/
    @Select("select * from groupmealstaff order by GMStaf_createDate ASC")
    @ResultMap(value = "getone")
    public List<GroupMealStaff> selcetOrderByCreateTimeASC();

    /*创建时间逆序*/
    @Select("select * from groupmealstaff order by GMStaf_createDate DESC")
    @ResultMap(value = "getone")
    public List<GroupMealStaff> selcetOrderByCreateTimeDESC();

    /*增加数据*/
    @Insert("insert into groupmealstaff values(null,#{groupMealUnitId.groupMealUnitId},#{gMStafMobile},#{gMStaffName}" +
            ",#{gMStaffStatus},#{gMStaffSex},#{gMStafIsdefualt},#{gMStafLoginname}," +
            "#{gMStafPassword},#{unitTickerId},#{gMStaffOpenId},#{gMStafCreateDate})")
    @Options( useGeneratedKeys = true,keyColumn = "GMStaff_id",keyProperty = "gMStaffId")
    public int insertOne(GroupMealStaff groupMealStaff);

    /*删除数据*/
    @Delete("delete  from groupmealstaff where GMStaff_id = #{id} ")
    public int deleteOneById(Integer id);


    /*更新数据*/
    @Update("update  groupmealstaff set GroupMealUnit_id = #{groupMealUnitId.groupMealUnitId},GMStaf_Mobile = #{gMStafMobile},GMStaff_name = #{gMStaffName}" +
            ",GMStaff_status = #{gMStaffStatus},GMStaff_sex = #{gMStaffSex},GMStaf_isdefualt = #{gMStafIsdefualt},GMStaff_sex= #{gMStaffSex},GMStaf_isdefualt = #{gMStafIsdefualt}," +
            "GMStaf_loginname = #{gMStafLoginname},GMStaf_password = #{gMStafPassword},Unit_ticker_id = #{unitTickerId},GMStaff_OpenId = #{gMStaffOpenId},GMStaf_createDate = #{gMStafCreateDate} where GMStaff_id = #{gMStaffId}")

    public int updateOne(GroupMealStaff groupMealStaff);

    /*更新数据*/
    @Update("update  groupmealstaff set GMStaff_status = #{gMStaffStatus} where GMStaff_id = #{id}")
    public int updateStatusById(@Param("id") Integer id,@Param("gMStaffStatus") Integer status);
}
