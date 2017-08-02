package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.data_models.CommentDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.array_list_events.CommentsListEvent;

/**
 * Created by Deividas on 2017-04-28.
 */

public class CommentsParser extends AsyncTask<String, Void, CommentsListEvent> {

    @Override
    protected CommentsListEvent doInBackground(String... strings) {
        ArrayList<CommentDataModel> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(strings[0]);
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CommentDataModel obj = new CommentDataModel();
                if(jsonObject.has("_id")) {
                    obj.setId(jsonObject.getString("_id"));
                }
                if(jsonObject.has("userId")) {
                    obj.setUserId(jsonObject.getString("userId"));
                }
                if(jsonObject.has("img")) {
                    obj.setImgUrl(jsonObject.getString("img"));
                }
                if(jsonObject.has("name")) {
                    obj.setName(jsonObject.getString("name"));
                }
                if(jsonObject.has("comment")) {
                    obj.setComment(jsonObject.getString("comment"));
                }
                if(jsonObject.has("time") && jsonObject.get("time") instanceof JSONObject) {
                    JSONObject date = jsonObject.getJSONObject("time");
                    if(date.has("$date")) {
                        long time = date.getLong("$date");
                        obj.setTime(time);
                    }
                }
                list.add(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CommentsListEvent event = new CommentsListEvent();
        event.setList(list);
        return event;
    }

    @Override
    protected void onPostExecute(CommentsListEvent commentDataModel) {
        super.onPostExecute(commentDataModel);

        EventBus.getDefault().post(commentDataModel);
    }
}