package v1.ev.box.charge.smart.smartchargeboxv1.events.array_list_events;

import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.data_models.CommentDataModel;

/**
 * Created by Deividas on 2017-04-28.
 */

public class CommentsListEvent {
    private ArrayList<CommentDataModel> list;

    public ArrayList<CommentDataModel> getList() {
        return list;
    }

    public void setList(ArrayList<CommentDataModel> list) {
        this.list = list;
    }
}
