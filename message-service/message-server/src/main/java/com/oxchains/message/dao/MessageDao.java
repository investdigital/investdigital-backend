package com.oxchains.message.dao;

import com.oxchains.message.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author luoxuri
 * @create 2018-02-05 15:06
 **/
@Repository
public interface MessageDao extends CrudRepository<Message, Long> {

    /**
     * 根据logo，receiverId,messageType获取message的page
     * @param logo          项目标识
     * @param recId         接受者ID
     * @param msgType       消息类型
     * @param del           逻辑删除标识
     * @param pageable      pageable
     * @return              page对象
     */
    Page<Message> findByLogoAndReceiverIdAndAndMessageTypeAndDel(String logo, Long recId, Integer msgType, Integer del, Pageable pageable);

    /**
     * 根据logo，receiverId，messageType获取message集合
     * @param logo          项目标识
     * @param receiverId    接受者ID
     * @param messageType   消息类型
     * @param del           逻辑删除标识
     * @return              集合
     */
    List<Message> findByLogoAndReceiverIdAndMessageTypeAndDel(String logo, Long receiverId, Integer messageType, Integer del);

    /**
     * 根据logo，receiverId，readStatus查询数量
     * @param logo          项目标识，如themis
     * @param receiverId    接受者ID
     * @param readStatus    阅读状态
     * @param del           逻辑删除标识
     * @return              数量
     */
    Integer countByLogoAndReceiverIdAndReadStatusAndDel(String logo, Long receiverId, Integer readStatus, Integer del);

    /**
     * 根据logo，receiverId，readStatus,messageType查询数量
     * @param logo          项目标识
     * @param receiverId    接受者ID
     * @param readStatus    阅读状态
     * @param messageType   消息类型
     * @param del           逻辑删除标识
     * @return              数量
     */
    Integer countByLogoAndReceiverIdAndReadStatusAndMessageTypeAndDel(String logo, Long receiverId, Integer readStatus, Integer messageType, Integer del);
}
