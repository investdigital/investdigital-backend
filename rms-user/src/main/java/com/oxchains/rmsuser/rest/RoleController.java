package com.oxchains.rmsuser.rest;

import com.oxchains.rmsuser.common.RestResp;
import com.oxchains.rmsuser.common.RestRespPage;
import com.oxchains.rmsuser.entity.Role;
import com.oxchains.rmsuser.service.RoleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author ccl
 * @time 2018-01-30 15:15
 * @name RoleController
 * @desc:
 */
@RestController
@RequestMapping(value = "/role")
public class RoleController {
    @Resource
    private RoleService roleService;

    @GetMapping(value = "/list")
    public RestResp list(String roleName, Integer pageNo, Integer pageSize){
        return roleService.findRoles(roleName,pageNo,pageSize);
    }

    @PostMapping(value = "/add")
    public RestResp add(@RequestBody Role role){
        return roleService.addRole(role);
    }

    @PutMapping(value = "/update")
    public RestResp update(Role role){
        return roleService.updateRole(role);
    }

    @PostMapping(value = "/delete")
    public RestResp delete(@RequestBody Role role){
        return roleService.deleteRole(role);
    }
}
