package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.data_models.LocationDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.StationDataModel;

/**
 * Created by Deividas on 2017-04-01.
 */

public class DetailBottomSheetDataParser extends AsyncTask<String, Void, LocationDataModel> {
    @Override
    protected LocationDataModel doInBackground(String... strings) {
        LocationDataModel model = new LocationDataModel();
        Log.d("DSFYSUODSDSDSDSD", strings[0]);
        try {
            ArrayList<StationDataModel> stations = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(strings[0]);
            if(jsonObject.has("isFav")) {
                int isFav = jsonObject.getInt("isFav");
                model.setIsFav(isFav);
            }
            if(jsonObject.has("locationDetail") && jsonObject.get("locationDetail") instanceof JSONObject) {
                JSONObject locationDetail = jsonObject.getJSONObject("locationDetail");
                if(locationDetail.has("_id")) {
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
                if(locationDetail.has("phone")) {
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
                if(locationDetail.has("rating")) {
                    model.setRating(locationDetail.getDouble("rating"));
                }
            }
            if (jsonObject.has("stationList") && jsonObject.get("stationList") instanceof JSONArray) {
                JSONArray jsonArray = jsonObject.getJSONArray("stationList");
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject chargePointObject = jsonArray.getJSONObject(i);
                    StationDataModel stationModel = new StationDataModel();
                    if (chargePointObject.has("_id")) {
                        String id = chargePointObject.getString("_id");
                        stationModel.setId(id);
                    }
                    if(chargePointObject.has("kilowatts")) {
                        String kilowatts = chargePointObject.getString("kilowatts");
                        stationModel.setKilowatts(kilowatts);
                    }
                    if(chargePointObject.has("name")) {
                        String name = chargePointObject.getString("name");
                        stationModel.setName(name);
                    }
                    if(chargePointObject.has("manufacturer")) {
                        String manufacturer = chargePointObject.getString("manufacturer");
                        stationModel.setManufacturer(manufacturer);
                    }
                    stations.add(stationModel);
                }
            }
            model.setStationsArray(stations);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }

    @Override
    protected void onPostExecute(LocationDataModel model) {
        super.onPostExecute(model);

        if(model == null) return;

        EventBus.getDefault().post(model);
    }
}