package com.cuctut.common.constant;


import lombok.Getter;

/**
 * 通用常量
 *
 * @author cuctut
 * @since 2024/10/01
 */
public class CommonConsts {

    /**
     * 是
     */
    public static final Integer YES = 1;
    public static final String TRUE = "true";
    /**
     * 否
     */
    public static final Integer NO = 0;
    public static final String FALSE = "false";

    /**
     * 性别常量
     */
    @Getter
    public enum SexEnum {
        /**
         * 男
         */
        MALE(0, "男"),
        /**
         * 女
         */
        FEMALE(1, "女");

        SexEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        private final int code;
        private final String desc;

    }
}
