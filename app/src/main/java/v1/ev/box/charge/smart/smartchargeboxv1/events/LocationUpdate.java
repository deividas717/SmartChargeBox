package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-03-25.
 */

public class LocationUpdate {
    public double lat;
    public double lng;
    public LocationUpdate(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
