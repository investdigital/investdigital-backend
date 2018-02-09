package com.oxchains.message.domain;

import javax.persistence.*;

/**
 * @author ccl
 * @time 2017-10-26 10:00
 * @name Role
 * @desc:
 */

@Entity
@Table(name = "tbl_sys_role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 32)
    private String roleName;

    @Column(length = 32)
    private String roleSign;

    @Column(length = 32)
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleSign() {
        return roleSign;
    }

    public void setRoleSign(String roleSign) {
        this.roleSign = roleSign;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
