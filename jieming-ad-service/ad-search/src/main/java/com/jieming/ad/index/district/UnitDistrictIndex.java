package com.jieming.ad.index.district;

import com.jieming.ad.index.IndexAware;
import com.jieming.ad.search.vo.feature.DistrictFeature;
import com.jieming.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

// 反向索引，可以通过一个推广单元查看在哪个城市进行了推广，查询一个城市查看有哪些推广单元
// 城市 和 Unit是多对多的关系
// 传进来的 key 是 province-city

@SuppressWarnings("Duplicates")
@Slf4j
@Component
public class UnitDistrictIndex implements IndexAware<String, Set<Long>> {

    // key: privince-city ,value :unitIds
    private static Map<String, Set<Long>> districtUnitMap;
    // key: unitd, value : province-key
    private static Map<Long, Set<String>> unitDistrictMap;

    static {
        districtUnitMap = new ConcurrentHashMap<>();
        unitDistrictMap = new ConcurrentHashMap<>();
    }

    @Override
    public Set<Long> get(String key) {
        return districtUnitMap.get(key);
    }

    @Override
    public void add(String key, Set<Long> value) {

        log.info("UnitDistrictIndex, before add: {}", unitDistrictMap);

        // 查询 一个城市有哪些推广单元
        Set<Long> unitIds = CommonUtils.getorCreate(
                key, districtUnitMap,
                ConcurrentSkipListSet::new
        );
        // 然后添加到 城市对应的推广单元中
        unitIds.addAll(value);

        for (Long unitId : value) {

            // 查询一个推广单元在哪些城市进行了推广
            Set<String> districts = CommonUtils.getorCreate(
                    unitId, unitDistrictMap,
                    ConcurrentSkipListSet::new
            );
            // 因为传进来的key就是一个值，只用add即可
            districts.add(key);
        }

        log.info("UnitDistrictIndex, after add: {}", unitDistrictMap);
    }

    @Override
    public void update(String key, Set<Long> value) {

        log.error("district index can not support update");
    }


    // 删除和添加是刚好相反的
    @Override
    public void delete(String key, Set<Long> value) {

        log.info("UnitDistrictIndex, before delete: {}", unitDistrictMap);

        Set<Long> unitIds = CommonUtils.getorCreate(
                key, districtUnitMap,
                ConcurrentSkipListSet::new
        );
        unitIds.removeAll(value);

        for (Long unitId : value) {

            Set<String> districts = CommonUtils.getorCreate(
                    unitId, unitDistrictMap,
                    ConcurrentSkipListSet::new
            );
            districts.remove(key);
        }

        log.info("UnitDistrictIndex, after delete: {}", unitDistrictMap);
    }

    // 通过推广单元的ID查找districts
    public boolean match(Long adUnitId,
                         List<DistrictFeature.ProvinceAndCity> districts) {

        if (unitDistrictMap.containsKey(adUnitId) &&
                CollectionUtils.isNotEmpty(unitDistrictMap.get(adUnitId))) {

            Set<String> unitDistricts = unitDistrictMap.get(adUnitId);

            // 从内存中的unitDistrictMap 匹配传进来的districts里面的键
            List<String> targetDistricts = districts.stream()
                    .map(
                            d -> CommonUtils.stringConcat(
                                    d.getProvince(), d.getCity()
                            )
                    ).collect(Collectors.toList());

            return CollectionUtils.isSubCollection(targetDistricts, unitDistricts);
        }
        return false;
    }
}
