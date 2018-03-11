package info.investdigital.entity;

/**
 * @author oxchains
 * @time 2017-12-04 10:06
 * @name VerifyCode
 * @desc:
 */
public class VerifyCode {
    String key;
    String vcode;
    Integer type;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
