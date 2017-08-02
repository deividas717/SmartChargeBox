package v1.ev.box.charge.smart.smartchargeboxv1.events.array_list_events;

import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.data_models.ReservationCollapsesDataModel;

/**
 * Created by Deividas on 2017-04-27.
 */

public class CollapseEvent {
    private ArrayList<ReservationCollapsesDataModel> list;
    private String detailProblem;
    private String activeId;

    public String getActiveId() {
        return activeId;
    }

    public void setActiveId(String activeId) {
        this.activeId = activeId;
    }

    public void setList(ArrayList<ReservationCollapsesDataModel> list) {
        this.list = list;
    }

    public ArrayList<ReservationCollapsesDataModel> getList() {
        return list;
    }

    public String getDetailProblem() {
        return detailProblem;
    }

    public void setDetailProblem(String detailProblem) {
        this.detailProblem = detailProblem;
    }
}
