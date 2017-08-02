package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import v1.ev.box.charge.smart.smartchargeboxv1.data_models.CommentRemoveDataModel;

/**
 * Created by Deividas on 2017-05-06.
 */

public class CommentRemoveAsyncTask extends AsyncTask<String, Void, CommentRemoveDataModel> {
    @Override
    protected CommentRemoveDataModel doInBackground(String... strings) {
        CommentRemoveDataModel obj = new CommentRemoveDataModel();
        try {
            JSONObject jsonObject = new JSONObject(strings[0]);
            if(jsonObject.has("result")) {
                obj.setCount(jsonObject.getInt("result"));
            }
            if(jsonObject.has("id")) {
                obj.setCommentId(jsonObject.getString("id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    protected void onPostExecute(CommentRemoveDataModel commentRemoveDataModel) {
        super.onPostExecute(commentRemoveDataModel);

        EventBus.getDefault().post(commentRemoveDataModel);
    }
}
