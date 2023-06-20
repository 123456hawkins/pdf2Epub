package com.example.pdfconvertepub.domain;

import java.util.Date;

/**
 * 定时任务日志实体类
 * @author songzhe
 * @date 2020/12/29 15:21
 */
public class ErrorLog {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 调用名称
     */
    private String callName;

    /**
     * 调用时间
     */
    private Date callTime;

    /**
     * 调用方法全量名
     */
    private String callFunctionFullName;

    /**
     * 异常信息
     */
    private String exceptionMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public Date getCallTime() {
        return callTime;
    }

    public void setCallTime(Date callTime) {
        this.callTime = callTime;
    }

    public String getCallFunctionFullName() {
        return callFunctionFullName;
    }

    public void setCallFunctionFullName(String callFunctionFullName) {
        this.callFunctionFullName = callFunctionFullName;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public String toString() {
        return "ErrorLog{" +
                "id=" + id +
                ", callName='" + callName + '\'' +
                ", callTime=" + callTime +
                ", callFunctionFullName='" + callFunctionFullName + '\'' +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                '}';
    }
}
