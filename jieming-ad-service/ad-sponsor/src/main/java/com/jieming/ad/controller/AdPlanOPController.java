package com.jieming.ad.controller;

import com.alibaba.fastjson.JSON;
import com.jieming.ad.entity.AdPlan;
import com.jieming.ad.exception.AdException;
import com.jieming.ad.service.IAdPlanService;
import com.jieming.ad.vo.AdPlanGetRequest;
import com.jieming.ad.vo.AdPlanRequest;
import com.jieming.ad.vo.AdPlanResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class AdPlanOPController {

    @Autowired
    private IAdPlanService iAdPlanService;

    // 推广计划的增加
    @PostMapping("/create/adPlan")
    public AdPlanResponse createAdPlan(@RequestBody  AdPlanRequest request) throws AdException{
        log.info("ad-sponsor:createAdPlan -> {}", JSON.toJSONString(request));
        return iAdPlanService.createAdPlan(request);
    }

    // 推广计划的查询
    @PostMapping("/get/adPlan")
    public List<AdPlan>  getAdPlanByIds(@RequestBody AdPlanGetRequest request) throws AdException{
        log.info("ad-sponsor:getAdPlan -> {}", JSON.toJSONString(request));
        return iAdPlanService.getAdPlanByIds(request);
    }

    // 推广计划的更新
    @PutMapping("/update/adPlan")
    public AdPlanResponse updateAdPlan(@RequestBody AdPlanRequest request ) throws AdException{
        log.info("ad-sponsor:updateAdPlan -> {}", JSON.toJSONString(request));
        return  iAdPlanService.updateAdPlan(request);
    }

    // 推广计划的删除
    @DeleteMapping("/delete/adPlan")
    public void deleteAdPlan(
            @RequestBody AdPlanRequest request) throws AdException {
        log.info("ad-sponsor: deleteAdPlan -> {}",
                JSON.toJSONString(request));
        iAdPlanService.deleteAdPlan(request);
    }


}
