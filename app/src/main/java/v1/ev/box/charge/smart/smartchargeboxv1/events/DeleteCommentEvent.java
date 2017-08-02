package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-05-06.
 */

public class DeleteCommentEvent {
    private String commentId;

    public DeleteCommentEvent(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentId() {
        return commentId;
    }
}
