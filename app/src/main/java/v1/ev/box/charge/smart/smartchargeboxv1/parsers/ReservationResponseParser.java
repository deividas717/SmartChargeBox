package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationParsedResponse;

/**
 * Created by Deividas on 2017-04-02.
 */

public class ReservationResponseParser extends AsyncTask<String, Void, ReservationParsedResponse> {
    private String jsonString;
    @Override
    protected ReservationParsedResponse doInBackground(String... strings) {
        String result;
        Log.d("SDSDFYUSDSD", strings[0]);

        jsonString = strings[0];
        ReservationParsedResponse obj = new ReservationParsedResponse();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if(jsonObject.has("status")) {
                String status = jsonObject.getString("status");
                boolean success = "success".equals(status);
                obj.success = success;

                if (!success && "overlaps".equals(status)) {
                    return null;
                }
            }

            if(jsonObject.has("insertionId")) {
                obj.reservationId = jsonObject.getString("insertionId");
            }
            if(jsonObject.has("result")) {
                JSONObject resultObject = jsonObject.getJSONObject("result");
                if(resultObject.has("start_date")) {
                    JSONObject startDate = resultObject.getJSONObject("start_date");
                    obj.startTime = startDate.getLong("$date");
                }
                if(resultObject.has("expire_date")) {
                    JSONObject endDateLong = resultObject.getJSONObject("expire_date");
                    obj.endTime = endDateLong.getLong("$date");
                }
                if (resultObject.has("station_id")) {
                    String stationId = resultObject.getString("station_id");
                    obj.stationId = stationId;
                }
                result = jsonObject.getString("result");
                obj.result = result;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    protected void onPostExecute(ReservationParsedResponse obj) {
        super.onPostExecute(obj);

        if(obj != null) {
            EventBus.getDefault().post(obj);
        } else {
            new CollapsingReservationsParser().execute(jsonString);
        }
    }
}
