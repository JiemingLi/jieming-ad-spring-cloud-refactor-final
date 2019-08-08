package com.jieming.ad.dto;

import com.jieming.ad.constant.OpType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableTemplate {

    // 数据表名字
    private String tableName;
    // 处理的优先级，也就是各等级
    private String level;
    //描述了 哪个操作对哪些column感兴趣
    private Map<OpType, List<String>> opTypeFieldSetMap = new HashMap<>();
    // 字段索引 -> 字段名
    private Map<Integer, String> posMap = new HashMap<>();
}
