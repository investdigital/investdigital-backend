package com.oxchains.message.service;

import com.oxchains.message.domain.MessageVO;
import com.oxchains.message.grpc.lib.Request;
import com.oxchains.message.grpc.lib.Response;
import com.oxchains.message.grpc.lib.SimpleGrpc;
import io.grpc.Channel;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;
import org.springframework.stereotype.Service;

/**
 * User: Michael
 * Email: yidongnan@gmail.com
 * Date: 2016/11/8
 */
@Service
public class GrpcClientService {

    @GrpcClient("grpc-server")
    private Channel serverChannel;

    /**
     * 保存消息
     *
     * 对应通知类型的参数不能为null
     * @param messageVO 实体
     * @return boolean
     */
    public boolean saveMessage(MessageVO messageVO) {
        if (messageVO.getInformType().equals(1L)) {
            return saveMessageToDbOnly(messageVO);
        }
        if (messageVO.getInformType().equals(2L)) {
            return saveMessageAndPushToRegId(messageVO);
        }
        return saveMessageAndPushToAndroidOrIos(messageVO);

    }

    // 保存消息，不推送
    public boolean saveMessageToDbOnly(MessageVO messageVO) {
        SimpleGrpc.SimpleBlockingStub stub = SimpleGrpc.newBlockingStub(serverChannel);
        Response response = stub.save(Request.newBuilder()
                .setInformType(messageVO.getInformType())
                .setReceiverId(messageVO.getReceiverId())
                .setMessageType(messageVO.getMessageType())
                .setLogo(messageVO.getLogo())
                .setMessage(messageVO.getMessage())
                .setOrderId(messageVO.getOrderId())
                .setSendId(messageVO.getSendId())
                .build());
        return response.getMessage();
    }

    // 保存信息，推送给指定设备
    public boolean saveMessageAndPushToRegId(MessageVO messageVO) {
        SimpleGrpc.SimpleBlockingStub stub = SimpleGrpc.newBlockingStub(serverChannel);
        Response response = stub.save(Request.newBuilder()
                .setInformType(messageVO.getInformType())
                .setReceiverId(messageVO.getReceiverId())
                .setMessageType(messageVO.getMessageType())
                .setLogo(messageVO.getLogo())
                .setMessage(messageVO.getMessage())
                .setOrderId(messageVO.getOrderId())
                .setSendId(messageVO.getSendId())
                .setRegistrationId(messageVO.getRegistrationId())
                .setNotificationTitle(messageVO.getNotificationTitle())
                .setNotificationSummary(messageVO.getNotificationSummary())
                .setMsgTitle(messageVO.getMsgTitle())
                .setMsgContent(messageVO.getMsgContent())
                .setExtrasParam(messageVO.getExtrasParam())
                .build());
        return response.getMessage();
    }

    // 保存信息，推送给android或者ios手机
    public boolean saveMessageAndPushToAndroidOrIos(MessageVO messageVO) {
        SimpleGrpc.SimpleBlockingStub stub = SimpleGrpc.newBlockingStub(serverChannel);
        Response response = stub.save(Request.newBuilder()
                .setInformType(messageVO.getInformType())
                .setReceiverId(messageVO.getReceiverId())
                .setMessageType(messageVO.getMessageType())
                .setLogo(messageVO.getLogo())
                .setMessage(messageVO.getMessage())
                .setOrderId(messageVO.getOrderId())
                .setSendId(messageVO.getSendId())
                .setNotificationTitle(messageVO.getNotificationTitle())
                .setNotificationSummary(messageVO.getNotificationSummary())
                .setMsgTitle(messageVO.getMsgTitle())
                .setMsgContent(messageVO.getMsgContent())
                .setExtrasParam(messageVO.getExtrasParam())
                .build());
        return response.getMessage();
    }
}
