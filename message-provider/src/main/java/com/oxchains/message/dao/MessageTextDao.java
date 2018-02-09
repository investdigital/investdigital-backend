package com.oxchains.message.dao;

import com.oxchains.message.domain.MessageText;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author luoxuri
 * @create 2018-02-05 15:11
 **/
@Repository
public interface MessageTextDao extends CrudRepository<MessageText, Long> {
    /**
     * 根据id和消息类型获取
     * @param id
     * @param messageType
     * @return
     */
    MessageText findByIdAndMessageType(Long id, Integer messageType);

/*
    @Query(value = " select mt from MessageText as mt where mt.messageType = ?1 and (mt.userGroup in (?2,?3)) ")
    List<MessageText> findByMessageTypeAndUserGroup(@Param("messageType") Integer messageType, @Param("userGroup") Long userGroup, @Param("userGroupAll") Long userGroupAll);
*/

    /**
     * 根据logo,messageType,userGroup获取
     * userGroup：4是所有用户
     * 调用者会传入一个4和一个userId所在用户组
     * 返回这个userId所在用户组消息和userGroup=4的所有消息
     *
     * @param logo
     * @param messageType
     * @param userGroup
     * @param userGroupAll
     * @return
     */
    @Query(value = " select mt from MessageText as mt where mt.logo = ?1 and mt.messageType = ?2 and (mt.userGroup in (?3,?4)) ")
    List<MessageText> findByLogoAndMessageTypeAndUserGroup(@Param("logo") String logo, @Param("messageType") Integer messageType, @Param("userGroup") Long userGroup, @Param("userGroupAll") Long userGroupAll);
}
