package com.jieming.ad.dto;

import com.jieming.ad.constant.OpType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParseTemplate {
    private String database;
    //key 就是 tableName，value是
    private Map<String,TableTemplate> tableTemplateMap = new HashMap<>();

    // 解析模板
    public static ParseTemplate parse(Template template){
        // 新建ParseTemplate对象
        ParseTemplate parseTemplate = new ParseTemplate();
        // 设置数据库
        parseTemplate.setDatabase(template.getDatabase());

        // 设置
        for (JsonTable jsonTable: template.getTableList()
             ) {
            //解析JsonTable对象的tableName和level属性
            String name = jsonTable.getTableName();
            Integer level = jsonTable.getLevel();
            TableTemplate tableTemplate = new TableTemplate();
            tableTemplate.setLevel(level.toString());
            tableTemplate.setTableName(name);

            parseTemplate.tableTemplateMap.put(name,tableTemplate);

            //解析JsonTable对象的操作列表，然后存储 什么操作 操作了 哪些column
            Map<OpType, List<String>> opTypeFieldSetMap = tableTemplate.getOpTypeFieldSetMap();
            //遍历操作类型所对应的列
            for (JsonTable.Column column: jsonTable.getInsert()
                 ) {
                //
                getAndCreateIfNeed(
                        OpType.ADD,
                        opTypeFieldSetMap,
                        ArrayList::new
                        ).add(column.getColumn());
            }

            for (JsonTable.Column column : jsonTable.getUpdate()) {
                getAndCreateIfNeed(
                        OpType.UPDATE,
                        opTypeFieldSetMap,
                        ArrayList::new
                ).add(column.getColumn());
            }

            for (JsonTable.Column column : jsonTable.getDelete()) {
                getAndCreateIfNeed(
                        OpType.DELETE,
                        opTypeFieldSetMap,
                        ArrayList::new
                ).add(column.getColumn());
            }
        }
        //返回解析后的结果
        return parseTemplate;
    }

    private static <T,R> R getAndCreateIfNeed(T key, Map<T,R>map, Supplier<R> factory){
        return map.computeIfAbsent(key,k->factory.get());
    }
}
