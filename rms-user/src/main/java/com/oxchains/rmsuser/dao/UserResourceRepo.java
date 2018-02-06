package com.oxchains.rmsuser.dao;

import com.oxchains.rmsuser.entity.Resource;
import com.oxchains.rmsuser.entity.UserResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ccl
 * @time 2018-02-02 18:10
 * @name ResourceRepo
 * @desc:
 */
@Repository
public interface UserResourceRepo extends CrudRepository<UserResource,Long> {
    List<UserResource> findByUserId(Long userId);

    int deleteByUserId(Long userId);
    int deleteByUserIdAndResourceIdIn(Long userId,List<Long> resourceIds);
}
