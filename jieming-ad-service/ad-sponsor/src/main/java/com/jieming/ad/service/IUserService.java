package com.jieming.ad.service;

import com.jieming.ad.exception.AdException;
import com.jieming.ad.vo.CreateUserRequest;
import com.jieming.ad.vo.CreateUserResponse;

/**
 * Created by Qinyi.
 */
public interface IUserService {

    /**
     * <h2>创建用户</h2>
     * */
    CreateUserResponse createUser(CreateUserRequest request)
            throws AdException;
}
