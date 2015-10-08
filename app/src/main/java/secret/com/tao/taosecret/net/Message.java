package secret.com.tao.taosecret.net;

import java.io.Serializable;

/**
 * Created by Adminstrator on 2015/9/22.
 */
public class Message implements Serializable {
    private String msg;
    private String phoneNum;
    private String msgId;
    private String msgtime;
    private int commentCount;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public void setMsgtime(String msgtime) {
        this.msgtime = msgtime;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }


    public Message(String msg, String phoneNum, String msgId, String msgtime, int commentCount) {
        this.msg = msg;
        this.phoneNum = phoneNum;
        this.msgId = msgId;
        this.msgtime = msgtime;
        this.commentCount = commentCount;
    }

    public int getCommentCount() {
        return commentCount;
    }


    public String getMsgtime() {
        return msgtime;
    }

    public String getMsg() {
        return msg;
    }


    public String getPhoneNum() {
        return phoneNum;
    }


    public String getMsgId() {
        return msgId;
    }

}
