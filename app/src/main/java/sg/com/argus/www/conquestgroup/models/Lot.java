package sg.com.argus.www.conquestgroup.models;

public class Lot {
    private String lotId;

    public Lot(String lotId) {
       setLotId(lotId);
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }
}
