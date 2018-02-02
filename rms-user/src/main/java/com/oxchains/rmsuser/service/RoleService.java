package com.oxchains.rmsuser.service;

import com.oxchains.rmsuser.common.RestResp;
import com.oxchains.rmsuser.common.RestRespPage;
import com.oxchains.rmsuser.dao.RolePermissionRepo;
import com.oxchains.rmsuser.dao.RoleRepo;
import com.oxchains.rmsuser.dao.UserRoleRepo;
import com.oxchains.rmsuser.entity.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ccl
 * @time 2018-01-30 15:22
 * @name RoleService
 * @desc:
 */
@Slf4j
@Service
public class RoleService {
    @Resource
    private RoleRepo roleRepo;

    @Resource
    private RolePermissionRepo rolePermissionRepo;

    @Resource
    private UserRoleRepo userRoleRepo;

    public RestResp findRoles(String roleName, Integer pageNo, Integer pageSize){
        pageNo = pageNo == null?1:pageNo;
        pageSize = pageSize == null ?10 :pageSize;
        Pageable pager = new PageRequest((pageNo-1)*pageSize, pageSize);
        try{
            Page<Role> roles = null;
            if(null != roleName && !"".equals(roleName.trim())){
                roles = roleRepo.findByRoleNameLike(roleName, pager);
            }else {
                roles =  roleRepo.findAll(pager);
            }
            return RestRespPage.success(roles.getContent(),roles.getTotalElements());
        }catch (Exception e){
            log.error("查询角色失败",e);
            return RestResp.fail("查询角色失败");
        }

    }

    public RestResp addRole(Role role){
        if(null == role || null == role.getRoleName() || "".equals(role.getRoleName().trim())){
            return RestResp.fail("角色名称不能为空");
        }
        try{
            Role r = getRole(role);
            if(null != r){
                return RestResp.fail("角色已经存在，请重新添加");
            }
            role = roleRepo.save(role);
            return RestResp.success("添加角色成功",role);
        }catch (Exception e){
            log.error("添加角色失败",e);
            return RestResp.fail("添加角色失败");
        }
    }

    public RestResp updateRole(Role role){
        try{
            role = roleRepo.save(role);
            return RestResp.success("更新成功",role);
        }catch (Exception e){
            log.error("更新失败",e);
            return RestResp.fail("更新失败");
        }
    }

    public RestResp deleteRole(Role role){
        if(null == role || role.getId()==null){
            return RestResp.fail("操作有误");
        }
        try{
            int n = 0;
            n = userRoleRepo.countByRoleId(role.getId());
            if(n>0){
                return RestResp.fail("角色已和用户关联");
            }

            n = rolePermissionRepo.countByRoleId(role.getId());
            if(n>0){
                return RestResp.fail("角色已和权限关联");
            }
             roleRepo.delete(role);
            return RestResp.success("删除成功",role);
        }catch (Exception e){
            log.error("删除失败",e);
            return RestResp.fail("删除失败");
        }
    }



    public Role getRole(Role role){
        if(null == role){
            return null;
        }
        if(null != role.getId()){
            return roleRepo.findOne(role.getId());
        }
        if(null != role.getRoleName()){
            return roleRepo.findByRoleName(role.getRoleName());
        }
        if(null != role.getRoleSign()){
            return roleRepo.findByRoleSign(role.getRoleSign());
        }
        return null;
    }
}
