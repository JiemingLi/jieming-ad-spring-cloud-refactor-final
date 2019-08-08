package com.jieming.ad.handler;

import com.alibaba.fastjson.JSON;
import com.jieming.ad.dump.table.AdCreativeTable;
import com.jieming.ad.dump.table.AdCreativeUnitTable;
import com.jieming.ad.dump.table.AdPlanTable;
import com.jieming.ad.dump.table.AdUnitDistrictTable;
import com.jieming.ad.dump.table.AdUnitItTable;
import com.jieming.ad.dump.table.AdUnitKeywordTable;
import com.jieming.ad.dump.table.AdUnitTable;
import com.jieming.ad.index.DataTable;
import com.jieming.ad.index.IndexAware;
import com.jieming.ad.index.adplan.AdPlanIndex;
import com.jieming.ad.index.adplan.AdPlanObject;
import com.jieming.ad.index.adunit.AdUnitIndex;
import com.jieming.ad.index.adunit.AdUnitObject;
import com.jieming.ad.index.creative.CreativeIndex;
import com.jieming.ad.index.creative.CreativeObject;
import com.jieming.ad.index.creativeunit.CreativeUnitIndex;
import com.jieming.ad.index.creativeunit.CreativeUnitObject;
import com.jieming.ad.index.district.UnitDistrictIndex;
import com.jieming.ad.index.interest.UnitItIndex;
import com.jieming.ad.index.keyword.UnitKeywordIndex;
import com.jieming.ad.constant.OpType;
import com.jieming.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 1. 索引之间存在着层级的划分, 也就是依赖关系的划分
 * 2. 加载全量索引其实是增量索引 "添加" 的一种特殊实现
 * Created by Qinyi.
 */

/*
* 不仅可以添加全量索引，还可以监听增量索引
* */
// 此类负责将 本地的文件加载进系统的对象  转化为  一个个索引对象
// 分层次是因为每个层次之间都有一个互相依赖的关系
@Slf4j
public class AdLevelDataHandler {

    //处理第二层级，adplan的方法，第二层级的索引并不和其他索引存在着依赖关系（除了用户索引）
    public static void handleLevel2(AdPlanTable planTable, OpType type) {

        //将从json文件反序列化的表结构对象转化为索引对象
        AdPlanObject planObject = new AdPlanObject(
                planTable.getId(),
                planTable.getUserId(),
                planTable.getPlanStatus(),
                planTable.getStartDate(),
                planTable.getEndDate()
        );

        //保存在AdPlan存储的内存中
        handleBinlogEvent(
                DataTable.of(AdPlanIndex.class),
                planObject.getPlanId(),
                planObject,
                type
        );
    }

    public static void handleLevel2(AdCreativeTable creativeTable,
                                    OpType type) {

        CreativeObject creativeObject = new CreativeObject(
                creativeTable.getAdId(),
                creativeTable.getName(),
                creativeTable.getType(),
                creativeTable.getMaterialType(),
                creativeTable.getHeight(),
                creativeTable.getWidth(),
                creativeTable.getAuditStatus(),
                creativeTable.getAdUrl()
        );
        handleBinlogEvent(
                DataTable.of(CreativeIndex.class),
                creativeObject.getAdId(),
                creativeObject,
                type
        );
    }

//   ----------------------------------------------------------------↑层级2--------------------------------------------------------------------

    //第三层级和第二层级有依赖关系
    public static void handleLevel3(AdUnitTable unitTable, OpType type) {

        //判断传进来的推广单元结构属于哪个推广计划
        //也就是第3层级依赖于第2层级的表现
        AdPlanObject adPlanObject = DataTable.of(
                AdPlanIndex.class
        ).get(unitTable.getPlanId());

        if (null == adPlanObject) {
            log.error("handleLevel3 found AdPlanObject error: {}",
                    unitTable.getPlanId());
            return;
        }

        //把推广单元的数据表结构转为索引结构
        AdUnitObject unitObject = new AdUnitObject(
                unitTable.getUnitId(),
                unitTable.getUnitStatus(),
                unitTable.getPositionType(),
                unitTable.getPlanId(),
                adPlanObject
        );

        handleBinlogEvent(
                DataTable.of(AdUnitIndex.class),
                unitTable.getUnitId(),
                unitObject,
                type
        );
    }

    public static void handleLevel3(AdCreativeUnitTable creativeUnitTable,
                                    OpType type) {

        if (type == OpType.UPDATE) {
            log.error("CreativeUnitIndex not support update");
            return;
        }

        // 获取unitObject对象
        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(creativeUnitTable.getUnitId());

        // 获取creativeObject对象
        CreativeObject creativeObject = DataTable.of(
                CreativeIndex.class
        ).get(creativeUnitTable.getAdId());

        // 判空
        if (null == unitObject || null == creativeObject) {
            log.error("AdCreativeUnitTable index error: {}",
                    JSON.toJSONString(creativeUnitTable));
            return;
        }

        // 建立联创意和推广单元合索引对象
        CreativeUnitObject creativeUnitObject = new CreativeUnitObject(
                creativeUnitTable.getAdId(),
                creativeUnitTable.getUnitId()
        );

        handleBinlogEvent(
                DataTable.of(CreativeUnitIndex.class),
                CommonUtils.stringConcat(
                        creativeUnitObject.getAdId().toString(),
                        creativeUnitObject.getUnitId().toString()
                ),
                creativeUnitObject,
                type
        );
    }
//   ----------------------------------------------------------------↑层级3--------------------------------------------------------------------


    //推广单元的地域限制
    public static void handleLevel4(AdUnitDistrictTable unitDistrictTable,
                                    OpType type) {

        //不支持更新操作
        if (type == OpType.UPDATE) {
            log.error("district index can not support update");
            return;
        }

        // 依赖于第2层级的 unitObject 索引对象
        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(unitDistrictTable.getUnitId());

        if (unitObject == null) {
            log.error("AdUnitDistrictTable index error: {}",
                    unitDistrictTable.getUnitId());
            return;
        }

        // 构建索引UnitDistrictIndex的键
        String key = CommonUtils.stringConcat(
                unitDistrictTable.getProvince(),
                unitDistrictTable.getCity()
        );
        // 构建UnitDistrictIndex的value
        Set<Long> value = new HashSet<>(
                Collections.singleton(unitDistrictTable.getUnitId())
        );
        handleBinlogEvent(
                DataTable.of(UnitDistrictIndex.class),
                key, value,
                type
        );
    }


    public static void handleLevel4(AdUnitItTable unitItTable, OpType type) {

        if (type == OpType.UPDATE) {
            log.error("it index can not support update");
            return;
        }

        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(unitItTable.getUnitId());

        if (unitObject == null) {
            log.error("AdUnitItTable index error: {}",
                    unitItTable.getUnitId());
            return;
        }

        Set<Long> value = new HashSet<>(
                Collections.singleton(unitItTable.getUnitId())
        );
        handleBinlogEvent(
                DataTable.of(UnitItIndex.class),
                unitItTable.getItTag(),
                value,
                type
        );
    }

    public static void handleLevel4(AdUnitKeywordTable keywordTable,
                                    OpType type) {

        if (type == OpType.UPDATE) {
            log.error("keyword index can not support update");
            return;
        }

        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(keywordTable.getUnitId());

        if (unitObject == null) {
            log.error("AdUnitKeywordTable index error: {}",
                    keywordTable.getUnitId());
            return;
        }

        Set<Long> value = new HashSet<>(
                Collections.singleton(keywordTable.getUnitId())
        );

        handleBinlogEvent(
                DataTable.of(UnitKeywordIndex.class),
                keywordTable.getKeyword(),
                value,
                type
        );
    }

    private static <K, V> void handleBinlogEvent(
            IndexAware<K, V> index,
            K key,
            V value,
            OpType type) {

        switch (type) {
            case ADD:
                index.add(key, value);
                break;
            case UPDATE:
                index.update(key, value);
                break;
            case DELETE:
                index.delete(key, value);
                break;
            default:
                break;
        }
    }
}
