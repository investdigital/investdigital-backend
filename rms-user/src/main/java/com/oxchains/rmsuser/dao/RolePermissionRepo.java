package com.oxchains.rmsuser.dao;

import com.oxchains.rmsuser.entity.RolePermission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ccl
 * @time 2017-12-12 17:10
 * @name UserRepo
 * @desc:
 */
@Repository
public interface RolePermissionRepo extends CrudRepository<RolePermission,Long> {

    List<RolePermission> findByRoleId(Long roleId);
    RolePermission findByRoleIdAndPermissionId(Long roleId, Long permissionId);

    @Query(" SELECT rp FROM RolePermission as rp WHERE rp.roleId IN(SELECT roleId FROM UserRole as ur WHERE ur.userId=?1) ")
    List<RolePermission> queryRolePermissionByUserId(@Param("userId") Long userId);
}
