package com.oxchains.rmsuser.dao;

import com.oxchains.rmsuser.entity.RoleResource;
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
public interface RoleResourceRepo extends CrudRepository<RoleResource,Long> {
    List<RoleResource> findByRoleId(Long roleId);
}
