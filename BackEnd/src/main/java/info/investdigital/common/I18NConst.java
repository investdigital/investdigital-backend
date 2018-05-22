package info.investdigital.common;

/**
 * @Author: Gaoyp
 * @Description: 国际化数据键
 * @Date: Create in 下午4:30 2018/3/22
 * @Modified By:
 */
public interface I18NConst {

    String INFO_WELCOME_MESSAGE = "info.welcome.message";

    String FOUNDSERVICE_ISSUEFUND = "fundservice.issuefund";
    String FOUNDSERVICE_REVIEWFUND_FIRST = "fundservice.reviewFund.first";
    String FOUNDSERVICE_REVIEWFUND_SECOND = "fundservice.reviewFund.second";


    /**
     * 基金取址失败
     **/
    String FUND_ADDRESS_FETCH_FAILED = "fund.address.fetch.failed";
    /**
     * 未找到该基金
     **/
    String FUND_NOT_FOUND = "fund.not.found";
    /**
     * 下标基金失败
     **/
    String SUBSCRIP_FUND_FAIL = "subscrip.fund.fail";
    /**
     * 该基金不存在
     **/
    String FUND_NOT_EXIST = "fund.not.exist";
    /**
     * 成功
     **/
    String SUCCESS = "success";
    /**
     * 失败
     **/
    String FAILURE = "failure";
    /**
     * 发送行事务失败
     **/
    String SEND_TRANSACTION_FAILED = "send.transaction.failed";
    /**
     * 基金异常，请与管理员联系
     **/
    String FUND_EXECPTION = "fund.execption";
    /**
     * 服务繁忙,请重新操作
     **/
    String SERVICE_BUSY = "service.busy";
    /**
     * 用户不存在
     **/
    String USER_NOT_EXIST = "user.not.exist";
    /**
     * 秘钥已存在
     **/
    String SECRET_KEY_EXISTED = "secret.key.existed";
    /**
     * 秘钥保存失败
     **/
    String SECRET_KEY_SAVE_FAILED = "secret.key.save.failed";
    /**
     * 秘钥保存成功
     **/
    String SECRET_KEY_SAVE_SUCCESS = "secret.key.save.success";
    /**
     * 秘钥用户关联保存失败
     **/
    String SECRET_KEY_ASSOCIATED_WITH_USER_FAILED = "secret.key.associated.with.user.failed";
    /**
     * 秘钥生成成功
     **/
    String SECRET_KEY_CREATE_SUCCESS = "secret.key.create.success";
    /**
     * 秘钥生成失败
     **/
    String SECRET_KEY_CREATE_FAILED = "secret.key.create.failed";
    /**
     * 密钥获取失败
     **/
    String SECRET_KEY_OBTAINING_FAILURE = "secret.key.Obtaining.failure";
    /**
     * 图片上传成功
     **/
    String IMAGE_UPLOAD_SUCCESS = "image.upload.success";
    /**
     * 图片上传失败
     **/
    String IMAGE_UPLOAD_FAILED = "image.upload.failed";
    /**
     * 请正确提交的注册信息
     **/
    String SUBMIT_REGISTRATION_INFORMATION_CORRECTLY = "submit.registration.information.correctly";
    /**
     * 请正确填写登录名，只能包含字母、数字、下划线，且只能以字母开头
     **/
    String LOGIN_NAME_FORMAT = "login.name.format";
    /**
     * 请正确填写手机号
     **/
    String FILL_PHONE_NUMBER_CORRECTLY = "fill.phone.number.correctly";
    /**
     * 请正确填写邮箱地址
     **/
    String FILL_MAIL_ADDRESS_CORRECTLY = "fill.mail.address.correctly";
    /**
     * 该手机号已被注册
     **/
    String PHONE_NUMBER_REGISTERED = "phone.number.registered";
    /**
     * 该邮箱已被注册
     **/
    String MAIL_ADDRESS_REGISTERED = "mail.address.registered";
    /**
     * 注册用户已经存在
     **/
    String USER_ALREADY_EXIST = "user.already.exist";
    /**
     * 请正确填写登录密码
     **/
    String FILL_LOGIN_PASSWORD_CORRECTLY = "fill.login.password.correctly";
    /**
     * 操作失败
     **/
    String ACTION_FAILURE = "action.failure";
    /**
     * 操作成功
     **/
    String ACTION_SUCCESS = "action.success";
    /**
     * 邮件发送失败,请重新操作
     **/
    String SEND_MAIL_FAILED = "send.mail.failed";
    /**
     * 注册成功
     **/
    String REGISTER_SUCCESS = "register.success";
    /**
     * 注册失败
     **/
    String REGISTER_FAILURE = "register.failure";
    /**
     * 请正确填写验证码
     **/
    String FILL_VERIFYING_CODE_CORRECTLY = "fill.verifying.code.correctly";
    /**
     * 论坛同步注册失败
     **/
    String FORUM_SYNCHRONIZED_REGISTRATION_FAILURE = "Forum.synchronized.registration.failure";
    /**
     * 论坛同步注登录失败
     **/
    String FORUM_SYNCHRONIZED_LOGIN_FAILURE = "Forum.synchronized.login.failure";
    /**
     * 参数不能为空
     **/
    String PARAMETERS_NOT_EMPTY = "Parameters.not.empty";
    /**
     * 提交信息有误
     **/
    String INFORMATION_INCORRECT = "information.incorrect";
    /**
     * 用户名不能为空
     **/
    String USERNAME_NOT_EMPTY = "username.not.empty";
    /**
     * 用户信息不正确
     **/
    String USER_INFORMATION_INCORRECT = "user.information.incorrect";
    /**
     * 没有需要修改的信息
     **/
    String NO_INFORMATION_MODIFIED = "no.information.modified";
    /**
     * 旧密码不能为空
     **/
    String OLD_PASSWORD_NOT_EMPTY = "old.password.not.empty";
    /**
     * 新密码不能为空
     **/
    String NEW_PASSWORD_NOT_EMPTY = "new.password.not.empty";
    /**
     * 输入的旧密码错误
     **/
    String OLD_PASSWORD_ERROR = "odl.password.error";
    /**
     * 邮箱正在使用
     **/
    String MAIL_ADDRESS_BEING_USED = "mail.address.being.used";
    /**
     * 邮箱已被使用
     **/
    String MAIL_ADDRESS_BEEN_USED = "mail.address.been.used";
    /**
     * 手机号正在使用
     **/
    String PHONE_NUMBER_BEING_USED = "phone.number.being.used";
    /**
     * 手机号已被使用
     **/
    String PHONE_NUMBER_BEEN_USED = "phone.number.been.used";
    /**
     * 账号未激活
     **/
    String ACCOUNT_NOT_ACTICATED = "account.not.acticated";
    /**
     * 用户已经登录
     **/
    String USER_BEEN_LOGIN = "user.been.login";
    /**
     * 登录成功
     **/
    String LOGIN_SUCCESS = "login.success";
    /**
     * 登录失败
     **/
    String LOGIN_FAILURE = "login.failure";
    /**
     * 用户信息异常
     **/
    String USER_INFORMATION_EXECPTION = "user.information.execption";
    /**
     * 登录账号或密码错误
     **/
    String ACCOUNT_OR_PASSWORD_ERROR = "account.or.password.error";
    /**
     * 账号有误
     **/
    String ACCOUNT_ERROR = "account.error";
    /**
     * 退出成功
     **/
    String LOGOUT_SUCCESS = "logout.success";
    /**
     * 退出失败
     **/
    String LOGOUT_FAILURE = "logout.failure";
    /**
     * 论坛退出异常
     **/
    String FORUM_LOGOUT_FAILURE = "forum.logout.failure";
    /**
     * 用户id不能为空
     **/
    String USER_ID_NOT_EMPTY = "user.id.not.empty";
    /**
     * 输入的邮箱格式不正确
     **/
    String MAIL_ADDRESS_FORMAT_INCORRECT = "fmail.address.format.incorrect";
    /**
     * 账号非法
     **/
    String ACCOUNT_ILLAGAL = "account.illagal";
    /**
     * 密码不能为空
     **/
    String PASSWORD_NOT_EMPTY = "password.not.empty";
    /**
     * 重置密码失败
     **/
    String RESET_PASSWORD_FAILURE = "reset.password.failure";
    /**
     * 重置密码成功
     **/
    String RESET_PASSWORD_SUCCESS = "reset.password.success";
    /**
     * 邮箱格式不正确，激活失败
     **/
    String MAIL_ADDRESS_ACTIVATE_FAILURE = "mail.address.activate.failure";
    /**
     * 该邮箱未注册，无法激活
     **/
    String MAIL_ADDRESS_NOT_REGISTERED = "mail.address.not.registered";
    /**
     * 账号已经激活，请勿重复操作
     **/
    String ACCOUNT_BEEN_ACTIVATED = "account.been.activated";
    /**
     * 账号激活成功
     **/
    String ACCOUNT_ACTIVATION_SUCCESS = "account.activation.success";
    /**
     * 账号激活失败
     **/
    String ACCOUNT_ACTIVATION_FAILURE = "account.activation.failure";
    /**
     * 发送内容不能为空
     **/
    String SEND_CONTENT_NOT_EMPTY = "send.content.not.empty";
    /**
     * 未正确填写收款地址,请重新填写
     **/
    String FILL_RECEIVABLE_ADDRESS_CORRECTLY = "fill.receivable.address.correctly";
    /**
     * 用户名不正确
     **/
    String USERNAME_INCORRECT = "username.incorrect";
    /**
     * 您未修改地址
     **/
    String NOT_CHANGED_ADDRESS = "not.changed.address";
    /**
     * 邮件已发送到
     **/
    String MAIL_BEEN_SEND = "mail.been.send";
    /**
     * 请前往查收
     **/
    String PLEASE_CHECK = "please.check";
    /**
     * 请尽快修改您的密码
     */
    String MODIFY_YOUR_PASSWORD = "modify.your.password";
    /**
     * 请尽快登录获取
     */
    String PLEAST_LOG_IN = "please.log.in";
    /**
     * 获取申请列表失败
     **/
    String OBTAIN_APPLICATION_LIST_FAILURE = "obtain.application.list.failure";
    /**
     * 您已经提交过申请,请勿重复提交
     **/
    String SUBMITTED_APPLICATION = "submitted.application";
    /**
     * 申请已经提交,等待审核中
     **/
    String APPLICATON_BEEN_SUBMITED = "applicaton.been.submited";
    /**
     * 申请异常
     **/
    String APPLICATION_EXECPTION = "application.execption";
    /**
     * 您还未提交过申请
     **/
    String HAVE_NOT_SUBMITED_APPLICATION = "have.not.submited.application";
    /**
     * 取消申请成功
     **/
    String CANEL_APPLICATION_SUCCESS = "canel.application.success";
    /**
     * 您已是大V用户
     **/
    String ALREADY_BIG_V_USER = "already.big.V.user";
    /**
     * 批准申请
     **/
    String APPROVAL_APPLICATION = "approval.application";
    /**
     * 拒绝申请
     **/
    String REFUSE_APPLICATION = "refuse.application";
    /**
     * 认证成功
     */
    String CERTIFICATION_SUCCESS = "certification.success";

    /**
     * 认证码错误
     */
    String AUTHENTICATION_CODE_ERROR = "authentication.code.error";
    /**
     * 未绑定
     */
    String UNBOUND = "unbound";
    /**
     * 已绑定
     */
    String BINDINGS = "bindings";
    /**
     * 验证码不能为空
     */
    String VERIFYING_CODE_NOT_EMPTY = "verifying.code.not.empty";
    /**
     * 注册成功，验证信息已经发送到邮箱：
     */
    String REGISTER_SUCCESS_EMAIL = "register.success.email";
    /**
     * 中，请前往操作
     */
    String PLEASE_OPERATION = "please.operation";
    /**
     * 登录名已经存在
     */
    String LOGIN_NAME_EXISTED = "login.name.existed";
    /**
     * 验证码错误
     */
    String VERIFICATION_CODE_ERROR = "verification.code.error";
    /**
     * 验证码正确
     */
    String VERIFICATION_CODE_CORRECT = "verification.code.correct";
    /**
     * 短信发送失败,请重新操作
     */
    String SMS_SENDING_FAILURE = "sms.sending.failure";
    /**
     * 验证成功
     */
    String VERIFICATION_SUCCESS = "verification.success";
    /**
     * 验证失败
     */
    String VERIFICATION_FAILURE = "verification.failure";
    /**
     * 短信已发送到：
     */
    String SMS_BEEN_SEND = "sms.been.send";

    /**
     * 修改的昵称不能为空
     */
    String MODIFY_NICKNAME_NOT_EMPTY = "modify.nickname.not.empty";
    /**
     * 您未做任何修改
     */
    String NOT_MADE_ANY_CHANGES = "not.made.any.changes";

    String SIGN_UP_MSG_SUCCESS = "sign.up.message.success";
    String SIGN_UP_MSG_FAILURE = "sign.up.message.failure";
    String SIGN_UP_MSG_ALREADY  = "sign.up.message.already";

}
