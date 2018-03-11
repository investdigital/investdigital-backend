package info.investdigital.common;

/**
 * @author ccl
 * @time 2017-11-01 16:50
 * @name ParamType
 * @desc:
 */
public interface ParamType {

    enum UpdateUserInfoType implements ParamType {
        ADD(1),INFO(2),PWD(3),EMAIL(4),PHONE(5),FPWD(6);

        UpdateUserInfoType(int type){
            this.type=type;
        }
        private int type;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
    enum FundApplyForStatus implements ParamType{
        APPLYFORING(1,"请求中"),ALLOW(2,"已通过"),UNALLOW(3,"未通过");
        private int status;
        private String desc;

        public int getStatus() {
            return status;
        }

        public String getDesc() {
            return desc;
        }

        FundApplyForStatus(int status, String desc) {
            this.status = status;
            this.desc = desc;
        }
    }
    enum TrustTabType implements ParamType{
        TRUSTED(1),TRUST(2),SHIELD(3);

        TrustTabType(int type) {
            this.type = type;
        }

        private int type;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    enum LoginType implements ParamType{
        LOGINNAME("用户名",1),MOBILEPHONE("手机",2),EMAIL("邮箱",3),OTHER("其他",4);
        LoginType(String name,int type){}
        private String name;
        private int type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    enum LoginDevice implements ParamType{
    }
    enum ContractAddress implements ParamType{
        USER_CONTRACT(""),
        FUND_MANAGE_CONTRACT(""),
        FUND_SHARE_CONTRACT("");
        private String address;

        ContractAddress(String address) {
            this.address = address;
        }

        public String getAddress() {
            return address;
        }
    }
}
