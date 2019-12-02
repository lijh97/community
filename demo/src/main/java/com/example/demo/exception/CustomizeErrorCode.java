package com.example.demo.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {

    QUESTION_NOT_FOUND(2001, "查询问题不存在"),
    TARGET_PARAM_NOT_FOUND(2002, "未选中任何问题或评论进行回复"),
    NO_LOGIN(2003, "当前操作需要登录，请登录后重试"),
    SYS_ERROR(2004, "服务器繁忙，请稍后再试！"),
    TYPE_PARAM_WRONG(2005, "评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006, "查询评论不存在"),
    CONTENT_IS_EMPTY(2007, "输入内容不存在!"),
    READ_NOTIFICATION_FAIL(2008, "查看通知失败"),
    NOTIFICATION_NOT_FOUND(2009, "通知不存在，请重试"),
    FILE_UPLOAD_FAIL(2010,"文件上传失败");

    private Integer code;
    private String message;


    CustomizeErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
