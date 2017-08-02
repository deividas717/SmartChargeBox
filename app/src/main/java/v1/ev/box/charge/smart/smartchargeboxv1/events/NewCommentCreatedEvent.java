package v1.ev.box.charge.smart.smartchargeboxv1.events;

import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;

/**
 * Created by Deividas on 2017-04-29.
 */

public class NewCommentCreatedEvent {
    private String commentId;
    private String userId;
    private String userImg;
    private String userName;
    private String comment;
    private long time;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
