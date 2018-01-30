package com.oxchains.rmsuser.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author luoxuri
 * @create 2018-01-09 13:41
 **/
@Entity
@Data
@Table(name = "sys_role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32)
    private String roleName;
    @Column(length = 32)
    private String roleSign;
    @Column(length = 128)
    private String description;

    public Role(String roleName, String roleSign) {
        this.roleName = roleName;
        this.roleSign = roleSign;
    }

    public Role() {
    }
}
