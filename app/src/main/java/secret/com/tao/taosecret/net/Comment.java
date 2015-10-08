package secret.com.tao.taosecret.net;

/**
 * Created by Adminstrator on 2015/9/22.
 */
public class Comment {
    private String content, phoneNum,cmttime;

    public Comment(String content, String phoneNum, String cmttime) {
        this.content = content;
        this.phoneNum = phoneNum;
        this.cmttime = cmttime;
    }

    public String getCmttime() {
        return cmttime;
    }


    public String getContent() {
        return content;
    }


    public String getPhoneNum() {
        return phoneNum;
    }
}
