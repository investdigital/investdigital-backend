package com.oxchains.rmsuser.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ccl
 * @time 2018-02-02 18:08
 * @name RoleResource
 * @desc:
 */
@Entity
@Data
@Table(name = "sys_role_resource")
public class RoleResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roleId;
    private Long resourceId;

    public RoleResource(Long roleId, Long resourceId) {
        this.roleId = roleId;
        this.resourceId = resourceId;
    }

    public RoleResource() {
    }
}
