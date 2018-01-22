package com.oxchains.rmsuser.rest;

import com.oxchains.rmsuser.common.RestResp;
import com.oxchains.rmsuser.entity.Permission;
import com.oxchains.rmsuser.service.PermissionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author luoxuri
 * @create 2018-01-10 11:24
 **/
@RestController
@RequestMapping(value = "/permission")
public class PermissionController {

    @Resource private PermissionService permissionService;

    @PostMapping("/addURI")
    public RestResp addURI(@RequestBody Permission permission){
        return permissionService.addURI(permission);
    }

    @DeleteMapping("/delete/{permissionId}")
    public RestResp deleteURI(@PathVariable Long permissionId){
        return permissionService.deleteURI(permissionId);
    }

    @PutMapping("/update/{permissionId}")
    public RestResp updateURI(@PathVariable Long permissionId, @RequestBody Permission permission){
        return permissionService.updateURI(permissionId, permission);
    }

    @GetMapping("/queryAdmin/all")
    public RestResp queryAllURI(){
        return permissionService.queryAllURI();
    }

    @GetMapping("queryUser/{userId}")
    public RestResp userQueryUri(@PathVariable Long userId){
        return permissionService.userQueryUri(userId);
    }

    @GetMapping("/authUri/{permissionId}/{toId}")
    public RestResp authURI(@PathVariable Long permissionId, @PathVariable Long toId){
        return permissionService.authURI(permissionId, toId);
    }

    @GetMapping("/authUser/{permissionId}/{toId}")
    public RestResp authUser(@PathVariable Long permissionId, @PathVariable Long toId){
        return permissionService.authUser(permissionId, toId);
    }

    @GetMapping("/deleteAuthUser/{permissionId}/{toId}")
    public RestResp deleteAuthUser(@PathVariable Long permissionId, @PathVariable Long toId){
        return permissionService.deleteAuthUser(permissionId, toId);
    }
}
