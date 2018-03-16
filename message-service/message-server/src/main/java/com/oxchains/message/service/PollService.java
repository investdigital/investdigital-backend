package com.oxchains.message.service;

import com.oxchains.message.common.MapCacheManager;
import com.oxchains.message.common.RestResp;
import com.oxchains.message.dao.MessageDao;
import com.oxchains.message.dao.MessageTextDao;
import com.oxchains.message.domain.*;
import com.oxchains.message.rest.dto.MessageDTO;
import com.oxchains.message.rest.dto.PageDTO;
import com.oxchains.message.rest.dto.UnReadSizeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.oxchains.message.common.MessageReadStatus.UN_READ;
import static com.oxchains.message.common.MessageType.*;

/**
 * @author luoxuri
 * @create 2018-02-05 14:41
 **/
@Service
public class PollService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    /**
     * 所有公告信息ID
     */
    private final Set<Long> set = new HashSet<>();

    @Resource
    private MessageDao messageDao;
    @Resource
    private MessageTextDao messageTextDao;
    @Resource
    private MapCacheManager mapCacheManager;

    public RestResp sendNoticeMsg(MessageText messageText) {
        try {
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            messageText.setPostDate(currentTime);
            messageText.setSenderId(0L);
            messageText.setMessageType(PUBLIC_LETTET);
            MessageText save = messageTextDao.save(messageText);
            return RestResp.success("操作成功", save);
        } catch (Exception e) {
            LOG.error("send notice message error", e);
        }
        return RestResp.fail("操作失败");
    }

    /**
     * 查询公告信息
     * @param pollData
     * @return
     */
    public RestResp queryNoticeMsg(PollData pollData) {
        return queryMessage(pollData.getLogo(), pollData.getUserId(), pollData.getPageNum(), pollData.getPageSize(), PUBLIC_LETTET);
    }

    /**
     * 查询系统信息
     * @param pollData
     * @return
     */
    public RestResp queryGlobalMsg(PollData pollData) {
        return queryMessage(pollData.getLogo(), pollData.getUserId(), pollData.getPageNum(), pollData.getPageSize(), GLOBAL_LETTET);
    }

    /**
     * 查询私信
     * @param pollData
     * @return
     */
    public RestResp queryPrivateMsg(PollData pollData) {
        return queryMessage(pollData.getLogo(), pollData.getUserId(), pollData.getPageNum(), pollData.getPageSize(), PRIVATE_LETTET);
    }

    /**
     * 逻辑删除公告信息
     * @param messageId
     * @return
     */
    public RestResp deleteNoticeMsg(Long messageId) {
        try {
            Message message = messageDao.findOne(messageId);
            if (message.getDel() == 1){
                return RestResp.fail("公告信息异常，请联系管理员");
            }
            message.setDel(1);
            Message save = messageDao.save(message);
            return RestResp.success("操作成功", save);
        } catch (Exception e) {
            LOG.error("delete notice message error", e);
        }
        return RestResp.fail("操作失败");
    }

    /**
     * 未读消息
     * @param
     * @return
     */
    public RestResp queryUnReadCount(PollData pollData) {
        int count = 1;
        try {
            int[] unReadSizeArr = invokeDb(pollData, count);
            return RestResp.success("操作成功", unReadSizeArr);
        } catch (Exception e) {
            LOG.error("query unRead count error", e);
        }
        return RestResp.fail("操作失败");
    }

    private int[] invokeDb(PollData pollData, int count) throws Exception {
        // 第一次请求的时候，说明用户登录了，将未读公告信息添加到message表中
        addUnReadMsg(pollData);

        int[] counts = mapCacheManager.getCountMap().get(pollData.getUserId());
        if (counts == null){
            int[] allUnReadArr = getAllUnReadArr(pollData);
            counts = allUnReadArr;
            mapCacheManager.getCountMap().put(pollData.getUserId(), allUnReadArr);
        }

        // 未读条数数组中的第五个元素是  当有新的消息添加时为1，没有为0
        if (counts[4] == 1){
            return getAllUnReadArr(pollData);
        }

        // 第一次请求
        if (pollData.getTip() == 1) {
            int[] allUnReadArr = getAllUnReadArr(pollData);
            mapCacheManager.getCountMap().put(pollData.getUserId(), allUnReadArr);
            return allUnReadArr;
        }

        Integer allUnRead = messageDao.countByLogoAndReceiverIdAndReadStatusAndDel(pollData.getLogo(), pollData.getUserId(), UN_READ, 0);
        Integer cacheAllUnRead = mapCacheManager.getCountMap().get(pollData.getUserId())[0];

        // 第二次以及之后，缓存值和数据库查询值对比，一样，继续轮询查
        while (allUnRead.equals(cacheAllUnRead)) {
            counts = mapCacheManager.getCountMap().get(pollData.getUserId());
            if (counts[4] == 1){
                return getAllUnReadArr(pollData);
            }

            // 未读消息能够实时查询，每次都要查
            addUnReadMsg(pollData);
            if (count <= 10) {
                Thread.sleep(2000);
            } else if (count > 10 && count <= 15) {
                Thread.sleep(3000);
            } else {
                int[] allUnReadArr = {allUnRead, mapCacheManager.getCountMap().get(pollData.getUserId())[1], mapCacheManager.getCountMap().get(pollData.getUserId())[2], mapCacheManager.getCountMap().get(pollData.getUserId())[3], 0};
                mapCacheManager.getCountMap().put(pollData.getUserId(), allUnReadArr);
                return allUnReadArr;
            }
            allUnRead = messageDao.countByLogoAndReceiverIdAndReadStatusAndDel(pollData.getLogo(), pollData.getUserId(), UN_READ, 0);
            count++;
        }
        int[] allUnReadArr = getAllUnReadArr(pollData);
        mapCacheManager.getCountMap().put(pollData.getUserId(), allUnReadArr);
        return allUnReadArr;
    }

    private int[] getAllUnReadArr(PollData pollData) {
        Integer unReadNoticeSize = messageDao.countByLogoAndReceiverIdAndReadStatusAndMessageTypeAndDel(pollData.getLogo(), pollData.getUserId(), UN_READ, PUBLIC_LETTET, 0);
        Integer unReadGolbalSize = messageDao.countByLogoAndReceiverIdAndReadStatusAndMessageTypeAndDel(pollData.getLogo(), pollData.getUserId(), UN_READ, GLOBAL_LETTET, 0);
        Integer unReadPrivateSize = messageDao.countByLogoAndReceiverIdAndReadStatusAndMessageTypeAndDel(pollData.getLogo(), pollData.getUserId(), UN_READ, PRIVATE_LETTET, 0);
        Integer newAllUnRead = unReadNoticeSize + unReadGolbalSize + unReadPrivateSize;
        int[] newAllUnReadArr = {newAllUnRead, unReadNoticeSize, unReadGolbalSize, unReadPrivateSize, 0};
        mapCacheManager.getCountMap().put(pollData.getUserId(), newAllUnReadArr);
        return newAllUnReadArr;
    }

    @javax.transaction.Transactional(javax.transaction.Transactional.TxType.NEVER)
    private void addUnReadMsg(PollData pollData) throws Exception {
        /*
        themis的角色和id的角色分组不一样
        themis的普通用户是4
        id的普通用户是2
         */
        Long defaultGroup = null;
        if (pollData.getLogo().equals("id")){
            defaultGroup = 2L; // id普通用戶的角色
        }
        if (pollData.getLogo().equals("themis")){
            defaultGroup = 4L;
        }
        Long userGroup = pollData.getUserGroup();
        List<MessageText> messageTextList = messageTextDao.findByLogoAndMessageTypeAndUserGroup(pollData.getLogo(), PUBLIC_LETTET, userGroup, defaultGroup);
        if (messageTextList.size() != 0) {
            for (MessageText mt : messageTextList) {
                set.add(mt.getId());
            }

            // 移除已读公告的mtId
            List<Message> allPublic = messageDao.findByLogoAndReceiverIdAndMessageTypeAndDel(pollData.getLogo(), pollData.getUserId(), PUBLIC_LETTET, 0);
            for (Message m : allPublic) {
                set.remove(m.getMessageTextId());
            }

            // 添加剩余没有的公告
            Iterator<Long> it = set.iterator();
            Message message = new Message();
            while (it.hasNext()) {
                message.setMessageTextId(it.next());
                message.setReadStatus(UN_READ);
                message.setReceiverId(pollData.getUserId());
                message.setMessageType(PUBLIC_LETTET);
                message.setLogo(pollData.getLogo());
                messageDao.save(message);
            }
        }

    }


    private RestResp queryMessage(String logo, Long userId, Integer pageNum, Integer pageSize, int messageType) {
        try {
            Pageable pageable = new PageRequest(pageNum - 1, pageSize, new Sort(Sort.Direction.DESC, "id"));
            Page<Message> page = messageDao.findByLogoAndReceiverIdAndAndMessageTypeAndDel(logo, userId, messageType, 0, pageable);
            Iterator<Message> it = page.iterator();
            List<MessageDTO> mList = new ArrayList<>();
            while (it.hasNext()) {
                Message message = it.next();
                MessageText messageText = messageTextDao.findByIdAndMessageType(message.getMessageTextId(), messageType);
                message.setMessageText(messageText);
                mList.add(new MessageDTO(message));

                message.setReadStatus(2);
                message.setReceiverId(userId);
                messageDao.save(message);
            }

            PageDTO<MessageDTO> pageDTO = new PageDTO<>();
            pageDTO.setPageList(mList);
            pageDTO.setRowCount(page.getTotalElements());
            pageDTO.setTotalPage(page.getTotalPages());
            pageDTO.setPageNum(pageNum);
            pageDTO.setPageSize(pageSize);
            return RestResp.success("操作成功", pageDTO);
        } catch (Exception e) {
            LOG.error("query message error", e);
        }
        return RestResp.fail("操作失败");
    }

}
