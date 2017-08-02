package v1.ev.box.charge.smart.smartchargeboxv1.data_models;

/**
 * Created by Deividas on 2017-05-06.
 */

public class EditedCommentDataModel {
    private String text;
    private String commentId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
