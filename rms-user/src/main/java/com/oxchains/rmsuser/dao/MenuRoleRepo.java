package com.oxchains.rmsuser.dao;

import com.oxchains.rmsuser.entity.MenuRole;
import com.oxchains.rmsuser.entity.RolePermission;
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
public interface MenuRoleRepo extends CrudRepository<MenuRole,Long> {

    List<MenuRole> findByRoleId(Long roleId);
    MenuRole findByMenuIdAndRoleId(Long menuId, Long roleId);
}
