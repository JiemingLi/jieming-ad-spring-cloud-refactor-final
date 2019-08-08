package com.jieming.ad.service.impl;

import com.jieming.ad.constant.Constants;
import com.jieming.ad.dao.AdPlanRepository;
import com.jieming.ad.dao.AdUnitRepository;
import com.jieming.ad.dao.CreativeRepository;
import com.jieming.ad.dao.unit_condition.AdUnitDistrictRepository;
import com.jieming.ad.dao.unit_condition.AdUnitItRepository;
import com.jieming.ad.dao.unit_condition.AdUnitKeywordRepository;
import com.jieming.ad.dao.unit_condition.CreativeUnitRepository;
import com.jieming.ad.entity.AdPlan;
import com.jieming.ad.entity.AdUnit;
import com.jieming.ad.entity.unit_condition.AdUnitDistrict;
import com.jieming.ad.entity.unit_condition.AdUnitIt;
import com.jieming.ad.entity.unit_condition.AdUnitKeyword;
import com.jieming.ad.entity.unit_condition.CreativeUnit;
import com.jieming.ad.exception.AdException;
import com.jieming.ad.service.IAdUnitService;
import com.jieming.ad.vo.AdUnitDistrictRequest;
import com.jieming.ad.vo.AdUnitDistrictResponse;
import com.jieming.ad.vo.AdUnitItRequest;
import com.jieming.ad.vo.AdUnitItResponse;
import com.jieming.ad.vo.AdUnitKeywordRequest;
import com.jieming.ad.vo.AdUnitKeywordResponse;
import com.jieming.ad.vo.AdUnitRequest;
import com.jieming.ad.vo.AdUnitResponse;
import com.jieming.ad.vo.CreativeUnitRequest;
import com.jieming.ad.vo.CreativeUnitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by jieming.
 */
@Service
public class AdUnitServiceImpl implements IAdUnitService {

    private final AdPlanRepository planRepository;
    private final AdUnitRepository unitRepository;

    private final AdUnitKeywordRepository unitKeywordRepository;
    private final AdUnitItRepository unitItRepository;
    private final AdUnitDistrictRepository unitDistrictRepository;

    private final CreativeRepository creativeRepository;
    private final CreativeUnitRepository creativeUnitRepository;

    @Autowired
    public AdUnitServiceImpl(AdPlanRepository planRepository,
                             AdUnitRepository unitRepository,
                             AdUnitKeywordRepository unitKeywordRepository,
                             AdUnitItRepository unitItRepository,
                             AdUnitDistrictRepository unitDistrictRepository, CreativeRepository creativeRepository, CreativeUnitRepository creativeUnitRepository) {
        this.planRepository = planRepository;
        this.unitRepository = unitRepository;
        this.unitKeywordRepository = unitKeywordRepository;
        this.unitItRepository = unitItRepository;
        this.unitDistrictRepository = unitDistrictRepository;
        this.creativeRepository = creativeRepository;
        this.creativeUnitRepository = creativeUnitRepository;
    }

    //创建单元计划
    @Override
    public AdUnitResponse createUnit(AdUnitRequest request)
            throws AdException {

        // 验证参数是否为空
        if (!request.createValidate()) {
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        //判断对应的推广计划是否存在
        Optional<AdPlan> adPlan =
                planRepository.findById(request.getPlanId());

        if (!adPlan.isPresent()) {
            throw new AdException(Constants.ErrorMsg.CAN_NOT_FIND_RECORD);
        }

        //如果已经存在推广单元，则抛出异常
        AdUnit oldAdUnit = unitRepository.findByPlanIdAndUnitName(
                request.getPlanId(), request.getUnitName()
        );
        if (oldAdUnit != null) {
            throw new AdException(Constants.ErrorMsg.SAME_NAME_UNIT_ERROR);
        }

        //保存推广单元到数据库
        AdUnit newAdUnit = unitRepository.save(
                new AdUnit(request.getPlanId(), request.getUnitName(),
                        request.getPositionType(), request.getBudget())
        );

        return new AdUnitResponse(newAdUnit.getId(),
                newAdUnit.getUnitName());
    }

    // 创建关键词推广单元
    @Override
    public AdUnitKeywordResponse createUnitKeyword(
            AdUnitKeywordRequest request) throws AdException {

        //获取传递进来的 关键词推广单元 所对应的 推广单元 的id
        List<Long> unitIds = request.getUnitKeywords().stream()
                .map(AdUnitKeywordRequest.UnitKeyword::getUnitId)
                .collect(Collectors.toList());

        //如果不存在unitIds，直接抛出异常
        if (!isRelatedUnitExist(unitIds)) {
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        //用于保存增加后的 unitIds
        List<Long> ids = Collections.emptyList();

        // 副本，用来保存 传进来的 request的UnitKeywords 所遍历的值
        List<AdUnitKeyword> unitKeywords = new ArrayList<>();

        if (!CollectionUtils.isEmpty(request.getUnitKeywords())) {

            request.getUnitKeywords().forEach(i -> unitKeywords.add(
                    new AdUnitKeyword(i.getUnitId(), i.getKeyword())
            ));
            // 得到最新的ids
            ids = unitKeywordRepository.saveAll(unitKeywords).stream()
                    .map(AdUnitKeyword::getId)
                    .collect(Collectors.toList());
        }
        return new AdUnitKeywordResponse(ids);
    }

    // 创建兴趣单元
    @Override
    public AdUnitItResponse createUnitIt(
            AdUnitItRequest request) throws AdException {

        List<Long> unitIds = request.getUnitIts().stream()
                .map(AdUnitItRequest.UnitIt::getUnitId)
                .collect(Collectors.toList());

        if (!isRelatedUnitExist(unitIds)) {
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        List<AdUnitIt> unitIts = new ArrayList<>();

        request.getUnitIts().forEach(i -> unitIts.add(
                new AdUnitIt(i.getUnitId(), i.getItTag())
        ));

        List<Long> ids = unitItRepository.saveAll(unitIts).stream()
                .map(AdUnitIt::getId)
                .collect(Collectors.toList());

        return new AdUnitItResponse(ids);
    }

    @Override
    public AdUnitDistrictResponse createUnitDistrict(
            AdUnitDistrictRequest request) throws AdException {

        List<Long> unitIds = request.getUnitDistricts().stream()
                .map(AdUnitDistrictRequest.UnitDistrict::getUnitId)
                .collect(Collectors.toList());

        if (!isRelatedUnitExist(unitIds)) {
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        List<AdUnitDistrict> unitDistricts = new ArrayList<>();

        request.getUnitDistricts().forEach(d -> unitDistricts.add(
                new AdUnitDistrict(d.getUnitId(), d.getProvince(),
                        d.getCity())
        ));
        List<Long> ids = unitDistrictRepository.saveAll(unitDistricts)
                .stream().map(AdUnitDistrict::getId)
                .collect(Collectors.toList());

        return new AdUnitDistrictResponse(ids);
    }

    // 创建 创意和推广单元 的对象
    @Override
    public CreativeUnitResponse createCreativeUnit(
            CreativeUnitRequest request) throws AdException {

        //查找推广单元的id
        List<Long> unitIds = request.getUnitItems().stream()
                .map(CreativeUnitRequest.CreativeUnitItem::getUnitId)
                .collect(Collectors.toList());

        //查找创意的id
        List<Long> creativeIds = request.getUnitItems().stream()
                .map(CreativeUnitRequest.CreativeUnitItem::getCreativeId)
                .collect(Collectors.toList());

        // 判断推广单元的id和创意的id是否存在
        if (!(isRelatedUnitExist(unitIds) && isRelatedCreativeExist(creativeIds))) {
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }


        List<CreativeUnit> creativeUnits = new ArrayList<>();

        request.getUnitItems().forEach(i -> creativeUnits.add(
                new CreativeUnit(i.getCreativeId(), i.getUnitId())
        ));

        List<Long> ids = creativeUnitRepository.saveAll(creativeUnits)
                .stream()
                .map(CreativeUnit::getId)
                .collect(Collectors.toList());

        return new CreativeUnitResponse(ids);
    }

    // 查找推广单元
    private boolean isRelatedUnitExist(List<Long> unitIds) {

        if (CollectionUtils.isEmpty(unitIds)) {
            return false;
        }

        // 查找推广单元
        return unitRepository.findAllById(unitIds).size() ==
                new HashSet<>(unitIds).size();
    }

    private boolean isRelatedCreativeExist(List<Long> creativeIds) {

        if (CollectionUtils.isEmpty(creativeIds)) {
            return false;
        }

        return creativeRepository.findAllById(creativeIds).size() ==
                new HashSet<>(creativeIds).size();
    }
}
