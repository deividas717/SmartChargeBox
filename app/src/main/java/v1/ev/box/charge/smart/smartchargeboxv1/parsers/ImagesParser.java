package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.data_models.ImagesDataModel;

/**
 * Created by Deividas on 2017-05-07.
 */

public class ImagesParser extends AsyncTask<String, Void, ImagesDataModel> {

    @Override
    protected ImagesDataModel doInBackground(String... strings) {
        ImagesDataModel obj = new ImagesDataModel();
        ArrayList<String> imgsList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(strings[0]);
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.has("imgsList") && jsonObject.get("imgsList") instanceof JSONArray) {
                    JSONArray list = jsonObject.getJSONArray("imgsList");
                    for(int j=0; j< list.length(); j++) {
                        imgsList.add(list.getString(j));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        obj.setImgs(imgsList);
        return obj;
    }

    @Override
    protected void onPostExecute(ImagesDataModel imagesDataModel) {
        super.onPostExecute(imagesDataModel);

        EventBus.getDefault().post(imagesDataModel);
    }
}
