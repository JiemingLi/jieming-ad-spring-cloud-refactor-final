package com.jieming.ad.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// 查询完推广计划之后的推广计划的响应对象
// 这里只返回了推广计划的Id,推广计划的名字
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdPlanResponse {

    private Long id;
    private String planName;
}
