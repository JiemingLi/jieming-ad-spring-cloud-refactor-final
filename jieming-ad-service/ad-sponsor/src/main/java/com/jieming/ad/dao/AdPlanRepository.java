package com.jieming.ad.dao;

import com.jieming.ad.entity.AdPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Qinyi.
 */
public interface AdPlanRepository extends JpaRepository<AdPlan, Long> {

    AdPlan findByIdAndUserId(Long id, Long userId);

    //一个user可以有多个推广计划
    List<AdPlan> findAllByIdInAndUserId(List<Long> ids, Long userId);

    //描述 用户投放了哪个计划
    AdPlan findByUserIdAndPlanName(Long userId, String planName);

    // 通过状态查找推广计划，也就是查看有没有效
    List<AdPlan> findAllByPlanStatus(Integer status);
}
