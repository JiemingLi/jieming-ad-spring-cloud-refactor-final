package com.jieming.ad.dao.unit_condition;

import com.jieming.ad.entity.unit_condition.CreativeUnit;
import org.springframework.data.jpa.repository.JpaRepository;

// 一个推广单元可以推广多个创意
// 一个 创意 可以被多个推广单元 推广
// 创意 和 推广单元是多对多的关系
//  一个创意
public interface CreativeUnitRepository
        extends JpaRepository<CreativeUnit, Long> {
}
