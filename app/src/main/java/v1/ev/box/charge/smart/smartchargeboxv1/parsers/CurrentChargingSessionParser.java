package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import v1.ev.box.charge.smart.smartchargeboxv1.events.CurrentChargingSessionEvent;

/**
 * Created by Deividas on 2017-04-30.
 */

public class CurrentChargingSessionParser extends AsyncTask<String, Void, CurrentChargingSessionEvent> {
    private boolean fromMenu;

    public CurrentChargingSessionParser(boolean fromMenu) {
        this.fromMenu = fromMenu;
    }

    @Override
    protected CurrentChargingSessionEvent doInBackground(String... strings) {
        CurrentChargingSessionEvent result = new CurrentChargingSessionEvent();
        try {
            JSONArray jsonArray = new JSONArray(strings[0]);
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.has("_id")) {
                    result.setId(jsonObject.getString("_id"));
                }
                if(jsonObject.has("userId")) {
                    result.setUserId(jsonObject.getString("userId"));
                }
                if(jsonObject.has("station_id")) {
                    result.setStationId(jsonObject.getString("station_id"));
                }
                if(jsonObject.has("startTime")) {
                    result.setStartTime(jsonObject.getLong("startTime"));
                }
                if(jsonObject.has("endTime")) {
                    result.setEndTime(jsonObject.getLong("endTime"));
                }
                if(jsonObject.has("actualCharging")) {
                    result.setActualCharging(jsonObject.getBoolean("actualCharging"));
                }
                if(jsonObject.has("reservationId")) {
                    result.setReservationId(jsonObject.getString("reservationId"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result.setFromMenu(fromMenu);
        return result;
    }

    @Override
    protected void onPostExecute(CurrentChargingSessionEvent currentChargingSessionEvent) {
        super.onPostExecute(currentChargingSessionEvent);

        EventBus.getDefault().post(currentChargingSessionEvent);
    }
}
