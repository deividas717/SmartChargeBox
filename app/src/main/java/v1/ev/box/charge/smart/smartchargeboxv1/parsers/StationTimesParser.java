package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationTimesArrayWrapperEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationTimesEvent;

/**
 * Created by Deividas on 2017-04-02.
 */

public class StationTimesParser extends AsyncTask<String, Void, ReservationTimesArrayWrapperEvent> {

    private ReservationTimesArrayWrapperEvent obj;
    public StationTimesParser(String id) {
        obj = new ReservationTimesArrayWrapperEvent();
        obj.stationId = id;
    }

    @Override
    protected ReservationTimesArrayWrapperEvent doInBackground(String... strings) {
        ArrayList<ReservationTimesEvent> array = new ArrayList<>();
        try {
            Log.d("SDGSDYUSD", strings[0]  + "");
            Calendar startCalendar = Calendar.getInstance();
            long offset = startCalendar.get(Calendar.ZONE_OFFSET) + startCalendar.get(Calendar.DST_OFFSET);
            JSONObject jsonObject = new JSONObject(strings[0]);
            if(jsonObject.has("result") && jsonObject.get("result") instanceof JSONArray) {
                JSONArray resultsArray = jsonObject.getJSONArray("result");
                for (int i=0; i<resultsArray.length(); i++) {
                    JSONObject arrObject = resultsArray.getJSONObject(i);
                    String id = arrObject.getString("_id");
                    long start = 0;
                    long end = 0;
                    if(arrObject.has("start_date")) {
                        JSONObject startObj = arrObject.getJSONObject("start_date");
                        start = startObj.getLong("$date") - offset;
                    }
                    if(arrObject.has("expire_date")) {
                        JSONObject endObj = arrObject.getJSONObject("expire_date");
                        end = endObj.getLong("$date") - offset;
                    }
                    String idTag = "";
                    if(arrObject.has("id_Tag")) {
                        idTag = arrObject.getString("id_Tag");
                    }
                    ReservationTimesEvent obj = new ReservationTimesEvent(i, id, start, end, idTag);
                    array.add(obj);
                }
            }
            if(jsonObject.has("activeChargerId") && jsonObject.get("activeChargerId") instanceof JSONArray) {
                JSONArray jsonArray = jsonObject.getJSONArray("activeChargerId");
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    if(jsonObject1.has("reservationId")) {
                        String activeId = jsonObject1.getString("reservationId");
                        obj.activeId = activeId;
                    }
                }
            }
        } catch (JSONException e) {
            Log.d("DSGUSDIPSD", e.getMessage());
            e.printStackTrace();
        }
        obj.list = array;
        return obj;
    }

    @Override
    protected void onPostExecute(ReservationTimesArrayWrapperEvent obj) {
        super.onPostExecute(obj);

        EventBus.getDefault().post(obj);
    }
}
