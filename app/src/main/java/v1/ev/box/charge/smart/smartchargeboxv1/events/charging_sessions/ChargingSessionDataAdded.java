package v1.ev.box.charge.smart.smartchargeboxv1.events.charging_sessions;

/**
 * Created by Deividas on 2017-04-30.
 */

public class ChargingSessionDataAdded {
    public String collectionName;
    public String documentID;
    public String newValuesJson;

    public ChargingSessionDataAdded(String collectionName, String documentID, String newValuesJson) {
        this.collectionName = collectionName;
        this.documentID = documentID;
        this.newValuesJson = newValuesJson;
    }
}
