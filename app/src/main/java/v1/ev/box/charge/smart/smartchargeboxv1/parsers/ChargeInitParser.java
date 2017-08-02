package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import v1.ev.box.charge.smart.smartchargeboxv1.data_models.ChargeInitDataModel;

/**
 * Created by Deividas on 2017-04-29.
 */

public class ChargeInitParser extends AsyncTask<String, Void, ChargeInitDataModel> {

    @Override
    protected ChargeInitDataModel doInBackground(String... strings) {
        ChargeInitDataModel result = new ChargeInitDataModel();
        try {
            JSONObject jsonObject = new JSONObject(strings[0]);
            if(jsonObject.has("insertedId")) {
                String chargeSessionId = jsonObject.getString("insertedId");
                result.setChargeSessionId(chargeSessionId);
            }
            if(jsonObject.has("obj") && jsonObject.get("obj") instanceof JSONObject) {
                JSONObject obj = jsonObject.getJSONObject("obj");
                if(obj.has("userId")) {
                    String userId = obj.getString("userId");
                    result.setUserId(userId);
                }
                if(obj.has("stationId")) {
                    String stationId = obj.getString("stationId");
                    result.setStationId(stationId);
                }
                if(obj.has("startTime")) {
                    long startTime = obj.getLong("startTime");
                    result.setStartTime(startTime);
                }
                if(obj.has("endTime")) {
                    long endTime = obj.getLong("endTime");
                    result.setEndTime(endTime);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(ChargeInitDataModel chargeInitDataModel) {
        super.onPostExecute(chargeInitDataModel);

        if(chargeInitDataModel != null) {
            EventBus.getDefault().post(chargeInitDataModel);
        }
    }
}
