package v1.ev.box.charge.smart.smartchargeboxv1.data_models;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Deividas on 2017-04-08.
 */

public class LocationDataModel {
    private String id;
    private String reverseGeocoderAddress;
    private String description;
    private String phone;
    private String address;
    private String url;
    private double lat, lng;
    private double rating;
    private int isFav;

    public int getIsFav() {
        return isFav;
    }

    public void setIsFav(int isFav) {
        this.isFav = isFav;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double r) {
        rating = r;
    }

    private WeakReference<ArrayList<StationDataModel>> stationsRef;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReverseGeocoderAddress() {
        return reverseGeocoderAddress;
    }

    public void setReverseGeocoderAddress(String reverseGeocoderAddress) {
        this.reverseGeocoderAddress = reverseGeocoderAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setStationsArray(ArrayList<StationDataModel> stations){
        stationsRef = new WeakReference<>(stations);
    }

    public ArrayList<StationDataModel> getStations() {
        if(stationsRef != null) {
            return stationsRef.get();
        }
        return null;
    }
}
