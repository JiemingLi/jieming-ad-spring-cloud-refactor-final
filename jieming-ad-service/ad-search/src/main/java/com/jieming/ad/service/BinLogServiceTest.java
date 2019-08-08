package com.jieming.ad.service;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;

/*
*
Write---------------
WriteRowsEventData{tableId=109, includedColumns={0, 1, 2, 3, 4, 5, 6, 7}, rows=[
    [1, 10, plan, 1, Tue Jan 01 08:00:00 CST 2019, Tue Jan 01 08:00:00 CST 2019, Tue Jan 01 08:00:00 CST 2019, Tue Jan 01 08:00:00 CST 2019]
]}
* */

// Tue Jan 01 08:00:00 CST 2019

public class BinLogServiceTest {

    public static void main(String[] args) throws Exception {

        BinaryLogClient client = new BinaryLogClient(
                "127.0.0.1",
                3306,
                "root",
                "123456"
        );
//        client.setBinlogFilename("binlog.000037");
//        client.setBinlogPosition();

        client.registerEventListener(event -> {

            EventData data = event.getData();

            if (data instanceof UpdateRowsEventData) {
                System.out.println("Update--------------");
                System.out.println(data.toString());
            } else if (data instanceof WriteRowsEventData) {
                System.out.println("Write---------------");
                System.out.println(data.toString());
            } else if (data instanceof DeleteRowsEventData) {
                System.out.println("Delete--------------");
                System.out.println(data.toString());
            }
        });

        client.connect();
    }
}
