package com.oxchains.rmsuser.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author luoxuri
 * @create 2018-01-09 13:43
 **/
@Entity
@Data
@Table(name = "sys_user_role")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long roleId;

    public UserRole(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public UserRole() {
    }
}
