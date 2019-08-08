package com.jieming.ad.constant;

import com.github.shyiko.mysql.binlog.event.EventType;

//枚举各种操作
public enum OpType {
    ADD,
    UPDATE,
    DELETE,
    OTHER;

    // 将binlog监听到的操作类型转化为自定义的操作类型
    public static OpType to(EventType eventType){
        switch (eventType){
            case EXT_WRITE_ROWS:
                return ADD;
            case EXT_DELETE_ROWS:
                return DELETE;
            case EXT_UPDATE_ROWS:
                return UPDATE;
                default:
                    return OTHER;
        }
    }
}
