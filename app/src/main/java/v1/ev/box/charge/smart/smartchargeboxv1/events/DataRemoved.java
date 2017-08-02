package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-03-25.
 */

public class DataRemoved {
    public String collectionName;
    public String documentID;

    public DataRemoved(String collectionName, String documentID) {
        this.collectionName = collectionName;
        this.documentID = documentID;
    }
}
