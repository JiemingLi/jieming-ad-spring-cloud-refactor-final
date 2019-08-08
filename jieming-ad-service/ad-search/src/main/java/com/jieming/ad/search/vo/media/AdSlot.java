package com.jieming.ad.search.vo.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdSlot {
    //广告位编码
    private String adSlotCode;

    //流量的类型
    private Integer positionType;

    //广告位的尺寸
    private Integer width;
    private Integer height;

    //广告的物料类型,图片，视频等
    private List<Integer> type;

    //最低出价
    private Integer minCpm;


}
