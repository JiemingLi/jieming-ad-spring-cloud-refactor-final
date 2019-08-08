package com.jieming.ad.dao;

import com.jieming.ad.entity.AdUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Qinyi.
 */
public interface AdUnitRepository extends JpaRepository<AdUnit, Long> {

    // 一个推广计划有多个推广单元
    // 所以可以用过 推广计划的id和推广单元的名字唯一确定一个推广单元
    AdUnit findByPlanIdAndUnitName(Long planId, String unitName);

    //  通过状态查找推广单元，也就是查看有没有效
    List<AdUnit> findAllByUnitStatus(Integer unitStatus);
}
