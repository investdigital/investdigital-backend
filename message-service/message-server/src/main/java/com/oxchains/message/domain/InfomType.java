package com.oxchains.message.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * 推送类型
 *
 * 1：保存数据，不给移动端推送消息
 * 2：保存数据，推送给指定设备
 * 3：保存数据，推送给所有移动端消息
 * 4：保存数据，推送给android端消息
 * 5：保存数据，推送给ios端消息
 *
 * @author luoxuri
 * @create 2018-02-07 11:32
 **/
@Entity
@Data
@Table(name = "infom_type")
public class InfomType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String description;
}
