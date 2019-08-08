package com.jieming.ad.search.impl;

import com.alibaba.fastjson.JSON;
import com.jieming.ad.index.CommonStatus;
import com.jieming.ad.index.DataTable;
import com.jieming.ad.index.adunit.AdUnitIndex;
import com.jieming.ad.index.adunit.AdUnitObject;
import com.jieming.ad.index.creative.CreativeIndex;
import com.jieming.ad.index.creative.CreativeObject;
import com.jieming.ad.index.creativeunit.CreativeUnitIndex;
import com.jieming.ad.index.district.UnitDistrictIndex;
import com.jieming.ad.index.interest.UnitItIndex;
import com.jieming.ad.index.keyword.UnitKeywordIndex;
import com.jieming.ad.search.ISearch;
import com.jieming.ad.search.vo.SearchRequest;
import com.jieming.ad.search.vo.SearchResponse;
import com.jieming.ad.search.vo.feature.DistrictFeature;
import com.jieming.ad.search.vo.feature.FeatureRelation;
import com.jieming.ad.search.vo.feature.ItFeature;
import com.jieming.ad.search.vo.feature.KeywordFeature;
import com.jieming.ad.search.vo.media.AdSlot;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import java.util.*;

@Slf4j
@Component
public class SearchImpl implements ISearch {


    @Override
    public SearchResponse fetchAds(SearchRequest request) {
        // 获取请求的广告位信息
        List<AdSlot> adSlots = request.getRequestInfo().getAdSlots();

        // 获取三个特征信息
        DistrictFeature districtFeature = request.getFeatureInfo().getDistrictFeature();
        ItFeature itFeature = request.getFeatureInfo().getItFeature();
        KeywordFeature keywordFeature = request.getFeatureInfo().getKeywordFeature();
        //获取三个信息的关系
        FeatureRelation relation = request.getFeatureInfo().getRelation();

        //构造响应对象
        SearchResponse response = new SearchResponse();

        // key为广告位的编码，value为创意
        Map<String,List<SearchResponse.Creative>> adSlot2Ads =
                response.getAdSlot2Ads();

        // 广告检索的实质
        // 就是检索推广单元,因为推广单元定义了和广告关联的信息
        // 所以对所有推广单元进行预过滤，范围缩小

        // 根据初始的positionType获取初始的AdUnit
        for (AdSlot adSlot : adSlots) {

            Set<Long> targetUnitIdSet;
            // 获取推广单元索引对象的管理对象
            AdUnitIndex adUnitIndex = DataTable.of(AdUnitIndex.class);

            // 先根据请求的流量类型堆推广单元进行过滤
            Set<Long> adUnitIdSet = adUnitIndex.match(adSlot.getPositionType());

            // 三个维度 ，与 关系过滤
            if(relation == FeatureRelation.AND){
                filterKeywordFeature(adUnitIdSet,keywordFeature);
                filterDistrictFeature(adUnitIdSet,districtFeature);
                filterItTagFeature(adUnitIdSet,itFeature);
                targetUnitIdSet = adUnitIdSet;
            }else{
                // 三个维度 ，或 关系过滤
                targetUnitIdSet = getORRelationUnitIds(adUnitIdSet,keywordFeature,districtFeature,itFeature);
            }
            // 获取unitObjects
            List<AdUnitObject> unitObjects = DataTable.of(AdUnitIndex.class).fetch(targetUnitIdSet);
            filterAdUnitAndPlanStatus(unitObjects,CommonStatus.VALID);

            //通过create-unit的adunit的ID去查找创意的id
            List<Long> adIds = DataTable.of(CreativeUnitIndex.class).selectAds(unitObjects);

            //通过创意ID获取到真正的创意对象
            List<CreativeObject> creativeObjects = DataTable.of(CreativeIndex.class).fetch(adIds);

            // 通过 AdSlot 实现对 CreativeObject 的过滤
            filterCreativeByAdSlot(
                    creativeObjects,
                    adSlot.getWidth(),
                    adSlot.getHeight(),
                    adSlot.getType()
            );

            // 根据广告位的id，存进最终筛选到的创意，buildCreativeResponse将创意索引对象转为返回给
            // 前台的创意对象
            adSlot2Ads.put(adSlot.getAdSlotCode(),buildCreativeResponse(creativeObjects));
    }

        log.info("fetchAds: {}-{}",
                JSON.toJSONString(request),
                JSON.toJSONString(response));

        return response;
    }


    // OR关系表示
    private Set<Long> getORRelationUnitIds(Set<Long> adUnitIdSet,
                                           KeywordFeature keywordFeature,
                                           DistrictFeature districtFeature,
                                           ItFeature itFeature
                                           ){
        if(adUnitIdSet == null){
            return null;
        }

        //拷贝副本
        Set<Long> keywordUnitIdSet = new HashSet<>(adUnitIdSet);
        Set<Long> districtUnitIdSet = new HashSet<>(adUnitIdSet);
        Set<Long> itUnitIdSet = new HashSet<>(adUnitIdSet);

        //过滤
        filterKeywordFeature(keywordUnitIdSet,keywordFeature);
        filterDistrictFeature(districtUnitIdSet,districtFeature);
        filterItTagFeature(itUnitIdSet,itFeature);


        return new HashSet<>(
                CollectionUtils.union(
                        CollectionUtils.union(keywordUnitIdSet,districtUnitIdSet),
                        itUnitIdSet
                )
        );

    }


    private void filterKeywordFeature(Collection<Long> adUnitIds,KeywordFeature keywordFeature){
        if(adUnitIds == null){
            return;
        }
        // 请求中携带有关键词
        if(CollectionUtils.isNotEmpty(keywordFeature.getKeywords())){
            // 最终得到的是含有传进来的关键词的推广单元的ids
            CollectionUtils.filter(
                    adUnitIds,
                    adUnitId -> DataTable.of(UnitKeywordIndex.class).
                            match(adUnitId,keywordFeature.getKeywords())
            );
        }
    }

    private void filterDistrictFeature(Collection<Long> adUnitIds,DistrictFeature districtFeature){
        if(adUnitIds == null){
            return;
        }
        // 请求中携带地域信息
        if(CollectionUtils.isNotEmpty(districtFeature.getDistricts())){
            // 最终得到的是含有传进来的地区的推广单元的ids
            CollectionUtils.filter(
                    adUnitIds,
                    adUnitId -> DataTable.of(UnitDistrictIndex.class).
                            match(adUnitId,districtFeature.getDistricts())
            );
        }
    }

    private void filterItTagFeature(Collection<Long> adUnitIds,ItFeature itFeature){
        if(adUnitIds == null){
            return;
        }
        // 请求中携带兴趣的信息
        if(CollectionUtils.isNotEmpty(itFeature.getIts())){
            // 最终得到的是含有传进来的兴趣的推广单元的ids
            CollectionUtils.filter(
                    adUnitIds,
                    adUnitId -> DataTable.of(UnitItIndex.class).
                            match(adUnitId,itFeature.getIts())
            );
        }
    }

    // 通过推广单元去判断所关联的推广计划是否有效
    private void filterAdUnitAndPlanStatus(List<AdUnitObject> unitObjects, CommonStatus status){
        if(CollectionUtils.isEmpty(unitObjects)){
            return;
        }

        // 推广单元和其对应的推广计划都有效
        CollectionUtils.filter(
                unitObjects,
                object -> object.getUnitStatus().equals(status.getStatus()) &&
                          object.getAdPlanObject().getPlanStatus().equals(status.getStatus())
        );
    }

    //通过 请求传来的 请求信息 来筛选最终的创意
    private void filterCreativeByAdSlot(List<CreativeObject> creatives,
                                        Integer width,
                                        Integer height,
                                        List<Integer> type) {

        if (CollectionUtils.isEmpty(creatives)) {
            return;
        }

        CollectionUtils.filter(
                creatives,
                creative ->
                        creative.getAuditStatus().equals(CommonStatus.VALID.getStatus())
                                && creative.getWidth().equals(width)
                                && creative.getHeight().equals(height)
                                && type.contains(creative.getType())
        );
    }

    // 将CreativeObject对象转化为SearchResponse对象的creative，
    // 并且随机返回一个转化后的创意
    private List<SearchResponse.Creative> buildCreativeResponse(
            List<CreativeObject> creativeObjects
    ) {

        if (CollectionUtils.isEmpty(creativeObjects)) {
            return Collections.emptyList();
        }
        // 随机选去一个创意索引对象
        CreativeObject randomObject = creativeObjects.get(
                Math.abs(new Random().nextInt()) % creativeObjects.size()
        );

        // 将CreativeObject对象转为Creative对象
        return Collections.singletonList(
                SearchResponse.convert(randomObject)
        );
    }

}
