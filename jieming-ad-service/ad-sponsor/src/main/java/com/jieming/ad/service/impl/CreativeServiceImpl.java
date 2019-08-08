package com.jieming.ad.service.impl;

import com.jieming.ad.dao.CreativeRepository;
import com.jieming.ad.entity.Creative;
import com.jieming.ad.service.ICreativeService;
import com.jieming.ad.vo.CreativeRequest;
import com.jieming.ad.vo.CreativeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Qinyi.
 */
@Service
public class CreativeServiceImpl implements ICreativeService {

    private final CreativeRepository creativeRepository;

    @Autowired
    public CreativeServiceImpl(CreativeRepository creativeRepository) {
        this.creativeRepository = creativeRepository;
    }


    // 创建创意
    @Override
    public CreativeResponse createCreative(CreativeRequest request) {

        Creative creative = creativeRepository.save(
                request.convertToEntity()
        );

        return new CreativeResponse(creative.getId(), creative.getName());
    }
}
