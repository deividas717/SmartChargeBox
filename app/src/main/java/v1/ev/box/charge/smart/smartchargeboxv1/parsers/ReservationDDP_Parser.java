package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationTimesEvent;

/**
 * Created by Deividas on 2017-04-27.
 */

public class ReservationDDP_Parser extends AsyncTask<String, Void, ReservationTimesEvent> {
    @Override
    protected ReservationTimesEvent doInBackground(String... strings) {
        try {
            JSONObject jsonObject = new JSONObject(strings[0]);
            if(jsonObject.has("station_id")) {

            }
            String userId = null;
            if(jsonObject.has("id_Tag")) {
                userId = jsonObject.getString("id_Tag");
            }
            long start = 0;
            if(jsonObject.has("start_date")) {
                JSONObject start_dateObj = jsonObject.getJSONObject("start_date");
                if(start_dateObj.has("$date")) {
                    start = start_dateObj.getLong("$date");
                }
            }
            long end = 0;
            if(jsonObject.has("expire_date")) {
                JSONObject end_dateObj = jsonObject.getJSONObject("expire_date");
                if(end_dateObj.has("$date")) {
                    end = end_dateObj.getLong("$date");
                }
            }
            return new ReservationTimesEvent(-1, strings[1], start, end, userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ReservationTimesEvent reservationTimesEvent) {
        super.onPostExecute(reservationTimesEvent);

        if(reservationTimesEvent != null) {
            EventBus.getDefault().post(reservationTimesEvent);
        }
    }
}
