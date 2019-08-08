package com.jieming.ad.controller;

import com.alibaba.fastjson.JSON;
import com.jieming.ad.exception.AdException;
import com.jieming.ad.service.IAdUnitService;
import com.jieming.ad.vo.AdUnitDistrictRequest;
import com.jieming.ad.vo.AdUnitDistrictResponse;
import com.jieming.ad.vo.AdUnitItRequest;
import com.jieming.ad.vo.AdUnitItResponse;
import com.jieming.ad.vo.AdUnitKeywordRequest;
import com.jieming.ad.vo.AdUnitKeywordResponse;
import com.jieming.ad.vo.AdUnitRequest;
import com.jieming.ad.vo.AdUnitResponse;
import com.jieming.ad.vo.CreativeUnitRequest;
import com.jieming.ad.vo.CreativeUnitResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jieming.
 */
@Slf4j
@RestController
public class AdUnitOPController {

    private final IAdUnitService adUnitService;

    @Autowired
    public AdUnitOPController(IAdUnitService adUnitService) {
        this.adUnitService = adUnitService;
    }

    // 推广单元的增加
    @PostMapping("/create/adUnit")
    public AdUnitResponse createUnit(
            @RequestBody AdUnitRequest request) throws AdException {
        log.info("ad-sponsor: createUnit -> {}",
                JSON.toJSONString(request));
        return adUnitService.createUnit(request);
    }

    // 推广单元的
    @PostMapping("/create/unitKeyword")
    public AdUnitKeywordResponse createUnitKeyword(
            @RequestBody AdUnitKeywordRequest request
    ) throws AdException {
        log.info("ad-sponsor: createUnitKeyword -> {}",
                JSON.toJSONString(request));
        return adUnitService.createUnitKeyword(request);
    }

    @PostMapping("/create/unitIt")
    public AdUnitItResponse createUnitIt(
            @RequestBody AdUnitItRequest request
    ) throws AdException {
        log.info("ad-sponsor: createUnitIt -> {}",
                JSON.toJSONString(request));
        return adUnitService.createUnitIt(request);
    }

    @PostMapping("/create/unitDistrict")
    public AdUnitDistrictResponse createUnitDistrict(
            @RequestBody AdUnitDistrictRequest request
    ) throws AdException {
        log.info("ad-sponsor: createUnitDistrict -> {}",
                JSON.toJSONString(request));
        return adUnitService.createUnitDistrict(request);
    }

    @PostMapping("/create/creativeUnit")
    public CreativeUnitResponse createCreativeUnit(
            @RequestBody CreativeUnitRequest request
    ) throws AdException {
        log.info("ad-sponsor: createCreativeUnit -> {}",
                JSON.toJSONString(request));
        return adUnitService.createCreativeUnit(request);
    }
}

