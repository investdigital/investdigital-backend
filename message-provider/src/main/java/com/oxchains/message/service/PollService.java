package com.oxchains.message.service;

import com.oxchains.message.common.RestResp;
import com.oxchains.message.dao.MessageDao;
import com.oxchains.message.dao.MessageTextDao;
import com.oxchains.message.dao.OrderRepo;
import com.oxchains.message.dao.UserDao;
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
import static com.oxchains.message.common.UserGroup.DEFAULT_GROUP;


/**
 * @author luoxuri
 * @create 2018-02-05 14:41
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class PollService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    /**
     * 所有公告信息ID
     */
    private final Set<Long> set = new HashSet<>();
    /**
     * 所有未读消息map
     */
    private final Map<Long, Integer> countMap = new HashMap<>();
    /**
     * 存放userId所属用户组map
     */
    private final Map<Long, Long> upMap = new HashMap<>();
    private final UnReadSizeDTO unReadSizeDTO = new UnReadSizeDTO();

    @Resource
    private MessageDao messageDao;
    @Resource
    private MessageTextDao messageTextDao;
    @Resource
    private UserDao userDao;
    @Resource
    private OrderRepo orderDao;

    @Value("${user.default.image}")
    private String userDefaultImage;
    @Value("${system.default.image}")
    private String systemDefaultImage;

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
        return queryMessage(pollData.getLogo(), pollData.getUserId(), pollData.getPageNum(), pollData.getPageSize(), PUBLIC_LETTET, false);
    }

    /**
     * 查询系统信息
     * @param pollData
     * @return
     */
    public RestResp queryGlobalMsg(PollData pollData) {
        return queryMessage(pollData.getLogo(), pollData.getUserId(), pollData.getPageNum(), pollData.getPageSize(), GLOBAL_LETTET, true);
    }

    /**
     * 查询私信
     * @param pollData
     * @return
     */
    public RestResp queryPrivateMsg(PollData pollData) {
        return queryMessage(pollData.getLogo(), pollData.getUserId(), pollData.getPageNum(), pollData.getPageSize(), PRIVATE_LETTET, true);
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
     * @param pollData
     * @return
     */
    public RestResp queryUnReadCount(PollData pollData) {
        int count = 1;
        try {
            UnReadSizeDTO unReadSizeDTO = invokeDb(pollData, count);
            return RestResp.success("操作成功", unReadSizeDTO);
        } catch (Exception e) {
            LOG.error("query unRead count error", e);
        }
        return RestResp.fail("操作失败");
    }

    private UnReadSizeDTO invokeDb(PollData pollData, int count) throws Exception {
        // 第一次请求的时候，说明用户登录了，将未读公告信息添加到message表中
        addUnReadMsg(pollData);

        // 获取各种未读消息数量
        Integer unReadNoticeSize = messageDao.countByLogoAndReceiverIdAndReadStatusAndMessageTypeAndDel(pollData.getLogo(), pollData.getUserId(), UN_READ, PUBLIC_LETTET, 0);
        Integer unReadGolbalSize = messageDao.countByLogoAndReceiverIdAndReadStatusAndMessageTypeAndDel(pollData.getLogo(), pollData.getUserId(), UN_READ, GLOBAL_LETTET, 0);
        Integer unReadPrivateSize = messageDao.countByLogoAndReceiverIdAndReadStatusAndMessageTypeAndDel(pollData.getLogo(), pollData.getUserId(), UN_READ, PRIVATE_LETTET, 0);

        Integer allUnRead = messageDao.countByLogoAndReceiverIdAndReadStatusAndDel(pollData.getLogo(), pollData.getUserId(), UN_READ, 0);
        Integer cacheUnRead = countMap.getOrDefault(pollData.getUserId(), 0);

        // 第一次请求
        if (pollData.getTip() == 1) {
            putMapAndSetValues(pollData.getUserId(), unReadNoticeSize, unReadGolbalSize, unReadPrivateSize);
            return unReadSizeDTO;
        }

        // 第二次以及之后，缓存值和数据库查询值对比，一样，继续轮询查
        while (allUnRead.equals(cacheUnRead)) {
            // 未读消息能够实时查询，每次都要查
            addUnReadMsg(pollData);
            if (count <= 10) {
                Thread.sleep(2000);
            } else if (count > 10 && count <= 15) {
                Thread.sleep(3000);
            } else {
                putMapAndSetValues(pollData.getUserId(), unReadNoticeSize, unReadGolbalSize, unReadPrivateSize);
                return unReadSizeDTO;
            }
            allUnRead = messageDao.countByLogoAndReceiverIdAndReadStatusAndDel(pollData.getLogo(), pollData.getUserId(), UN_READ, 0);
            count++;
        }

        unReadNoticeSize = messageDao.countByLogoAndReceiverIdAndReadStatusAndMessageTypeAndDel(pollData.getLogo(), pollData.getUserId(), UN_READ, PUBLIC_LETTET, 0);
        unReadGolbalSize = messageDao.countByLogoAndReceiverIdAndReadStatusAndMessageTypeAndDel(pollData.getLogo(), pollData.getUserId(), UN_READ, GLOBAL_LETTET, 0);
        unReadPrivateSize = messageDao.countByLogoAndReceiverIdAndReadStatusAndMessageTypeAndDel(pollData.getLogo(), pollData.getUserId(), UN_READ, PRIVATE_LETTET, 0);
        putMapAndSetValues(pollData.getUserId(), unReadNoticeSize, unReadGolbalSize, unReadPrivateSize);
        return unReadSizeDTO;
    }

    private void putMapAndSetValues(Long userId, Integer unReadNoticeSize, Integer unReadGolbalSize, Integer unReadPrivateSize) {
        Integer unReadTotal = unReadNoticeSize + unReadGolbalSize + unReadPrivateSize;
        initUnReadSizeDTO();
        putMap(userId, unReadTotal);
        setValues(unReadNoticeSize, unReadGolbalSize, unReadPrivateSize, unReadTotal);
    }

    private void initUnReadSizeDTO() {
        unReadSizeDTO.setAllUnRead(0);
        unReadSizeDTO.setNoticeUnRead(0);
        unReadSizeDTO.setGlobalUnRead(0);
        unReadSizeDTO.setPrivateUnRead(0);
    }

    private void putMap(Long userId, Integer unReadAll) {
        countMap.put(userId, unReadAll);
    }

    private void setValues(Integer unReadNoticeSize, Integer unReadGolbalSize, Integer unReadPrivateSize, Integer unReadAll) {
        unReadSizeDTO.setAllUnRead(unReadAll);
        unReadSizeDTO.setNoticeUnRead(unReadNoticeSize);
        unReadSizeDTO.setGlobalUnRead(unReadGolbalSize);
        unReadSizeDTO.setPrivateUnRead(unReadPrivateSize);
    }

    private void addUnReadMsg(PollData pollData) throws Exception {
        Long userGroup;
        if (pollData.getTip() == 1) {
            // 先找到roleId，然后得到角色userGroup，然后根据msgType和userGroup得到id
            User user = userDao.findOne(pollData.getUserId());
            userGroup = user.getRoleId();
            upMap.put(pollData.getUserId(), userGroup);
        } else {
            userGroup = upMap.getOrDefault(pollData.getUserId(), 0L);
        }
        if (userGroup == 0L) {
            throw new Exception("用户没有设置用户组");
        }

        List<MessageText> messageTextList = messageTextDao.findByLogoAndMessageTypeAndUserGroup(pollData.getLogo(), PUBLIC_LETTET, userGroup, DEFAULT_GROUP);
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

    private RestResp queryMessage(String logo, Long userId, Integer pageNum, Integer pageSize, int messageType, boolean needOrder) {
        try {
            Pageable pageable = new PageRequest(pageNum - 1, pageSize, new Sort(Sort.Direction.DESC, "id"));
            Page<Message> page = messageDao.findByLogoAndReceiverIdAndAndMessageTypeAndDel(logo, userId, messageType, 0, pageable);
            Iterator<Message> it = page.iterator();
            List<MessageDTO> mList = new ArrayList<>();
            while (it.hasNext()) {
                Message message = it.next();
                MessageText messageText = messageTextDao.findByIdAndMessageType(message.getMessageTextId(), messageType);

                if (needOrder) {
                    boolean isSuccess = getOrderInfoAndImage(userId, messageText);
                    if (!isSuccess) {
                        return RestResp.fail("获取订单信息失败");
                    }
                }
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

    /**
     * 获取订单相关信息和头像
     */
    private boolean getOrderInfoAndImage(Long userId, MessageText messageText) {
        // 获取头像
        Long sendId = messageText.getSenderId();
        if (sendId == null) {
            sendId = 0L;
        }
        if (sendId == 0) {
            // 设置系统头像
            messageText.setImageName(systemDefaultImage);
        } else {
            User user = userDao.findOne(sendId);
            String imageName = user.getImage();
            if (imageName == null) {
                // 设置默认用户头像
                messageText.setImageName(userDefaultImage);
            } else {
                messageText.setImageName(imageName);
            }
        }

        // 获取order其余信息
        String orderId = messageText.getOrderId();
        Orders orders = orderDao.findOne(orderId);
        Long buyerId = orders.getBuyerId();
        Long sellerId = orders.getSellerId();
        if (userId.equals(buyerId)) {
            messageText.setPartnerId(sellerId);
            User user = userDao.findOne(sellerId);
            messageText.setFriendUsername(user.getLoginname());
            return true;
        } else if (userId.equals(sellerId)) {
            messageText.setPartnerId(buyerId);
            User user = userDao.findOne(buyerId);
            messageText.setFriendUsername(user.getLoginname());
            return true;
        } else {
            return false;
        }
    }


}
