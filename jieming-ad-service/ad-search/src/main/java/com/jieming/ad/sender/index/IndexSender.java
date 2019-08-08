package com.jieming.ad.sender.index;

import com.alibaba.fastjson.JSON;
import com.jieming.ad.dump.table.*;
import com.jieming.ad.handler.AdLevelDataHandler;
import com.jieming.ad.index.DataLevel;
import com.jieming.ad.constant.Constant;
import com.jieming.ad.dto.MySqlRowData;
import com.jieming.ad.sender.ISender;
import com.jieming.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class IndexSender implements ISender {

    @Override
    public void sender(MySqlRowData mySqlRowData) {
        // 获取处理的等级
        String level = mySqlRowData.getLevel();

        if(DataLevel.LEVEL2.getLevel().equals(level)){
            System.err.println("处理等级2...........");
            this.Level2RowData(mySqlRowData);
        }else if(DataLevel.LEVEL3.getLevel().equals(level)){
            System.err.println("处理等级3...........");
            this.Level3RowData(mySqlRowData);
        }else if(DataLevel.LEVEL4.getLevel().equals(level)){
            System.err.println("处理等级4...........");
            this.Level4RowData(mySqlRowData);
        }else{
            //错误
            log.info("MySqlRowData ERROR {}", JSON.toJSONString(mySqlRowData));
        }
    }

    //处理第二层级mysqlRowData
    private void Level2RowData(MySqlRowData rowData){
        //将监听到ad_plan的数据转化为AdPlanTable，方便投递
        if (rowData.getTableName().equals(
                Constant.AD_PLAN_TABLE_INFO.TABLE_NAME)) {

            List<AdPlanTable> planTables = new ArrayList<>();

            for (Map<String, String> fieldValueMap :
                    rowData.getFieldValueMap()) {

                AdPlanTable planTable = new AdPlanTable();

                fieldValueMap.forEach((k, v) -> {
                    switch (k) {
                        case Constant.AD_PLAN_TABLE_INFO.COLUMN_ID:
                            planTable.setId(Long.valueOf(v));
                            break;
                        case Constant.AD_PLAN_TABLE_INFO.COLUMN_USER_ID:
                            planTable.setUserId(Long.valueOf(v));
                            break;
                        case Constant.AD_PLAN_TABLE_INFO.COLUMN_PLAN_STATUS:
                            planTable.setPlanStatus(Integer.valueOf(v));
                            break;
                        case Constant.AD_PLAN_TABLE_INFO.COLUMN_START_DATE:
                            planTable.setStartDate(
                                    CommonUtils.parseStringDate(v)
                            );
                            break;
                        case Constant.AD_PLAN_TABLE_INFO.COLUMN_END_DATE:
                            planTable.setEndDate(
                                    CommonUtils.parseStringDate(v)
                            );
                            break;
                    }
                });

                planTables.add(planTable);
            }

            planTables.forEach(p ->
                    AdLevelDataHandler.handleLevel2(p, rowData.getOpType()));

        } else if (rowData.getTableName().equals(  //将监听到ad_create的数据转化为AdCreativeTable，方便投递
                Constant.AD_CREATIVE_TABLE_INFO.TABLE_NAME
        )) {
            List<AdCreativeTable> creativeTables = new ArrayList<>();

            for (Map<String, String> fieldValeMap :
                    rowData.getFieldValueMap()) {

                AdCreativeTable creativeTable = new AdCreativeTable();

                fieldValeMap.forEach((k, v) -> {
                    switch (k) {
                        case Constant.AD_CREATIVE_TABLE_INFO.COLUMN_ID:
                            creativeTable.setAdId(Long.valueOf(v));
                            break;
                        case Constant.AD_CREATIVE_TABLE_INFO.COLUMN_TYPE:
                            creativeTable.setType(Integer.valueOf(v));
                            break;
                        case Constant.AD_CREATIVE_TABLE_INFO.COLUMN_MATERIAL_TYPE:
                            creativeTable.setMaterialType(Integer.valueOf(v));
                            break;
                        case Constant.AD_CREATIVE_TABLE_INFO.COLUMN_HEIGHT:
                            creativeTable.setHeight(Integer.valueOf(v));
                            break;
                        case Constant.AD_CREATIVE_TABLE_INFO.COLUMN_WIDTH:
                            creativeTable.setWidth(Integer.valueOf(v));
                            break;
                        case Constant.AD_CREATIVE_TABLE_INFO.COLUMN_AUDIT_STATUS:
                            creativeTable.setAuditStatus(Integer.valueOf(v));
                            break;
                        case Constant.AD_CREATIVE_TABLE_INFO.COLUMN_URL:
                            creativeTable.setAdUrl(v);
                            break;
                    }
                });

                creativeTables.add(creativeTable);
            }

            creativeTables.forEach(c ->
                    AdLevelDataHandler.handleLevel2(c, rowData.getOpType()));
        }
    }


    //处理第三层级
    private void Level3RowData(MySqlRowData rowData) {

        //将监听到ad_unit表的数据转化为AdUnitTable，方便投递
        if (rowData.getTableName().equals(
                Constant.AD_UNIT_TABLE_INFO.TABLE_NAME)) {

            List<AdUnitTable> unitTables = new ArrayList<>();

            for (Map<String, String> fieldValueMap :
                    rowData.getFieldValueMap()) {

                AdUnitTable unitTable = new AdUnitTable();

                fieldValueMap.forEach((k, v) -> {
                    switch (k) {
                        case Constant.AD_UNIT_TABLE_INFO.COLUMN_ID:
                            unitTable.setUnitId(Long.valueOf(v));
                            break;
                        case Constant.AD_UNIT_TABLE_INFO.COLUMN_UNIT_STATUS:
                            unitTable.setUnitStatus(Integer.valueOf(v));
                            break;
                        case Constant.AD_UNIT_TABLE_INFO.COLUMN_POSITION_TYPE:
                            unitTable.setPositionType(Integer.valueOf(v));
                            break;
                        case Constant.AD_UNIT_TABLE_INFO.COLUMN_PLAN_ID:
                            unitTable.setPlanId(Long.valueOf(v));
                            break;
                    }
                });

                unitTables.add(unitTable);
            }

            unitTables.forEach(u ->
                    AdLevelDataHandler.handleLevel3(u, rowData.getOpType()));
        } else if (rowData.getTableName().equals(
                Constant.AD_CREATIVE_UNIT_TABLE_INFO.TABLE_NAME //将监听到create_unit表的数据转化为AdCreativeUnitTable，方便投递
        )) {
            List<AdCreativeUnitTable> creativeUnitTables = new ArrayList<>();

            for (Map<String, String> fieldValueMap :
                    rowData.getFieldValueMap()) {

                AdCreativeUnitTable creativeUnitTable = new AdCreativeUnitTable();

                fieldValueMap.forEach((k, v) -> {
                    switch (k) {
                        case Constant.AD_CREATIVE_UNIT_TABLE_INFO.COLUMN_CREATIVE_ID:
                            creativeUnitTable.setAdId(Long.valueOf(v));
                            break;
                        case Constant.AD_CREATIVE_UNIT_TABLE_INFO.COLUMN_UNIT_ID:
                            creativeUnitTable.setUnitId(Long.valueOf(v));
                            break;
                    }
                });

                creativeUnitTables.add(creativeUnitTable);
            }

            creativeUnitTables.forEach(
                    u -> AdLevelDataHandler.handleLevel3(u, rowData.getOpType())
            );
        }
    }


    private void Level4RowData(MySqlRowData rowData) {

        switch (rowData.getTableName()) {

            case Constant.AD_UNIT_DISTRICT_TABLE_INFO.TABLE_NAME:
                List<AdUnitDistrictTable> districtTables = new ArrayList<>();

                for (Map<String, String> fieldValueMap :
                        rowData.getFieldValueMap()) {

                    AdUnitDistrictTable districtTable = new AdUnitDistrictTable();

                    fieldValueMap.forEach((k, v) -> {
                        switch (k) {
                            case Constant.AD_UNIT_DISTRICT_TABLE_INFO.COLUMN_UNIT_ID:
                                districtTable.setUnitId(Long.valueOf(v));
                                break;
                            case Constant.AD_UNIT_DISTRICT_TABLE_INFO.COLUMN_PROVINCE:
                                districtTable.setProvince(v);
                                break;
                            case Constant.AD_UNIT_DISTRICT_TABLE_INFO.COLUMN_CITY:
                                districtTable.setCity(v);
                                break;
                        }
                    });

                    districtTables.add(districtTable);
                }

                districtTables.forEach(
                        d -> AdLevelDataHandler.handleLevel4(d, rowData.getOpType())
                );

                break;
            case Constant.AD_UNIT_IT_TABLE_INFO.TABLE_NAME:
                List<AdUnitItTable> itTables = new ArrayList<>();

                for (Map<String, String> fieldValueMap :
                        rowData.getFieldValueMap()) {

                    AdUnitItTable itTable = new AdUnitItTable();

                    fieldValueMap.forEach((k, v) -> {
                        switch (k) {
                            case Constant.AD_UNIT_IT_TABLE_INFO.COLUMN_UNIT_ID:
                                itTable.setUnitId(Long.valueOf(v));
                                break;
                            case Constant.AD_UNIT_IT_TABLE_INFO.COLUMN_IT_TAG:
                                itTable.setItTag(v);
                                break;
                        }
                    });
                    itTables.add(itTable);
                }
                itTables.forEach(
                        i -> AdLevelDataHandler.handleLevel4(i, rowData.getOpType())
                );
                break;
            case Constant.AD_UNIT_KEYWORD_TABLE_INFO.TABLE_NAME:

                List<AdUnitKeywordTable> keywordTables = new ArrayList<>();

                for (Map<String, String> fieldValueMap :
                        rowData.getFieldValueMap()) {
                    AdUnitKeywordTable keywordTable = new AdUnitKeywordTable();

                    fieldValueMap.forEach((k, v) -> {
                        switch (k) {
                            case Constant.AD_UNIT_KEYWORD_TABLE_INFO.COLUMN_UNIT_ID:
                                keywordTable.setUnitId(Long.valueOf(v));
                                break;
                            case Constant.AD_UNIT_KEYWORD_TABLE_INFO.COLUMN_KEYWORD:
                                keywordTable.setKeyword(v);
                                break;
                        }
                    });
                    keywordTables.add(keywordTable);
                }

                keywordTables.forEach(
                        k -> AdLevelDataHandler.handleLevel4(k, rowData.getOpType())
                );
                break;
        }
    }
}
