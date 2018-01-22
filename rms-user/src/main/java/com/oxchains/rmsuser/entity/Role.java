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

    private String name;
    private String description;

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Role() {
    }
}
