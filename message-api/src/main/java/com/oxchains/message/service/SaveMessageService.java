package com.oxchains.message.service;

import com.oxchains.message.domain.MessageVO;
import com.oxchains.message.exception.SaveMessageException;

/**
 * @author luoxuri
 * @create 2018-02-05 11:11
 **/
public interface SaveMessageService {
    /**
     * 保存站内信消息
     *
     * @param messageVO 消息内容
     * @return 返回值
     * @throws SaveMessageException 异常
     */
    public boolean saveMessage(MessageVO messageVO) throws SaveMessageException;
}
