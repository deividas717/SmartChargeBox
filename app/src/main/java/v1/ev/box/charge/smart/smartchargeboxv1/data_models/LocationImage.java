package v1.ev.box.charge.smart.smartchargeboxv1.data_models;

/**
 * Created by Deividas on 2017-05-07.
 */

public class LocationImage {
    private String base64Str;

    public LocationImage(String base64Str) {
        this.base64Str = base64Str;
    }

    public String getBase64Str() {
        return base64Str;
    }
}
