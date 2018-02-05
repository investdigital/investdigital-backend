package com.oxchains.rmsuser.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * @author ccl
 * @time 2018-02-02 15:08
 * @name Resources
 * @desc:
 */
@Entity
@Data
@Table(name = "sys_resource")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pid;

    @Column(length = 32)
    private String resourceName;
    @Column(length = 32)
    private String resourceSign;
    @Column(length = 128)
    private String resourceUrl;
    @Column(length = 1)
    private Integer resourceType;
    @Column(length = 1)
    private Integer resourceLevel;
    @Column(length = 1)
    private Integer enabled;
    @Column(length = 255)
    private String description;

    public Resource() {
    }

    public Resource(String name, String description, Long pid) {
        this.resourceName = name;
        this.description = description;
        this.pid = pid;
    }
}
