package com.oxchains.rmsuser.service;

import com.oxchains.rmsuser.common.RestResp;
import com.oxchains.rmsuser.dao.*;
import com.oxchains.rmsuser.entity.*;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author luoxuri
 * @create 2018-01-09 14:37
 **/
@Service
public class MenuService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Resource private UserRoleRepo userRoleRepo;
    @Resource private MenuRoleRepo menuRoleRepo;
    @Resource private MenuRepo menuRepo;

    /** 根据user角色获取所有菜单列表 */
    public RestResp menuList(Long userId){
        try {
            // 查询所有
            List<Menu> menuList = menuRepo.findByPid(0L);
            for (Menu m: menuList) {
                m.setChildMenu(menuRepo.findByPid(m.getId()));
            }

            // userId有权限的
            Set<Long> set = new HashSet<>();

            List<UserRole> userRoleList = userRoleRepo.findByUserId(userId);
            if (userRoleList.size() != 0){
                userRoleList.stream().forEach(m -> {
                    List<MenuRole> menuRoleList = menuRoleRepo.findByRoleId(m.getRoleId());
                    menuRoleList.stream().forEach(w -> {
                        // 将所有有权限的 menuId 放到set中
                        set.add(w.getMenuId());
                    });
                });

                List<Menu> list = new ArrayList<>();
                List<Long> setList = IteratorUtils.toList(set.iterator());
                menuList.stream().forEach(l -> {
                    if (setList.contains(l.getId())){
                        // 将所有有权限的一级菜单放到 list
                        list.add(l);
                    }
                });

                // 遍历一级菜单
                list.stream().forEach(l -> {
                    // 获取二级菜单
                    List<Menu> list1 = l.getChildMenu();
                    List<Menu> list2 = new ArrayList<>();
                    // 遍历二级菜单
                    list1.stream().forEach(o -> {
                        if (setList.contains(o.getId())){
                            list2.add(o); // 二级
                        }
                    });
                    l.setChildMenu(list2);
                });
                return RestResp.success("操作成功", list);
            }
        } catch (Exception e){
            LOG.error("query menu list failed:{}", e.getMessage(), e);
        }
        return RestResp.fail("操作失败");
    }

    /** 管理员查看菜单列表 */
    public RestResp adminMenuList(){
        try {
            List<Menu> menuList = menuRepo.findByPid(0L);
            for (Menu m: menuList) {
                m.setChildMenu(menuRepo.findByPid(m.getId()));
            }
            return RestResp.success("操作成功", menuList);
        } catch (Exception e){
            LOG.error("admin query all menu failed: {}", e.getMessage(), e);
        }
        return RestResp.fail("操作失败");

    }

    /** 添加菜单 */
    public RestResp addMenu(String name, Long pid){
        try {
            Menu menu = new Menu(name, null, pid);
            menuRepo.save(menu);
            return RestResp.success("操作成功", menu);
        } catch (Exception e){
            LOG.error("add menu failed:{}", e.getMessage(), e);
        }
        return RestResp.fail("操作失败");
    }

    /** 删除菜单 */
    public RestResp deleteMenu(Long menuId){
        try {
            // 如果是二级菜单直接删除，一级菜单删除所有的二级菜单
            Menu menu = menuRepo.findOne(menuId);
            if (menu != null){
                Long pid = menu.getPid();
                if (!pid.equals(0L)){
                    List<Menu> childMenuList = menuRepo.findByPid(pid);
                    for (Menu m : childMenuList) {
                        menuRepo.delete(m);
                    }
                }
                menuRepo.delete(menu);
                return RestResp.success("操作成功");
            }
        } catch (Exception e){
            LOG.error("delete menu failed: {}", e.getMessage(), e);
        }
        return RestResp.fail("操作失败");
    }

    /** 修改菜单 */
    public RestResp updateMenu(Long menuId, String name){
        try {
            Menu menu = menuRepo.findOne(menuId);
            menu.setName(name);
            menu.setDescription(null);
            Menu m = menuRepo.save(menu);
            return RestResp.success("操作成功", m);
        } catch (Exception e){
            LOG.error("update menu failed: {}", e.getMessage(), e);
        }
        return RestResp.fail("操作失败");
    }

    public RestResp authMenu(Long menuId, Long toId){
        try {
            List<UserRole> userRoleList = userRoleRepo.findByUserId(toId);
            Set<Long> roleSet = new HashSet<>();
            userRoleList.stream().forEach(ur -> {
                // 选中user所属角色roleId
                Long roleId = ur.getRoleId();
                roleSet.add(roleId);
            });

            // 按照menuId加权限
            Menu menu = menuRepo.findOne(menuId);
            MenuRole menuRole = new MenuRole();
            MenuRole menuRole2 = new MenuRole();
            Iterator<Long> it = roleSet.iterator();

            while (it.hasNext()){
                Long roleId = it.next();
                MenuRole mr = menuRoleRepo.findByMenuIdAndRoleId(menuId, roleId);
                if (mr == null){
                    menuRole.setMenuId(menuId);
                    menuRole.setRoleId(roleId);
                    menuRoleRepo.save(menuRole);

                    // 是否有pid,有pid将pid=id菜单加上
                    Long pid = menu.getPid();
                    if (!pid.equals(0L)) {
                        Menu menu1 = menuRepo.findOne(pid);
                        Long menuId1 = menu1.getId();// fuId
                        menuRole2.setMenuId(menuId1);
                        menuRole2.setRoleId(roleId);
                        menuRoleRepo.save(menuRole2);
                    }
                }
            }
            return RestResp.success("操作成功", toId);
        } catch (Exception e){
            LOG.error("auth menu failed: {}", e.getMessage(), e);
        }
        return RestResp.fail("操作失败");
    }

}
