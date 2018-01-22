package com.oxchains.rmsuser.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author luoxuri
 * @create 2018-01-09 13:42
 **/
@Entity
@Data
@Table(name = "sys_permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String url;

    public Permission(String name, String description, String url) {
        this.name = name;
        this.description = description;
        this.url = url;
    }

    public Permission() {
    }
}
