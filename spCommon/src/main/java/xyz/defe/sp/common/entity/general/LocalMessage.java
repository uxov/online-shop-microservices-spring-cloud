package xyz.defe.sp.common.entity.general;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import xyz.defe.sp.common.enums.LocalMsgState;

import java.util.Date;

@Entity
public class LocalMessage {
    @Id
    private String id;

    @Column(columnDefinition="text")
    private String msgJson;
    private String msgType;

    @Enumerated(EnumType.STRING)
    private LocalMsgState msgState = LocalMsgState.INIT;
    private Integer resendTimes = 0;

    @CreationTimestamp
    @Column(nullable = false)
    private Date createdTime;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date updatedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsgJson() {
        return msgJson;
    }

    public void setMsgJson(String msgJson) {
        this.msgJson = msgJson;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public LocalMsgState getMsgState() {
        return msgState;
    }

    public void setMsgState(LocalMsgState msgState) {
        this.msgState = msgState;
    }

    public Integer getResendTimes() {
        return resendTimes;
    }

    public void setResendTimes(Integer resendTimes) {
        this.resendTimes = resendTimes;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
