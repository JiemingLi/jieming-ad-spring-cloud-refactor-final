package com.jieming.ad.dto;

import com.jieming.ad.constant.OpType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MySqlRowData {
    private String tableName;
    private String level;
    private OpType opType;
    //字段索引到字段具体的名字的映射
    private List<Map<String,String>> fieldValueMap = new ArrayList<>();
}
