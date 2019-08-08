package com.jieming.ad.consumer;

import com.alibaba.fastjson.JSON;
import com.jieming.ad.dto.MySqlRowData;
import com.jieming.ad.sender.ISender;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

//消费BinLogMessage
@Slf4j
@Component
public class BinlogConsumer {

    @Autowired
    private ISender sender;

    @KafkaListener(topics = {"ad-search-mysql-data"},groupId = "ad-search")
    public void processMySqlRowData(ConsumerRecord<?,?> record){
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if(kafkaMessage.isPresent()){
            Object message = kafkaMessage.get();
            MySqlRowData rowData = JSON.parseObject(message.toString(), MySqlRowData.class);
            log.info("kafka process mysqlRowData:{}",JSON.toJSONString(rowData));
            sender.sender(rowData);
        }
    }
}
