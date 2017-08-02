package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import v1.ev.box.charge.smart.smartchargeboxv1.data_models.EditedCommentDataModel;

/**
 * Created by Deividas on 2017-05-06.
 */

public class EditedCommentParser extends AsyncTask<String, Void, EditedCommentDataModel> {
    @Override
    protected EditedCommentDataModel doInBackground(String... strings) {
        EditedCommentDataModel obj = new EditedCommentDataModel();
        try {
            JSONObject jsonObject = new JSONObject(strings[0]);
            if(jsonObject.has("result")) {
                obj.setText(jsonObject.getString("result"));
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
    protected void onPostExecute(EditedCommentDataModel editedCommentDataModel) {
        super.onPostExecute(editedCommentDataModel);

        EventBus.getDefault().post(editedCommentDataModel);
    }
}
