package com.jieming.ad.dao.unit_condition;

import com.jieming.ad.entity.unit_condition.AdUnitIt;
import org.springframework.data.jpa.repository.JpaRepository;

// 一个推广单元可以有到多个 兴趣标签
// 一个 兴趣标签 可以被多个推广单元使用
// 兴趣标签 和 推广单元是多对多的关系
public interface AdUnitItRepository
        extends JpaRepository<AdUnitIt, Long> {
}
