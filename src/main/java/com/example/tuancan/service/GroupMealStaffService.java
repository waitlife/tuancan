package com.example.tuancan.service;

import com.example.tuancan.model.GroupMealStaff;

import java.util.List;

public interface GroupMealStaffService {

    /*  根据id 查询单个结果*/
    public GroupMealStaff selectOneById(Integer id);

    /*  查询所有结果*/
    public List<GroupMealStaff> selectAll();

    /*查询公司对应的所有员工*/
    public List<GroupMealStaff> selectByUnitId(Integer id);

    /*根据name查询*/
    public List<GroupMealStaff> selectByName(String name,Integer id);

    /*根据状态查询*/
    public List<GroupMealStaff> selectByStatus(int status);

    /*查询是默认账号*/
    public List<GroupMealStaff> selectByIsDefault(int isdefault);

    /*根据登陆账号查询*/
    public GroupMealStaff selectByLoginName(String loginName);

    /*根据openid查询*/
    public GroupMealStaff selectByOpenid(String openid);

    /*根据ticket查询*/
    public GroupMealStaff selectByTicket(String ticket);

    /*创建时间正序*/
    public List<GroupMealStaff> selcetOrderByCreateTimeASC();

    /*创建时间逆序*/
    public List<GroupMealStaff> selcetOrderByCreateTimeDESC();

    /*插入数据*/
    public int insertOne(GroupMealStaff groupMealStaff);

    /*删除数据*/
    public int deleteOne(Integer id);

    /*更新数据*/
    public int updateOne(GroupMealStaff groupMealStaff);

    /**
     * 更新状态
     * @param id
     * @param status
     * @return
     */
    public int updateStatusById( Integer id,Integer status);

}
