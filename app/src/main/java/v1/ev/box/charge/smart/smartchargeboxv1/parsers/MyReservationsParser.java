package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import v1.ev.box.charge.smart.smartchargeboxv1.data_models.MyReservationsDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationTimesEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.array_list_events.ReservationsListHandlerEvent;

/**
 * Created by Deividas on 2017-04-25.
 */

public class MyReservationsParser extends AsyncTask<String, Void, ReservationsListHandlerEvent> {
    @Override
    protected ReservationsListHandlerEvent doInBackground(String... strings) {
        ArrayList<MyReservationsDataModel> listObj = new ArrayList<>();
        ReservationsListHandlerEvent result = new ReservationsListHandlerEvent();
//        try {
//            JSONArray jsonArray = new JSONArray(strings[0]);
//            Calendar startCalendar = Calendar.getInstance();
//            long offset = startCalendar.get(Calendar.ZONE_OFFSET) + startCalendar.get(Calendar.DST_OFFSET);
//            for(int i=0; i<jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                String id = jsonObject.getString("_id");
//                long start = 0;
//                long end = 0;
//                String stationId = null;
//                if(jsonObject.has("start_date")) {
//                    JSONObject startObj = jsonObject.getJSONObject("start_date");
//                    start = startObj.getLong("$date") - offset;
//                }
//                if(jsonObject.has("expire_date")) {
//                    JSONObject endObj = jsonObject.getJSONObject("expire_date");
//                    end = endObj.getLong("$date") - offset;
//                }
//                if(jsonObject.has("station_id")) {
//                    stationId = jsonObject.getString("station_id");
//                }
//                Log.d("SDSDGYUOSDSD", id + " " + stationId);
//                list.add(new MyReservationsDataModel(id, start, end, stationId));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        try {
            JSONObject mainjsonObject = new JSONObject(strings[0]);
            if(mainjsonObject.has("activeIds") && mainjsonObject.get("activeIds") instanceof JSONArray) {
                JSONArray jsonArray = mainjsonObject.getJSONArray("activeIds");
                for (int i=0; i<jsonArray.length(); i++) {
                    result.activeId = jsonArray.getString(i);;
                }
            }
            Calendar startCalendar = Calendar.getInstance();
           long offset = startCalendar.get(Calendar.ZONE_OFFSET) + startCalendar.get(Calendar.DST_OFFSET);
            if(mainjsonObject.has("list") && mainjsonObject.get("list") instanceof JSONArray) {
                JSONArray list = mainjsonObject.getJSONArray("list");
                for (int i=0; i<list.length(); i++) {
                    JSONObject jsonObject = list.getJSONObject(i);
                    String id = jsonObject.getString("_id");
                    long start = 0;
                    long end = 0;
                    String stationId = null;
                    if(jsonObject.has("start_date")) {
                        JSONObject startObj = jsonObject.getJSONObject("start_date");
                        start = startObj.getLong("$date") - offset;
                    }
                    if(jsonObject.has("expire_date")) {
                        JSONObject endObj = jsonObject.getJSONObject("expire_date");
                        end = endObj.getLong("$date") - offset;
                    }
                    if(jsonObject.has("station_id")) {
                        stationId = jsonObject.getString("station_id");
                    }
                    Log.d("SDSDGYUOSDSD", id + " " + stationId);
                    listObj.add(new MyReservationsDataModel(id, start, end, stationId));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result.list = listObj;
        return result;
    }

    @Override
    protected void onPostExecute(ReservationsListHandlerEvent reservationTimesEvents) {
        super.onPostExecute(reservationTimesEvents);

        EventBus.getDefault().post(reservationTimesEvents);
    }
}
