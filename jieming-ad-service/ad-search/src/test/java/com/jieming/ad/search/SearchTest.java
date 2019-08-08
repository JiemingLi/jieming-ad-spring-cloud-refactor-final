package com.jieming.ad.search;

import com.alibaba.fastjson.JSON;
import com.jieming.ad.Application;
import com.jieming.ad.search.vo.SearchRequest;
import com.jieming.ad.search.vo.feature.DistrictFeature;
import com.jieming.ad.search.vo.feature.FeatureRelation;
import com.jieming.ad.search.vo.feature.ItFeature;
import com.jieming.ad.search.vo.feature.KeywordFeature;
import com.jieming.ad.search.vo.media.AdSlot;
import com.jieming.ad.search.vo.media.App;
import com.jieming.ad.search.vo.media.Device;
import com.jieming.ad.search.vo.media.Geo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SearchTest {

    @Autowired
    private ISearch search;

    @Test
    public void fetchTest(){
        SearchRequest request = new SearchRequest();
        request.setMediaId("jieming-ad");

        //第一个测试条件
        request.setRequestInfo(new SearchRequest.RequestInfo(
                "jiemingli",
                Collections.singletonList(new AdSlot(
                        "ad-x",1,
                        1080,720,Arrays.asList(1,2),
                        1000
                )),
                buildExampleApp(),
                buildExampleGeo(),
                buildExampleDevice()
        ));

        request.setFeatureInfo(buildExampleFeatureInfo(
                Arrays.asList("宝马","大众"),
                Collections.singletonList(
                        new DistrictFeature.ProvinceAndCity(
                                "安徽省","合肥市"
                        )
                ),
                Arrays.asList("台球","游泳"),
                FeatureRelation.AND
        ));

//        System.out.println(JSON.toJSONString(request));
        System.err.println(JSON.toJSONString(search.fetchAds(request)));

        // 第二个测试条件
        request.setRequestInfo(new SearchRequest.RequestInfo(
                "aaa",
                Collections.singletonList(new AdSlot(
                        "ad-y", 1,
                        1080, 720, Arrays.asList(1, 2),
                        1000
                )),
                buildExampleApp(),
                buildExampleGeo(),
                buildExampleDevice()
        ));
        request.setFeatureInfo(buildExampleFeatureInfo(
                Arrays.asList("宝马", "大众", "标志"),
                Collections.singletonList(
                        new DistrictFeature.ProvinceAndCity(
                                "安徽省", "合肥市")),
                Arrays.asList("台球", "游泳"),
                FeatureRelation.OR
        ));
//        System.out.println(JSON.toJSONString(request));
        System.err.println(JSON.toJSONString(search.fetchAds(request)));
    }

    private App buildExampleApp(){
        return new App("jieming-code","jieming-name","jieming-package",
                "jieming-activity");

    }

    private Geo buildExampleGeo() {
        return new Geo((float) 100.28, (float) 88.61,
                "北京市", "北京市");
    }

    private Device buildExampleDevice() {

        return new Device(
                "iphone",
                "0xxxxx",
                "127.0.0.1",
                "x",
                "1080 720",
                "1080 720",
                "123456789"

        );
    }

    private SearchRequest.FeatureInfo buildExampleFeatureInfo(
            List<String> keywords,
            List<DistrictFeature.ProvinceAndCity> provinceAndCities,
            List<String> its,
            FeatureRelation relation
    ) {
       return new SearchRequest.FeatureInfo(
               new KeywordFeature(keywords),
               new ItFeature(its),
               new DistrictFeature(provinceAndCities),
               relation
       );
    }
}
