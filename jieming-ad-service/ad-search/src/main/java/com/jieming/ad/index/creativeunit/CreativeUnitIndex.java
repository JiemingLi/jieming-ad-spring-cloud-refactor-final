package com.jieming.ad.index.creativeunit;

import com.jieming.ad.index.IndexAware;
import com.jieming.ad.index.adunit.AdUnitObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

// 反向索引，可以通过推广单元查看推广了哪些创意，查询创意查看有哪些推广单元
// creative 和 Unit是多对多的关系
@Slf4j
@Component
public class CreativeUnitIndex implements
        IndexAware<String, CreativeUnitObject> {

    // <adId-unitId, CreativeUnitObject>
    // 通过组合键来唯一确定一个CreativeUnitObject对象
    private static Map<String, CreativeUnitObject> objectMap;
    // <creativeId, unitId Set> ，一个创意可以被多个推广单元推广
    private static Map<Long, Set<Long>> creativeUnitMap;
    // <unitId, adId set> ， 一个推广单元可以推广多个创意
    private static Map<Long, Set<Long>> unitCreativeMap;

    static {
        objectMap = new ConcurrentHashMap<>();
        creativeUnitMap = new ConcurrentHashMap<>();
        unitCreativeMap = new ConcurrentHashMap<>();
    }

    @Override
    public CreativeUnitObject get(String key) {
        return objectMap.get(key);
    }


    @Override
    public void add(String key, CreativeUnitObject value) {

        log.info("before add: {}", objectMap);

        // 添加CreativeObject对象
        objectMap.put(key, value);

        // 查询创意被哪些推广单元推广了，如果没有，就新建一个推广单元的id的set放进map，
        // 然后把推广单元的id添加进这个set
        Set<Long> unitSet = creativeUnitMap.get(value.getAdId());
        if (CollectionUtils.isEmpty(unitSet)) {
            unitSet = new ConcurrentSkipListSet<>();
            creativeUnitMap.put(value.getAdId(), unitSet);
        }
        unitSet.add(value.getUnitId());

        // 查询推广单元推广了哪些创意，如果没有，就新建一个创意的id的set放进map，
        // 然后把创意的id添加进这个set
        Set<Long> creativeSet = unitCreativeMap.get(value.getUnitId());
        if (CollectionUtils.isEmpty(creativeSet)) {
            creativeSet = new ConcurrentSkipListSet<>();
            unitCreativeMap.put(value.getUnitId(), creativeSet);
        }
        creativeSet.add(value.getAdId());

        log.info("after add: {}", objectMap);
    }

    // 没有update方法
    @Override
    public void update(String key, CreativeUnitObject value) {
        log.error("CreativeUnitIndex not support update");
    }


    // 添加操作和删除是反过来的
    @Override
    public void delete(String key, CreativeUnitObject value) {

        log.info("before delete: {}", objectMap);

        objectMap.remove(key);

        Set<Long> unitSet = creativeUnitMap.get(value.getAdId());
        if (CollectionUtils.isNotEmpty(unitSet)) {
            unitSet.remove(value.getUnitId());
        }

        Set<Long> creativeSet = unitCreativeMap.get(value.getUnitId());
        if (CollectionUtils.isNotEmpty(creativeSet)) {
            creativeSet.remove(value.getAdId());
        }

        log.info("after delete: {}", objectMap);
    }

    // 通过推广单元的ids获取创意的ids
    public List<Long> selectAds(List<AdUnitObject> unitObjects) {

        if (CollectionUtils.isEmpty(unitObjects)) {
            return Collections.emptyList();
        }

        List<Long> result = new ArrayList<>();

        for (AdUnitObject unitObject : unitObjects) {

            Set<Long> adIds = unitCreativeMap.get(unitObject.getUnitId());
            if (CollectionUtils.isNotEmpty(adIds)) {
                result.addAll(adIds);
            }
        }
        return result;
    }
}
