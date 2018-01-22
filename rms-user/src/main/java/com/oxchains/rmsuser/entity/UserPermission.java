package com.oxchains.rmsuser.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author luoxuri
 * @create 2018-01-09 13:43
 **/
@Entity
@Data
@Table(name = "sys_user_permission")
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long permissionId;

    public UserPermission(Long userId, Long permissionId) {
        this.userId = userId;
        this.permissionId = permissionId;
    }

    public UserPermission() {
    }
}
