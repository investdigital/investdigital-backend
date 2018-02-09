package com.oxchains.message.utils;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.oxchains.message.common.Constants.*;

/**
 * @author luoxuri
 * @create 2018-02-01 15:31
 **/
public class JPushUtils {

    private static Logger LOG = LoggerFactory.getLogger(JPushUtils.class);
    private static JPushClient jPushClient = new JPushClient(MASTER_SECRET, APP_KEY);

    /**
     * 推送给设备标识参数的用户
     * @param registrationId 设备标识
     * @param notificationTitle 通知内容标题
     * @param notificationSummary 通知内容下面的小标题
     * @param msgTitle 消息内容标题
     * @param msgContent 消息内容
     * @param extrasParam 扩展字段
     * @return 0推送失败，1推送成功
     */
    public static int sendToRegistrationId(String registrationId, String notificationTitle, String notificationSummary, String msgTitle, String msgContent, String extrasParam){
        int result = 0;
        try {
            PushPayload pushPayload = buildPushObjectAllRegistrationIdAlertWithTitle(registrationId, notificationTitle, notificationSummary, msgTitle, msgContent, extrasParam);
            PushResult pushResult = jPushClient.sendPush(pushPayload);
            if (pushResult.getResponseCode() == SUCCESS_CODE){
                result = 1;
            }
        } catch (Exception e){
            LOG.error("send to registration error");
        }
        return result;
    }

    /**
     * 发送给所有安卓用户
     * @param notificationTitle 通知内容标题
     * @param notificationSummary 通知内容下面的小标题
     * @param msgTitle 消息内容标题
     * @param msgContent 消息内容
     * @param extrasParam 扩展字段
     * @return 0推送失败， 1推送成功
     */
    public static int sendToAllAndroid(String notificationTitle, String notificationSummary, String msgTitle, String msgContent, String extrasParam){
        int result = 0;
        try {
            PushPayload pushPayload = buildPushObjectAndroidAllAlertWithTitle(notificationTitle, notificationSummary, msgTitle, msgContent, extrasParam);
            PushResult pushResult = jPushClient.sendPush(pushPayload);
            if (pushResult.getResponseCode() == SUCCESS_CODE){
                result = 1;
            }
        } catch (Exception e){
            LOG.error("send to all Android error", e);
        }
        return result;
    }

    /**
     * 发送给所有IOS用户
     * @param notificationTitle 通知内容标题
     * @param msgTitle 消息内容标题
     * @param msgContent 消息内容
     * @param extrasParam 扩展内容
     * @return 0推送失败， 1推送成功
     */
    public static int sendToAllIos(String notificationTitle, String msgTitle, String msgContent, String extrasParam) {
        int result = 0;
        try {
            PushPayload pushPayload = buildPushObjectIosAllAlertWithTitle(notificationTitle, msgTitle, msgContent, extrasParam);
            PushResult pushResult = jPushClient.sendPush(pushPayload);
            if (pushResult.getResponseCode() == SUCCESS_CODE){
                result = 1;
            }
        } catch (Exception e){
            LOG.error("send to all Ios error", e);
        }
        return result;
    }


    /**
     * 发送给所有用户(Android Ios)
     * @param notificationTitle 通知内容标题
     * @param msgTitle 消息内容标题
     * @param msgContent 消息内容
     * @param extrasParam 扩展字段
     * @return 0推送失败， 1推送成功
     */
    public static int sendToAndroidAndIos(String notificationTitle, String msgTitle, String msgContent, String extrasParam){
        int result = 0;
        try {
            PushPayload pushPayload = buildPushObjectAndroidAndIos(notificationTitle, msgTitle, msgContent, extrasParam);
            PushResult pushResult = jPushClient.sendPush(pushPayload);
            if (pushResult.getResponseCode() == SUCCESS_CODE){
                result = 1;
            }
        } catch (Exception e){
            LOG.error("send to all error", e);
        }
        return result;
    }

    private static PushPayload buildPushObjectAllRegistrationIdAlertWithTitle(String registrationId, String notificationTitle, String notificationSummary, String msgTitle, String msgContent, String extrasParam){
        LOG.info("----------buildPushObjectAllAllAlert----------");
        // 创建一个IosAlert对象，可指定APNs的alert、title等字段
//        IosAlert iosAlert =  IosAlert.newBuilder().setTitleAndBody("title", "alert body").build();
        return PushPayload.newBuilder()
        // 指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
        .setPlatform(Platform.all())
        // 指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
        .setAudience(Audience.registrationId(registrationId))
        // jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
        .setNotification(Notification.newBuilder()
                //指定当前推送的android通知
                .addPlatformNotification(AndroidNotification.newBuilder()
                .setAlert(notificationTitle)
                        .setTitle(notificationSummary)
                        // TODO 此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                        .addExtra("androidNotification extras key",extrasParam)
                        .build())
                // 指定当前推送的iOS通知
                .addPlatformNotification(IosNotification.newBuilder()
                        // 传一个IosAlert对象，指定apns title、title、subtitle等
                        .setAlert(notificationTitle)
                        // 直接传alert
                        // 此项是指定此推送的badge自动加1
                        .incrBadge(1)
                        // 此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                        // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                        .setSound("sound.caf")
                        // TODO 此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                        .addExtra("iosNotification extras key",extrasParam)
                        // 此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
                        // 取消此注释，消息推送时ios将无法在锁屏情况接收
                        .setContentAvailable(true)
                        .build())
                .build())
        // Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
        // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
        // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
        .setMessage(Message.newBuilder()
        .setMsgContent(msgContent)
                .setTitle(msgTitle)
                .addExtra("message extras key",extrasParam)
                .build())
                .setOptions(Options.newBuilder()
                        // TODO 此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(false)
                        // 此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        .setSendno(1)
                        // 此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天；
                        .setTimeToLive(86400)
                        .build())
                .build();
    }

    private static PushPayload buildPushObjectAndroidAllAlertWithTitle(String notificationTitle, String notificationSummary, String msgTitle, String msgContent, String extrasParam){
        LOG.info("----------buildPushObjectAndroidAllAlertWithTitle----------");
        return PushPayload.newBuilder()
        // 指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
                .setPlatform(Platform.android())
        // 指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
                .setAudience(Audience.all())
                // jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
                .setNotification(Notification.newBuilder()
                        // 指定当前推送的android通知
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(notificationTitle)
                                .setTitle(notificationSummary)
                                // 此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("androidNotification extras key",extrasParam)
                                .build())
                        .build()
                )
        // Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
        // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
        // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
        .setMessage(Message.newBuilder()
                .setMsgContent(msgContent)
                .setTitle(msgTitle)
                .addExtra("message extras key",extrasParam)
                .build())
                .setOptions(Options.newBuilder()
                        // 此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(false)
                        // 此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        .setSendno(1)
                        // 此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .setTimeToLive(86400)
                        .build())
                .build();
    }

    private static PushPayload buildPushObjectIosAllAlertWithTitle( String notificationTitle, String msgTitle, String msgContent, String extrasParam) {
        System.out.println("----------buildPushObjectIosAllAlertWithTitle----------");
        return PushPayload.newBuilder()
                // 指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
                .setPlatform(Platform.ios())
                // 指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
                .setAudience(Audience.all())
                // jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
                .setNotification(Notification.newBuilder()
                        //指定当前推送的android通知
                        .addPlatformNotification(IosNotification.newBuilder()
                                // 传一个IosAlert对象，指定apns title、title、subtitle等
                                .setAlert(notificationTitle)
                                // 直接传alert
                                // 此项是指定此推送的badge自动加1
                                .incrBadge(1)
                                // 此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                                // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                                .setSound("sound.caf")
                                // TODO 此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("iosNotification extras key",extrasParam)
                                // 此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
//                                .setContentAvailable(true)
                                .build())
                        .build()
                )
        // Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
        // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
        // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
                .setMessage(Message.newBuilder()
                        .setMsgContent(msgContent)
                        .setTitle(msgTitle)
                        // TODO
                        .addExtra("message extras key",extrasParam)
                        .build())
                .setOptions(Options.newBuilder()
                        // 此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(false)
                        // 此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        .setSendno(1)
                        // 此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .setTimeToLive(86400)
                        .build())
                .build();
    }

    public static PushPayload buildPushObjectAndroidAndIos(String notificationTitle, String msgTitle, String msgContent, String extrasParam) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
                        .setAlert(notificationTitle)
                        .addPlatformNotification(AndroidNotification.newBuilder()
                        .setAlert(notificationTitle)
                        .setTitle(notificationTitle)
                                // TODO
                        .addExtra("androidNotification extras key",extrasParam)
                        .build())
                        .addPlatformNotification(IosNotification.newBuilder()
                        .setAlert(notificationTitle)
                        .incrBadge(1)
                        .setSound("sound.caf")
                                // TODO
                        .addExtra("iosNotification extras key",extrasParam)
                        .build()).build()
                )
                .setMessage(Message.newBuilder()
                .setMsgContent(msgContent)
                .setTitle(msgTitle)
                        // TODO
                .addExtra("message extras key",extrasParam)
                .build())
                .setOptions(Options.newBuilder()
                .setApnsProduction(false)
                .setSendno(1)
                .setTimeToLive(86400)
                .build()).build();
    }

}
