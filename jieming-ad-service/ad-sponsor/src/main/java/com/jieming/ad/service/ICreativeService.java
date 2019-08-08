package com.jieming.ad.service;

import com.jieming.ad.vo.CreativeRequest;
import com.jieming.ad.vo.CreativeResponse;

/**
 * Created by Qinyi.
 */
public interface ICreativeService {

    CreativeResponse createCreative(CreativeRequest request);
}
