package com.jieming.ad.mysql.listener;

import com.jieming.ad.dto.BinlogRowData;

public interface Ilistener {

    // 对应不同的表有不同的对应监听方法，所以要注册不同的监听方法
    void register();

    //在调用此方法之前，就已经把原生数据转为BinlogRowDatal类型
    void onEvent(BinlogRowData eventData);
}
