package com.example.tuancan.controller;

import com.example.tuancan.model.*;
import com.example.tuancan.service.*;
import com.example.tuancan.utils.JsonUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/recipe")
public class RecipeController {

    @Autowired
    private RecipeTypeService recipeTypeService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private AccessorieService accessorieService;

    @Autowired
    private FoodMaterialService foodMaterialService;


    /*进入添加食谱页面
    * 封装所需要的数据*/
    @RequestMapping("/define")
    public String recipeDefine(HttpServletRequest httpServletRequest, Model model) {

        log.info("公司id:" + httpServletRequest.getSession().getAttribute("companyId"));
        List<RecipeType> recipeTypes = recipeTypeService.selectAll();
        /*添加到model中*/
        model.addAttribute("recipelist", recipeTypes);
        return "/groupmanager/recipe";
    }

    /*添加*/
    @RequestMapping("/addrecipe")
    public String addRecipe(Recipe recipe, @RequestParam(value = "type", required = false) String type, HttpServletRequest httpServletRequest) {
        /*插入数据到食谱中*/
        RecipeType recipeType = new RecipeType();
        recipeType.setRecipeTypeId(Integer.parseInt(type));
        DeliveringCompany deliveringCompany = new DeliveringCompany();
        Integer companyId = (Integer) httpServletRequest.getSession().getAttribute("companyId");
        deliveringCompany.setDeliveringCompanyNo(companyId);
        recipe.setRecipeTypeId(recipeType);
        recipe.setDeliveringCompanyNo(deliveringCompany);
        recipeService.insertOne(recipe);
        /*得到编号*/
        List<Recipe> recipes = recipeService.selectByCompanyIdAndName(companyId, recipe.getRecipeName());

        /*原料编号*/
        String recipeMaterial = recipe.getRecipeMaterial();
        if (!StringUtils.isEmpty(recipeMaterial)) {
            String[] split = recipeMaterial.split(",");
            for (int i = 0; i < split.length; i++) {
                Material material = new Material();
                String[] split1 = split[i].split(":");
                int i1 = Integer.parseInt(split1[0]);
                int i2 = Integer.parseInt(split1[1]);

                System.out.println(i2);
                FoodMaterial foodMaterial = foodMaterialService.selectOneById(i1);
                material.setFoodMaterial(foodMaterial);
                material.setRecipe(recipes.get(recipes.size() - 1));
                material.setMaterialNo(new Date().getDate());
                material.setMaterialWeight(i2);
                materialService.insertOne(material);
            }
        }

//
        /*辅料编号*/
        String recipeAccessorie = recipe.getRecipeAccessorie();
        if (!StringUtils.isEmpty(recipeAccessorie)) {
            String[] split1 = recipeAccessorie.split(",");
            for (int i = 0; i < split1.length; i++) {
                Accessorie accessorie = new Accessorie();
                String[] split2 = split1[i].split(":");
                int i1 = Integer.parseInt(split2[0]);
                String w = split2[1];
                System.out.println(w);
                FoodMaterial foodMaterial = foodMaterialService.selectOneById(i1);
                accessorie.setFoodMaterial(foodMaterial);
                accessorie.setRecipe(recipes.get(recipes.size() - 1));
                accessorie.setAccessorieNo(new Date().getDate());
                accessorie.setAccessorieWeight(w);

                accessorieService.insertOne(accessorie);
            }
        }
        return "redirect:/recipe/define";
    }

    /*查询*/
    @RequestMapping("/search")
    public String searchStaffs(@RequestParam("id") Integer id, @RequestParam("name") String name, Model model
            , @PathVariable(value = "pagenum", required = false) Integer pageNum) {
        List<Recipe> recipes = recipeService.slectByLikeName(name);
        addToModel(model, recipes, 1);
        model.addAttribute("search", true);
        return "/groupmanager/allrecipe :: #table_recipe";
    }


    /*查看已添加食谱*/
    @RequestMapping("/getallrecipes")
    public String getAllRecipes(HttpServletRequest httpServletRequest, Model model) {
        return "redirect:/recipe/getallrecipes/1";
    }

    //    /*查看分页食谱*/
    @RequestMapping(value = "/getallrecipes/{pagenum}")
    public String getAllRecipesByNumber(Model model, @PathVariable(value = "pagenum", required = false) Integer pageNum, HttpServletRequest httpServletRequest) {
        Integer companyId = (Integer) httpServletRequest.getSession().getAttribute("companyId");
        List<Recipe> recipes = recipeService.selectByCompanyId(companyId);
        addToModel(model, recipes, pageNum);
        model.addAttribute("search", false);
        return "/groupmanager/allrecipe";
    }


    private void addToModel(Model model, List<Recipe> recipes, Integer pageNum) {
        model.addAttribute("isFirstPage", pageNum == 1);
        int totalPage;
        int size = 7;
        if (recipes.size() % size != 0) {
            totalPage = recipes.size() / size + 1;
        } else {
            totalPage = recipes.size() / size;
        }
        model.addAttribute("pageTotal", totalPage);
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("isLastPage", pageNum == totalPage);
        List<Recipe> recipeList = new ArrayList<>();
        for (int j = (pageNum - 1) * size; j < ((pageNum) * size > recipes.size() ? recipes.size() : (pageNum) * size); j++) {
            recipeList.add(recipes.get(j));
        }
        model.addAttribute("recipes", recipeList);
    }

    /*更新状态*/
    @RequestMapping("/updatestatus")
    public String updateStatus(@RequestParam("id") Integer id) {
        Recipe recipe = recipeService.selectOneById(id);
        int recipeStatus = recipe.getRecipeStatus();
        if (recipeStatus == 1) {
            recipe.setRecipeStatus(0);
        } else {
            recipe.setRecipeStatus(1);
        }
        recipeService.updateOne(recipe);
        return "redirect:/recipe/getallrecipes";
    }

    /*更新数据*/
    @RequestMapping("/updaterecipe")
    public String updateRecipe(@RequestParam("id") Integer id, Model model) {
        Recipe recipe = recipeService.selectOneById(id);
        model.addAttribute("recipe", recipe);
        List<RecipeType> recipeTypes = recipeTypeService.selectAll();
        model.addAttribute("recipelist", recipeTypes);
        return "/groupmanager/updaterecipe";
    }

    /*确认更新*/
    @RequestMapping("/update")
    public String update(Recipe recipe, @RequestParam("type") String type, HttpServletRequest httpServletRequest) {

        RecipeType recipeType = new RecipeType();
        recipeType.setRecipeTypeId(Integer.parseInt(type));
        recipe.setRecipeTypeId(recipeType);

        String recipeIcon1 = recipeService.selectOneById(recipe.getRecipeId()).getRecipeIcon();

        String recipeIcon = recipe.getRecipeIcon();
        if (StringUtils.isEmpty(recipeIcon)) {
            recipeIcon = recipeIcon1;
            recipe.setRecipeIcon(recipeIcon);
        }
        DeliveringCompany deliveringCompany = new DeliveringCompany();
        Integer companyId = (Integer) httpServletRequest.getSession().getAttribute("companyId");
        deliveringCompany.setDeliveringCompanyNo(companyId);
        recipe.setDeliveringCompanyNo(deliveringCompany);
        recipeService.updateOne(recipe);
        return "redirect:/recipe/getallrecipes";
    }
}
