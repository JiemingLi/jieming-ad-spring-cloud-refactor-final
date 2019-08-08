package com.jieming.ad.controller;

import com.alibaba.fastjson.JSON;
import com.jieming.ad.annotation.IgnoreResponseAdvise;
import com.jieming.ad.client.SponsorClient;
import com.jieming.ad.client.vo.AdPlan;
import com.jieming.ad.client.vo.AdPlanGetRequest;
import com.jieming.ad.search.ISearch;
import com.jieming.ad.search.vo.SearchRequest;
import com.jieming.ad.search.vo.SearchResponse;
import com.jieming.ad.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@RestController
public class SearchController {

    @Autowired
    private ISearch search;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SponsorClient sponsorClient;

    @PostMapping("/getAdPlansByRibbon")
    @IgnoreResponseAdvise
    public CommonResponse<List<AdPlan>> getAdPlansByRibbon(@RequestBody AdPlanGetRequest request){
        log.info("ad-search getAdPlansByRibbon ->{}", JSON.toJSONString(request));
        return restTemplate.postForEntity
                ("http://eureka-client-ad-sponsor/ad-sponsor/get/adPlan",
                request,
                CommonResponse.class).getBody();
    }

    @PostMapping("/getAdPlansByFeign")
    @IgnoreResponseAdvise
    public CommonResponse<List<AdPlan>> getAdPlans(@RequestBody AdPlanGetRequest request){
        log.info("ad-search getAdPlansByFeign ->{}", JSON.toJSONString(request));
        return sponsorClient.getAdPlans(request);
    }

    // 实现广告检索
    @PostMapping("/fetchAds")
    public SearchResponse fetchAds(@RequestBody SearchRequest request){
        log.info("ad-search :fetchAds ->{}",JSON.toJSONString(request));
        return search.fetchAds(request);
    }


}
