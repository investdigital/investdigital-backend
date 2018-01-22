package com.oxchains.rmsuser.entity;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author ccl
 * @time 2017-11-13 10:13
 * @name Email
 * @desc:
 */
public class Email implements Serializable{
    private static final long serialVersionUID = -1L;

    /**
     * 接受方邮件
     */
    private String[] email;
    /**
     * 主题
     */
    private String subject;
    /**
     * 邮件内容
     */
    private String content;
    /**
     * 模板
     */
    private String template;
    /**
     * 自定义参数
     */
    private HashMap<String,String> paramsMap;

    public Email() {
    }

    public Email(String[] email, String subject, String content) {
        this.email = email;
        this.subject = subject;
        this.content = content;
    }

    public Email(String[] email, String subject, String content, String template, HashMap<String, String> paramsMap) {
        this.email = email;
        this.subject = subject;
        this.content = content;
        this.template = template;
        this.paramsMap = paramsMap;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String[] getEmail() {
        return email;
    }

    public void setEmail(String[] email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public HashMap<String, String> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(HashMap<String, String> paramsMap) {
        this.paramsMap = paramsMap;
    }
}
