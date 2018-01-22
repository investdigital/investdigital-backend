package com.oxchains.rmsuser.common;

/**
 * @author ccl
 * @time 2017-11-06 14:12
 * @name Status
 * @desc:
 */
public interface Status {
    enum LoginStatus{
        LOGOUT(0,"未登录"),LOGIN(1,"已登录");
        private Integer status;
        private String name;

        LoginStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    enum TrustStatus{
        NONE(0,"无记录"),TRUST(1,"信任"),SHIELD(2,"屏蔽");
        private Integer status;
        private String name;

        TrustStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    enum EnableStatus {
        UNENABLED(0,"不可用"),ENABLED(1,"可用");
        private Integer status;
        private String name;

        EnableStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
