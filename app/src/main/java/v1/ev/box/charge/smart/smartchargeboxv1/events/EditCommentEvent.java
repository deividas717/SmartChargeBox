package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-05-06.
 */

public class EditCommentEvent {
    private String commentId;
    private String comment;

    public EditCommentEvent(String commentId, String comment) {
        this.commentId = commentId;
        this.comment = comment;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getComment() {
        return comment;
    }
}
