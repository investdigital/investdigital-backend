package com.oxchains.rmsuser.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author luoxuri
 * @create 2018-01-09 13:43
 **/
@Entity
@Data
@Table(name = "sys_menu_role")
public class MenuRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long menuId;
    private Long roleId;

    public MenuRole(Long menuId, Long roleId) {
        this.menuId = menuId;
        this.roleId = roleId;
    }

    public MenuRole() {
    }
}
