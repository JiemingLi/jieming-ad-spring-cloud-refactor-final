package com.jieming.ad.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;


// 查询具体的推广计划的请求对象
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdPlanRequest {

    private Long id;
    private Long userId;
    private String planName;
    private String startDate;
    private String endDate;

    //创建的时候四个字段都不可以为空
    public boolean createValidate() {

        return userId != null
                && !StringUtils.isEmpty(planName)
                && !StringUtils.isEmpty(startDate)
                && !StringUtils.isEmpty(endDate);
    }
    //更新：字段id和userId不为空即可
    public boolean updateValidate() {

        return id != null && userId != null;
    }

    //删除：字段id和userId不为空即可
    public boolean deleteValidate() {

        return id != null && userId != null;
    }
}
