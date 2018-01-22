package com.oxchains.rmsuser.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * @author luoxuri
 * @create 2018-01-09 13:39
 **/
@Entity
@Data
@Table(name = "sys_menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Long pid;

    @Transient
    private List<Menu> childMenu;

    public Menu() {
    }

    public Menu(String name, String description, Long pid) {
        this.name = name;
        this.description = description;
        this.pid = pid;
    }
}
