package com.example.tuancan.service;

import com.example.tuancan.model.FoodMaterial;

import java.util.List;

public interface  FoodMaterialService {

    /**
     *  根据id 查询单个结果*/
    public FoodMaterial selectOneById(Integer id);

    /**
     *  根据小类编号查询结果*/
   public List<FoodMaterial> selectByClassId(Integer id);

    /*  名字 查询结果*/
    public List<FoodMaterial> selectByName(String name);
   /**
    *所有数据*/
   public List<FoodMaterial> getAll();
    /**
     * 插入数据*/
    public int insertOne(FoodMaterial foodMaterial);

}
