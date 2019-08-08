package com.jieming.ad.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;


// 查询推广单元的请求对象
// 主要是通过userId和推广单元的id请求
// 一个用户可以投放多个推广单元
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdPlanGetRequest {

    private Long userId;
    private List<Long> ids;

    public boolean validate() {

        return userId != null && !CollectionUtils.isEmpty(ids);
    }
}
