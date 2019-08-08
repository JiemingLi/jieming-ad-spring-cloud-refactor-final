package com.jieming.ad.search.vo;

import com.jieming.ad.index.creative.CreativeObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse {

    //一个广告位可能会有多个广告，比如说轮播等
    // key是广告位的id，value就是多个广告
    public Map<String,List<Creative>> adSlot2Ads = new HashMap<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Creative{
        //广告ID
        private Long adId;
        //广告URL
        private String adUrl;

        private Integer width;
        private Integer height;
        //广告的类型
        private Integer type;
        private Integer metrialType;

        //展示监测 URL
        private List<String> showMonitorUrl =
                Arrays.asList("www.imooc.com","www.imooc.com");

        //点击监测 URL
        private List<String> clickMonitorUrl =
                Arrays.asList("www.imooc.com","www.imooc.com");
    }

    //将索引检索出来的CreativeObject对象转化为Creative对象返回给
    //媒体方
    public static Creative convert(CreativeObject object){
        Creative creative = new Creative();
        creative.setAdId(object.getAdId());
        creative.setAdUrl(object.getAdUrl());
        creative.setWidth(object.getWidth());
        creative.setHeight(object.getHeight());
        creative.setMetrialType(object.getMaterialType());
        creative.setType(object.getType());
        return creative;
    }
}
