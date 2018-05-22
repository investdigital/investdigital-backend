package com.oxchains.message.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 1:超级管理员
 * 2:仲裁
 * 3:客服
 * 4:所有用户（普通用户）
 *
 * @author luoxuri
 * @create 2018-03-08 11:06
 **/
@Data
@Entity
@Table(name = "user_group")
public class UserGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
}
