package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.data_models.QueryData;

/**
 * Created by Deividas on 2017-04-12.
 */

public class QueryResultParser extends AsyncTask<String, Void, ArrayList<QueryData>> {
    @Override
    protected ArrayList<QueryData> doInBackground(String... strings) {
        ArrayList<QueryData> list = null;
        try {
            JSONArray jsonArray = new JSONArray(strings[0]);
            list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                QueryData queryData = new QueryData();
                if(jsonObject.has("_id")) {
                    int id = jsonObject.getInt("_id");
                    queryData.setId(id);
                }
                if(jsonObject.has("latitude")) {
                    double lat = jsonObject.getDouble("latitude");
                    queryData.setLat(lat);
                }
                if(jsonObject.has("longitude")) {
                    double lng = jsonObject.getDouble("longitude");
                    queryData.setLng(lng);
                }
                if(jsonObject.has("address")) {
                    String address = jsonObject.getString("address");
                    queryData.setAddress(address);
                }
                list.add(queryData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onPostExecute(ArrayList<QueryData> queryList) {
        super.onPostExecute(queryList);

        EventBus.getDefault().post(queryList);
    }
}
