package com.jieming.ad.search.vo;

import com.jieming.ad.search.vo.feature.DistrictFeature;
import com.jieming.ad.search.vo.feature.FeatureRelation;
import com.jieming.ad.search.vo.feature.ItFeature;
import com.jieming.ad.search.vo.feature.KeywordFeature;
import com.jieming.ad.search.vo.media.AdSlot;
import com.jieming.ad.search.vo.media.App;
import com.jieming.ad.search.vo.media.Device;
import com.jieming.ad.search.vo.media.Geo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/*
* 三个部分
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {
    //媒体方的请求标识
    private String mediaId;

    //请求基本信息
    private RequestInfo requestInfo;

    //维度限制
    private FeatureInfo featureInfo;

    //请求基本信息
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestInfo{
        private String requestId;
        //一次请求可以请求多个广告位
        private List<AdSlot> adSlots ;
        // app信息
        private App app;
        //地理位置信息
        private Geo geo;
        //设备信息
        private Device device;
    }

    //请求匹配信息
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FeatureInfo{
        private KeywordFeature keywordFeature;
        private ItFeature itFeature;
        private DistrictFeature districtFeature;
        private FeatureRelation relation = FeatureRelation.AND;
    }


}
