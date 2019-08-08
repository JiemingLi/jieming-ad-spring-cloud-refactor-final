package com.jieming.ad.mysql.listener;

import com.github.shyiko.mysql.binlog.event.EventType;
import com.jieming.ad.constant.Constant;
import com.jieming.ad.constant.OpType;
import com.jieming.ad.dto.BinlogRowData;
import com.jieming.ad.dto.MySqlRowData;
import com.jieming.ad.dto.TableTemplate;
import com.jieming.ad.sender.ISender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class IncrementListener implements Ilistener {

    @Resource
    private ISender iSender;


    @Autowired
    private AggregationListener aggregationListener;


    // 负责将数据库名和表名的映射注册到监听器中
    // 本监听器监听所有的 表 中所发生的 增量
    // K:表名 ，V:数据库名
    // 一开始就把this监听了哪个数据库的哪个数据表的信息注册
    @Override
    @PostConstruct
    public void register() {
        Constant.table2Db.forEach(
                // 此功能主要说明的是 哪个监听器监听了哪个数据库的哪个表
                (K,V)->aggregationListener.register(V,K,this)
        );
    }

    @Override
    public void onEvent(BinlogRowData eventData) {
        // 获取BinlogData对象锁存储的tableTemplate对象
        TableTemplate tableTemplate = eventData.getTableTemplate();

        // 将BinlogRowData对象转为 MySqlRowData对象
        MySqlRowData mySqlRowData = new MySqlRowData();

        // 记录表名
        String tableName = tableTemplate.getTableName();
        // 记录处理等级
        String level = tableTemplate.getLevel();
        // 记录操作类型
        EventType opType = eventData.getEventType();

        //设置对应的属性
        mySqlRowData.setTableName(tableName);
        mySqlRowData.setLevel(level);
        mySqlRowData.setOpType(OpType.to(opType));

        // 这个tableTemplate是根据Binlog传递过来的信息中的tableName去查询对应的tableTemplate对象的
        // 也就是发生变化的column的值
        List<String> fieldList = tableTemplate.getOpTypeFieldSetMap().get(OpType.to(opType));

        if(null == fieldList){
            log.warn("{} not support for {}", opType, tableTemplate.getTableName());
            return;
        }

        // 将buildRowData时候创建的 after 属性的 值 复制到 MySqlRowData的fieldValueMap
        // 实质上fieldValueMap的内容和 binlogRowData的 after是 一样的
        for (Map<String, String> afterMap : eventData.getAfter()) {

            Map<String,String> _afterMap = new HashMap<>();

            for (Map.Entry<String,String> map: afterMap.entrySet()
                 ) {
                String colName = map.getKey();
                String colValue = map.getValue();
                _afterMap.put(colName,colValue);
            }

            mySqlRowData.getFieldValueMap().add(_afterMap);
        }
        // 将MySqlRowData对象发送出去给消息队列处理。
        iSender.sender(mySqlRowData);
    }
}
