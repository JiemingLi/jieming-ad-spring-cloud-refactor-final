package com.jieming.ad.dao;

import com.jieming.ad.entity.Creative;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 一一个推广计划有多个推广创意
 *   推广计划和推广创意是一对多的关系
 */
public interface CreativeRepository extends JpaRepository<Creative, Long> {
}
