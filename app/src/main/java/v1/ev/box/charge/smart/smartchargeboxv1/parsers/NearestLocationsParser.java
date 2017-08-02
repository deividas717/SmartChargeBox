package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.data_models.LocationDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.NearestLocationListEvent;

/**
 * Created by Deividas on 2017-04-21.
 */

public class NearestLocationsParser extends AsyncTask<String, Void, NearestLocationListEvent> {

    @Override
    protected  NearestLocationListEvent doInBackground(String... strings) {
        NearestLocationListEvent obj = new NearestLocationListEvent();
        ArrayList<LocationDataModel> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(strings[0]);
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject locationDetail = jsonArray.getJSONObject(i);
                LocationDataModel model = new LocationDataModel();
                if(locationDetail.has("_id") ) {
                    String id = locationDetail.getString("_id");
                    model.setId(id);
                }
                if(locationDetail.has("reverse_geocoded_address")) {
                    String reverseGeocoderAddress = locationDetail.getString("reverse_geocoded_address");
                    model.setReverseGeocoderAddress(reverseGeocoderAddress);
                }
                if(locationDetail.has("description")) {
                    String description = locationDetail.getString("description");
                    model.setDescription(description);
                }
                if(locationDetail.has("phone") && locationDetail.get("phone") instanceof String) {
                    String phone = locationDetail.getString("phone");
                    model.setPhone(phone);
                }
                if(locationDetail.has("address")) {
                    String address = locationDetail.getString("address");
                    model.setAddress(address);
                }
                if(locationDetail.has("url")) {
                    String url = locationDetail.getString("url");
                    model.setUrl(url);
                }
                if(locationDetail.has("latitude")) {
                    double latitude = locationDetail.getDouble("latitude");
                    model.setLat(latitude);
                }
                if(locationDetail.has("longitude")) {
                    double longitude = locationDetail.getDouble("longitude");
                    model.setLng(longitude);
                }
                list.add(model);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        obj.list = list;
        return obj;
    }

    @Override
    protected void onPostExecute(NearestLocationListEvent locationDataModels) {
        super.onPostExecute(locationDataModels);

        EventBus.getDefault().post(locationDataModels);

    }
}
