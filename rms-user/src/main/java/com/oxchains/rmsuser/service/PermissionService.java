package com.oxchains.rmsuser.service;

import com.oxchains.rmsuser.common.RestResp;
import com.oxchains.rmsuser.dao.PermissionRepo;
import com.oxchains.rmsuser.dao.RolePermissionRepo;
import com.oxchains.rmsuser.dao.UserPermissionRepo;
import com.oxchains.rmsuser.dao.UserRoleRepo;
import com.oxchains.rmsuser.entity.Permission;
import com.oxchains.rmsuser.entity.RolePermission;
import com.oxchains.rmsuser.entity.UserPermission;
import com.oxchains.rmsuser.entity.UserRole;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author luoxuri
 * @create 2018-01-10 11:08
 **/
@Service
public class PermissionService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Resource private PermissionRepo permissionRepo;
    @Resource private UserRoleRepo userRoleRepo;
    @Resource private RolePermissionRepo rolePermissionRepo;
    @Resource private UserPermissionRepo userPermissionRepo;

    public RestResp addURI(Permission permission){
        try {
            String name = permission.getName();
            String uri = permission.getUrl();
            Permission permission1 = permissionRepo.findByUrl(uri);
            if (permission1 != null){
                return RestResp.fail("URI已存在");
            }
            Permission permission2 = new Permission(name, null, uri);
            Permission p = permissionRepo.save(permission2);
            return RestResp.success("操作成功", p);
        } catch (Exception e){
            LOG.error("add url failed: {}", e.getMessage(), e);
        }
        return RestResp.fail("操作失败");
    }

    public RestResp deleteURI(Long permissionId){
        try {
            permissionRepo.delete(permissionId);
            return RestResp.success("操作成功", permissionId);
        } catch (Exception e){
            LOG.error("delete URI failed: {}", e.getMessage(), e);
        }
        return RestResp.fail("操作失败");
    }

    public RestResp updateURI(Long permissionId, Permission permission){
        try {
            String name = permission.getName();
            String uri = permission.getUrl();
            Permission permission1 = permissionRepo.findOne(permissionId);
            permission1.setName(name);
            permission1.setUrl(uri);
            Permission permission2 = permissionRepo.save(permission1);
            return RestResp.success("操作成功", permission2);
        } catch (Exception e){
            LOG.error("update URI failed: {}", e.getMessage(), e);
        }
        return RestResp.fail("操作失败");
    }

    public RestResp queryAllURI(){
        try {
            Iterator<Permission >it = permissionRepo.findAll().iterator();
            if (it.hasNext()){
                return RestResp.success("操作成功", it);
            }
            return RestResp.success("操作成功", new ArrayList<>());
        } catch (Exception e){
            LOG.error("query all URI failed: {}", e.getMessage(), e);
        }
        return RestResp.fail("操作失败");
    }

    public RestResp userQueryUri(Long userId){
        try {
            // 将所有有权限的url 放在list中返回
            List<UserPermission> userPermissionList = userPermissionRepo.findByUserId(userId);
            Set<Permission> permissionSet = new HashSet<>();
            if (userPermissionList.size() != 0){
                userPermissionList.stream().forEach(u -> {
                    Long permissionId = u.getPermissionId();
                    Permission permission = permissionRepo.findOne(permissionId);
                    permissionSet.add(permission);
                });
            }

            List<Permission>permissionList = permissionRepo.findByUserId(userId);
            if (permissionList.size() != 0){
                permissionList.stream().forEach(p -> {
                    permissionSet.add(p);
                });
            }

            List<Permission> list = IteratorUtils.toList(permissionSet.iterator());
            if (list.size() != 0){
                return RestResp.success("操作成功", list);
            }
            return RestResp.success("操作成功", new ArrayList<>());
        } catch (Exception e){
            LOG.error("user query all uri failed: {}", e.getMessage(), e);
        }
        return RestResp.fail("操作失败");
    }

    public RestResp authURI(Long permissionId, Long toId){
        try {
            List<UserRole> userRoleList = userRoleRepo.findByUserId(toId);
            Set<Long> roleSet = new HashSet<>();
            userRoleList.stream().forEach(ur -> {
                Long roleId = ur.getRoleId();
                roleSet.add(roleId);
            });

            Iterator<Long> iterator = roleSet.iterator();
            RolePermission rolePermission1 = new RolePermission();
            while (iterator.hasNext()){
                Long roleId = iterator.next();
                RolePermission rolePermission = rolePermissionRepo.findByRoleIdAndPermissionId(roleId, permissionId);
                if (rolePermission == null){
                    rolePermission1.setRoleId(roleId);
                    rolePermission1.setPermissionId(permissionId);
                    rolePermissionRepo.save(rolePermission1);
                }
            }
            return RestResp.success("操作成功", rolePermission1);
        } catch (Exception e){
            LOG.error("auth URI failed: {}", e.getMessage(), e);
        }
        return RestResp.fail("操作失败");
    }

    public RestResp authUser(Long permissionId, Long toId){
        try {
            // 先查看角色权限表中有没权限
            List<RolePermission> rolePermissionList = rolePermissionRepo.queryRolePermissionByUserId(toId);
            if (rolePermissionList.size() != 0){
                for (RolePermission rp : rolePermissionList) {
                    if (rp.getPermissionId().equals(permissionId)){
                        return RestResp.fail("当前用户已经拥有权限");
                    }
                }
            }

            UserPermission userPermission = userPermissionRepo.findByUserIdAndPermissionId(toId, permissionId);
            if (userPermission == null){
                UserPermission up = new UserPermission(toId, permissionId);
                UserPermission save = userPermissionRepo.save(up);
                return RestResp.success("操作成功", save);
            }
            return RestResp.fail("权限存在");
        } catch (Exception e){
            LOG.error("auth user permission failed: {}", e.getMessage(), e);
        }
        return RestResp.fail("操作失败");
    }

    public RestResp deleteAuthUser(Long permissionId, Long toId){
        try {
            UserPermission userPermission = userPermissionRepo.findByUserIdAndPermissionId(toId, permissionId);
            if (userPermission != null){
                userPermissionRepo.delete(userPermission.getId());
                return RestResp.success("操作成功", userPermission);
            }
            return RestResp.fail("权限不存在");
        } catch (Exception e){
            LOG.error("delete user permission failed: {}", e.getMessage(), e);
        }
        return RestResp.fail("操作失败");
    }
}
