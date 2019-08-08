package com.jieming.ad.mysql;

import com.alibaba.fastjson.JSON;
import com.jieming.ad.constant.OpType;
import com.jieming.ad.dto.ParseTemplate;
import com.jieming.ad.dto.TableTemplate;
import com.jieming.ad.dto.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

// 一开始就进行加载 哪个表的哪些字段需要被监听
@Slf4j
@Component
public class TemplateHolder {

    private ParseTemplate parseTemplate;
    @Autowired
    private  JdbcTemplate jdbcTemplate;

    private String SQL_SCHEMA = "SELECT table_schema,table_name,column_name,ordinal_position " +
                                "from information_schema.columns " +
                                "where table_schema = ? " +
                                 "and table_name = ?";

    // 通过tableName获取parseTemplate的tableTemplate
    public TableTemplate getTable(String tableName){
        return this.parseTemplate.getTableTemplateMap().get(tableName);
    }

    // 先加载json文件
    @PostConstruct
    private void init(){
        this.loadJson("template.json");
    }

    private void loadJson(String path){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);

        try{
            // 将json文件的内容填充到Templare对象
            Template template = JSON.parseObject(
                    inputStream,
                    Charset.defaultCharset(),
                    Template.class
            );
             // 解析Template对象，得到有 监听哪个数据库，哪个数据表的哪个字段的信息对象
            this.parseTemplate = ParseTemplate.parse(template);
            this.loadMeta();
        }catch (IOException ex){
            log.error(ex.getMessage());
            throw new RuntimeException("faile to parse json file");
        }
    }

    //加载数据
    private void loadMeta(){
        for (Map.Entry<String, TableTemplate> entry: this.parseTemplate.getTableTemplateMap().entrySet()
             ) {
            TableTemplate tableTemplate = entry.getValue();

            List<String> updateFields = tableTemplate.getOpTypeFieldSetMap().get(OpType.UPDATE);
            List<String> insertFields = tableTemplate.getOpTypeFieldSetMap().get(OpType.ADD);
            List<String> deleteFields = tableTemplate.getOpTypeFieldSetMap().get(OpType.DELETE);

            jdbcTemplate.query(SQL_SCHEMA,
                                new Object[]{parseTemplate.getDatabase(),tableTemplate.getTableName()},
                                (rs,i)->{
                                    int pos = rs.getInt("ordinal_position");
                                    String colName = rs.getString("column_name");
                                    if(
                                            (null!=updateFields && updateFields.contains(colName)) ||
                                                    (null != insertFields && insertFields.contains(colName) ||
                                                            (null!= deleteFields && deleteFields.contains(colName)))
                                    ){
                                        // 将column的下标和名字一一对应
                                        tableTemplate.getPosMap().put(pos-1,colName);
                                    }
                                    return null;
                                });
        }
    }

}
