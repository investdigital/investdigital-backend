package com.oxchains.rmsuser.dao;

import com.oxchains.rmsuser.entity.RolePermission;
import com.oxchains.rmsuser.entity.UserPermission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ccl
 * @time 2017-12-12 17:10
 * @name UserRepo
 * @desc:
 */
@Repository
public interface UserPermissionRepo extends CrudRepository<UserPermission,Long> {

    UserPermission findByUserIdAndPermissionId(Long userId, Long permissionId);
    List<UserPermission> findByUserId(Long UserId);
}
