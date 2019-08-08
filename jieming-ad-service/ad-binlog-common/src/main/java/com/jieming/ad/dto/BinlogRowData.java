package com.jieming.ad.dto;

import com.github.shyiko.mysql.binlog.event.EventType;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//本类主要是将Binlog监听到的event转为在程序中所定义的Java对象
@Data
public class BinlogRowData {
    //监听是哪个表的操作
    private TableTemplate tableTemplate;

    //操作的类型
    private EventType eventType;

    //map： 操作的列 所对应的 操作的值。
    //一次操作会影响到多个列所对应的值，所以用ArrayList保存
    //此字段表示是更新之后的值
    private List<Map<String,String>> after = new ArrayList<>();

    private List<Map<String,String>> before = new ArrayList<>();


}
