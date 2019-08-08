package com.jieming.ad.dao.unit_condition;

import com.jieming.ad.entity.unit_condition.AdUnitDistrict;
import org.springframework.data.jpa.repository.JpaRepository;

// 一个推广单元可以投放到多个 省-市
// 一个 省-市 可以被投放多个推广单元
// 省-市 和 推广单元是多对多的关系
public interface AdUnitDistrictRepository
        extends JpaRepository<AdUnitDistrict, Long> {
}
