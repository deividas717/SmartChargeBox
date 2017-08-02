package v1.ev.box.charge.smart.smartchargeboxv1.data_models;

/**
 * Created by Deividas on 2017-04-08.
 */

public class StationDataModel {
    private String id;
    private String kilowatts;
    private String name;
    private String manufacturer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKilowatts() {
        return kilowatts;
    }

    public void setKilowatts(String kilowatts) {
        this.kilowatts = kilowatts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}
