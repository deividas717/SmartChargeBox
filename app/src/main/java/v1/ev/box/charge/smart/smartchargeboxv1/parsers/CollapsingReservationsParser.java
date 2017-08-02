package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.data_models.ReservationCollapsesDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.array_list_events.CollapseEvent;

/**
 * Created by Deividas on 2017-04-27.
 */

public class CollapsingReservationsParser extends AsyncTask<String, Void, CollapseEvent> {

    @Override
    protected CollapseEvent doInBackground(String... strings) {
        ArrayList<ReservationCollapsesDataModel> list = new ArrayList<>();
        CollapseEvent event = new CollapseEvent();

        try {
            JSONObject resObject = new JSONObject(strings[0]);
            if(resObject.has("activeSessions")) {
                JSONObject activeObj = resObject.getJSONObject("activeSessions");
                if(activeObj.has("reservationId")) {
                    event.setActiveId(activeObj.getString("reservationId"));
                }
            }
            if (resObject.has("problem")) {
                event.setDetailProblem(resObject.getString("problem"));
            }
            if (resObject.has("result") && resObject.get("result") instanceof JSONArray) {
                JSONArray jsonArray = resObject.getJSONArray("result");
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ReservationCollapsesDataModel tmp = new ReservationCollapsesDataModel();
                    if(jsonObject.has("_id")) {
                        tmp.setReservationId(jsonObject.getString("_id"));
                    }
                    if(jsonObject.has("start_date")) {
                        JSONObject dateObj = jsonObject.getJSONObject("start_date");
                        if(dateObj.has("$date")) {
                            tmp.setStartTime(dateObj.getLong("$date"));
                        }
                    }
                    if(jsonObject.has("expire_date")) {
                        JSONObject dateObj = jsonObject.getJSONObject("expire_date");
                        if(dateObj.has("$date")) {
                            tmp.setEndTime(dateObj.getLong("$date"));
                        }
                    }
                    if(jsonObject.has("station_id")) {
                        tmp.setStationId(jsonObject.getString("station_id"));
                    }
                    if(jsonObject.has("id_Tag")) {
                        tmp.setUserId(jsonObject.getString("id_Tag"));
                    }
                    list.add(tmp);
                }
                event.setList(list);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return event;
    }

    @Override
    protected void onPostExecute(CollapseEvent event) {
        super.onPostExecute(event);

        EventBus.getDefault().post(event);
    }
}
