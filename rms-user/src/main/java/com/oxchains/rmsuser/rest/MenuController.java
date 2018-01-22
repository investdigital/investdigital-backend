package com.oxchains.rmsuser.rest;

import com.oxchains.rmsuser.common.RestResp;
import com.oxchains.rmsuser.service.MenuService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author luoxuri
 * @create 2018-01-09 14:34
 **/
@RestController
@RequestMapping(value = "/menu")
public class MenuController {

    @Resource private MenuService menuService;

    @GetMapping(value = "/list/{userId}")
    public RestResp menuList(@PathVariable Long userId){
        return menuService.menuList(userId);
    }

    @GetMapping(value = "/admin/list")
    public RestResp adminMenuList(){
        return menuService.adminMenuList();
    }

    @GetMapping(value = "/add/{name}/{pid}")
    public RestResp addMenu(@PathVariable String name, @PathVariable Long pid){
        return menuService.addMenu(name, pid);
    }

    @GetMapping(value = "/delete/{menuId}")
    public RestResp deleteMenu(@PathVariable Long menuId){
        return menuService.deleteMenu(menuId);
    }

    @GetMapping(value = "/update/{menuId}/{name}")
    public RestResp updateMenu(@PathVariable Long menuId, @PathVariable String name){
        return menuService.updateMenu(menuId, name);
    }

    @GetMapping(value = "/authMenu/{menuId}/{toId}")
    public RestResp authMenu(@PathVariable Long menuId, @PathVariable Long toId){
        return menuService.authMenu(menuId, toId);
    }

}
