package com.jieming.ad.constant;

import lombok.Getter;

//描述推广计划的状态
@Getter
public enum CommonStatus {
    VALID(1,"有效状态"),
    INVALID(0,"无效状态");

   private Integer status;
   private String desc;
   CommonStatus(Integer status,String desc){
       this.status = status;
       this.desc = desc;
   }
}
