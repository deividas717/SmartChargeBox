package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;
import android.util.Log;

import org.codehaus.jackson.map.util.JSONPObject;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.data_models.ChargingStatsDataModel;

/**
 * Created by Deividas on 2017-05-04.
 */

public class ChargingStatisticsParser extends AsyncTask<String, Void, ChargingStatsDataModel> {
    @Override
    protected ChargingStatsDataModel doInBackground(String... strings) {
        ChargingStatsDataModel result = new ChargingStatsDataModel();
        ArrayList<ChargingStatsDataModel.ChargingStatsDataModelInnerClass> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(strings[0]);
            for(int i=0; i<jsonArray.length(); i++) {
                ChargingStatsDataModel.ChargingStatsDataModelInnerClass obj = new ChargingStatsDataModel.ChargingStatsDataModelInnerClass();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.has("timeStamp")) {
                    String time = jsonObject.getString("timeStamp");
                    String[] array = time.split("-");
                    if( array.length > 0) {
                        int month = Integer.parseInt(array[1]);
                        int day = Integer.parseInt(array[2]);
                        obj.month = month;
                        obj.day = day;
                        Log.d("DSHISODs", month + " " + day);
                    }
                }
                if(jsonObject.has("chargingTime")) {
                    obj.time = jsonObject.getLong("chargingTime");
                }
                list.add(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result.list = list;
        return result;
    }

    @Override
    protected void onPostExecute(ChargingStatsDataModel chargingStatsDataModel) {
        super.onPostExecute(chargingStatsDataModel);

        EventBus.getDefault().post(chargingStatsDataModel);
    }
}
