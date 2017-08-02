package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import v1.ev.box.charge.smart.smartchargeboxv1.data_models.CommentDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.NewCommentCreatedEvent;

/**
 * Created by Deividas on 2017-05-02.
 */

public class OneCommentParser extends AsyncTask<String, Void, NewCommentCreatedEvent> {
    @Override
    protected NewCommentCreatedEvent doInBackground(String... strings) {
        NewCommentCreatedEvent obj = new NewCommentCreatedEvent();
        Log.d("SDSGDUISDSD", strings[0]);
        try {
            JSONObject jsonObject = new JSONObject(strings[0]);
            if(jsonObject.has("_id")) {
                obj.setCommentId(jsonObject.getString("_id"));
            }
            if(jsonObject.has("userId")) {
                obj.setUserId(jsonObject.getString("userId"));
            }
            if(jsonObject.has("img")) {
                obj.setUserImg(jsonObject.getString("img"));
            }
            if(jsonObject.has("name")) {
                obj.setUserName(jsonObject.getString("name"));
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    @Override
    protected void onPostExecute(NewCommentCreatedEvent newCommentCreatedEvent) {
        super.onPostExecute(newCommentCreatedEvent);

        EventBus.getDefault().post(newCommentCreatedEvent);
    }
}
