package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-04-22.
 */

public class LocationListSelected {
    private String id;
    private String address;

    public LocationListSelected(String id, String address) {
        this.id = id;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }
}
