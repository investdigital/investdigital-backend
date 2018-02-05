package com.oxchains.rmsuser.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ccl
 * @time 2018-02-02 18:08
 * @name UserResource
 * @desc:
 */
@Entity
@Data
@Table(name = "sys_user_resource")
public class UserResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long resourceId;

    public UserResource(Long userId, Long resourceId) {
        this.userId = userId;
        this.resourceId = resourceId;
    }

    public UserResource() {
    }
}
