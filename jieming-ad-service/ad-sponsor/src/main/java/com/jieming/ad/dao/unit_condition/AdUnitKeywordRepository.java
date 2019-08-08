package com.jieming.ad.dao.unit_condition;

import com.jieming.ad.entity.unit_condition.AdUnitKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

// 一个推广单元可以有多个关键词
// 一个 关键词 可以被 多个推广单元 使用
// 关键词 和 推广单元是多对多的关系
public interface AdUnitKeywordRepository extends
        JpaRepository<AdUnitKeyword, Long> {
}
