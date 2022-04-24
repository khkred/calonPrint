package sg.com.argus.www.conquestgroup.models;

public class Bag {
    final String bagCountLabel;
    final String bagWeight;

    public String getBagCountLabel() {
        return bagCountLabel;
    }

    public String getBagWeight() {
        return bagWeight;
    }

    public Bag(String bagCountLabel, String bagWeight) {
        this.bagCountLabel = bagCountLabel;
        this.bagWeight = bagWeight;
    }
}
