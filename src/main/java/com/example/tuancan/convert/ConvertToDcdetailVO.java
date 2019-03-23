package com.example.tuancan.convert;

import com.example.tuancan.dto.DeliverDetailVO;
import com.example.tuancan.model.DeliveringDetail;
import com.example.tuancan.model.Recipe;
import com.example.tuancan.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ConvertToDcdetailVO {

    /*将配送*/
    public static List<DeliverDetailVO> convertToDcdetailVO(List<DeliveringDetail> deliveringDetails){

        List<DeliverDetailVO> deliverDetailVOS=new ArrayList<DeliverDetailVO>();
        DeliverDetailVO detailVO=null;
        for (DeliveringDetail detail:deliveringDetails){
            detailVO=new DeliverDetailVO();
            Recipe recipe=new Recipe();
            recipe.setRecipeIcon(detail.getRecipe().getRecipeIcon());
            recipe.setRecipeId(detail.getRecipe().getRecipeId());
            recipe.setRecipeName(detail.getRecipe().getRecipeName());
            detailVO.setRecipe(recipe);
            detailVO.setDeliveringDetailNo(detail.getDeliveringDetailNo());
            detailVO.setDeliveringDetailNumber(detail.getDeliveringDetailNumber());
            detailVO.setDeliveringDetailMemo(detail.getDeliveringDetailMemo());
            deliverDetailVOS.add(detailVO);
        }
        log.info("details>>>>"+JsonUtil.toJson(deliverDetailVOS));
        return  deliverDetailVOS;
    }
}
