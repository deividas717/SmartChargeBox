package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-05-01.
 */

public class ChargingFromFragmentEvent {
    private String id;

    public ChargingFromFragmentEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
