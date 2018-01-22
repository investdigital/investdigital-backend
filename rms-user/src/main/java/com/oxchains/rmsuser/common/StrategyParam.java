package com.oxchains.rmsuser.common;

/**
 * Created by xuqi on 2017/12/18.
 */
public interface StrategyParam {

    enum StrategyType{
        CODE(0,"Code"),WIZARD(1,"Wizard"),NO(2,"No");
        private Integer status;
        private String value;

        StrategyType(Integer status, String value) {
            this.status = status;
            this.value = value;
        }
        public static String getValue(Integer status){
            if(status != null){
                for (StrategyType s: StrategyType.values()) {
                    if(status == s.status.intValue()){
                        return s.value;
                    }
                }
            }
            return "";

        }
    }
}
