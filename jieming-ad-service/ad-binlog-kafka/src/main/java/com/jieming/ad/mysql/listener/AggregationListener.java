package com.jieming.ad.mysql.listener;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.jieming.ad.mysql.TemplateHolder;
import com.jieming.ad.dto.BinlogRowData;
import com.jieming.ad.dto.TableTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

// 此监听器监听binlog，也就是监听数据库所有的数据表发生了变化，就会触发onEvent方法
@Slf4j
@Component
public class AggregationListener implements BinaryLogClient.EventListener {
    private String dbName;
    private String tableName;
    @Autowired
    private TemplateHolder templateHolder;
    private Map<String,Ilistener>  listenerMap = new HashMap<>();


    private String genKey(String dbName,String tableName){
        return dbName + ":" + tableName;
    }

    // 此功能主要说明的是 哪个监听器监听了哪个数据库的哪个表
    public void register(String _dbName, String _tableName,
                         Ilistener ilistener){
        log.info("register : {}-{}", _dbName, _tableName);
        this.listenerMap.put(genKey(_dbName, _tableName), ilistener);
    }

    // 数据库有变化就触发此方法
    @Override
    public void onEvent(Event event) {
        // 获取信息类型
        EventType type = event.getHeader().getEventType();
        log.info("event type: {}", type);

        if (type == EventType.TABLE_MAP) {
            TableMapEventData data = event.getData();
            this.tableName = data.getTable();
            this.dbName = data.getDatabase();
            return;
        }

        if (type != EventType.EXT_UPDATE_ROWS
                && type != EventType.EXT_WRITE_ROWS
                && type != EventType.EXT_DELETE_ROWS) {
            return;
        }

        // 表名和库名是否已经完成填充
        if (StringUtils.isEmpty(dbName) || StringUtils.isEmpty(tableName)) {
            log.error("no meta data event");
            return;
        }

        // 找出对应表有兴趣的监听器
        String key = genKey(this.dbName, this.tableName);
        Ilistener listener = this.listenerMap.get(key);

        if (null == listener) {
            log.debug("skip {}", key);
            return;
        }
        log.info("trigger event: {}", type.name());
        try {
            BinlogRowData rowData = buildRowData(event.getData());
            if (rowData == null) {
                return;
            }
            // binrowData设置发生变化的类型
            rowData.setEventType(type);
            // 回调（IncrementListener）的onEvent方法
            listener.onEvent(rowData);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
        } finally {
            this.dbName = "";
            this.tableName = "";
        }
    }

    // 通过data获取对应的列
    private List<Serializable[]> getAfterValues(EventData eventData) {

        if (eventData instanceof WriteRowsEventData) {
            return ((WriteRowsEventData) eventData).getRows();
        }

        if (eventData instanceof UpdateRowsEventData) {
            return ((UpdateRowsEventData) eventData).getRows().stream()
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }

        if (eventData instanceof DeleteRowsEventData) {
            return ((DeleteRowsEventData) eventData).getRows();
        }

        return Collections.emptyList();
    }

    // 创建 BinlogRowData
    private BinlogRowData buildRowData(EventData data) {
        // 实质就是通过ParseTemplate对象获取tableTemplate
        TableTemplate tableTemplate = templateHolder.getTable(tableName);

        if(null == tableTemplate){
            log.warn("tableTemplate {} not found" ,tableName);
            return null;
        }

        // 用来存储多个binlog监听到的 被影响的列名：被影响的列的下标
        List<Map<String, String>> afterMapList = new ArrayList<>();

        /*
        * rows=[
                [1, 10, plan, 1,
                *  Tue Jan 01 08:00:00 CST 2019,
                *  Tue Jan 01 08:00:00 CST 2019,
                *  Tue Jan 01 08:00:00 CST 2019,
                *  Tue Jan 01 08:00:00 CST 2019]
          ]
        * */
        for (Serializable[] after : getAfterValues(data)) {

            Map<String, String> afterMap = new HashMap<>();

            // 发生变化的列的长度
            int colLen = after.length;

            for (int ix = 0; ix < colLen; ++ix) {

                // 取出当前位置对应的列名
                String colName = tableTemplate.getPosMap().get(ix);

                // 如果没有则说明不关心这个列
                if (null == colName) {
                    log.debug("ignore position: {}", ix);
                    continue;
                }

                String colValue = after[ix].toString();

                // 记录 列名 对应的 列下标
                afterMap.put(colName, colValue);
            }
            afterMapList.add(afterMap);
        }
        BinlogRowData rowData = new BinlogRowData();
        rowData.setAfter(afterMapList);
        rowData.setTableTemplate(tableTemplate);
        return rowData;
    }
}
