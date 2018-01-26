package com.oxchains.comments.common;

/**
 * @author oxchains
 * @time 2018-01-22 16:53
 * @name ConstEnum
 * @desc:
 */
public interface   ConstEnum {
    enum AppKey implements ConstEnum{
        /**
         * Thmis
         */
        THEMIS(1,"THEMIS"),
        /**
         * ID
         */
        INVESTDIGITAL(2,"INVESTDIGITAL");
        AppKey(int key, String value){
            this.key = key;
            this.value = value;
        }

        private int key;
        private String value;


        public static int getKey(String value){
            AppKey[] appKeys = AppKey.values();
            for(AppKey appKey : appKeys){
                if(appKey.getValue().equals(value)){
                    return appKey.getKey();
                }
            }
            return -1;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    enum FavorType implements ConstEnum{
        /**
         * 评论
         */
        COMMENT(1),
        /**
         * 回复
         */
        REPLAY(2);
        private int type;
        FavorType(int type){
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
