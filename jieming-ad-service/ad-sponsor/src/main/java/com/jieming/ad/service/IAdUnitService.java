package com.jieming.ad.service;

import com.jieming.ad.exception.AdException;
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

/**
 * Created by jieming.
 */
public interface IAdUnitService {

    //创建推广单元
    AdUnitResponse createUnit(AdUnitRequest request) throws AdException;

    // 创建关键字推广单元
    AdUnitKeywordResponse createUnitKeyword(AdUnitKeywordRequest request)
            throws AdException;

    //创建兴趣推广单元
    AdUnitItResponse createUnitIt(AdUnitItRequest request)
        throws AdException;

    //创建 地域推广单元
    AdUnitDistrictResponse createUnitDistrict(AdUnitDistrictRequest request)
        throws AdException;

    //创建创意推广单元
    CreativeUnitResponse createCreativeUnit(CreativeUnitRequest request)
        throws AdException;
}
