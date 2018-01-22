package com.oxchains.rmsuser.dao;

import com.oxchains.rmsuser.entity.Permission;
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
public interface PermissionRepo extends CrudRepository<Permission,Long> {

    Permission findByUrl(String uri);

    @Query("SELECT p FROM Permission as p WHERE id IN(SELECT permissionId FROM RolePermission rp WHERE rp.roleId IN(SELECT roleId FROM UserRole ur WHERE ur.userId=?1))")
    List<Permission> findByUserId(@Param("userId") Long userId);

}
