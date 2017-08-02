package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-05-02.
 */

public class FragmentDataToActivityEvent {
    private String url;
    private String phone;
    private float rating;
    private int isFav;
    private double lat;
    private double lng;

    public FragmentDataToActivityEvent(String url, String phone, double rating, int isFav, double lat, double lng) {
        this.url = url;
        this.phone = phone;
        this.isFav = isFav;
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getUrl() {
        return url;
    }

    public double getRating() {
        return rating;
    }

    public String getPhone() {
        return phone;
    }

    public int getIsFav() {
        return isFav;
    }
}
