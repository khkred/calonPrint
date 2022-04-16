package sg.com.argus.www.conquestgroup.models;


public class MenuCategories {
    public String getFeeCategoryId() {
        return feeCategoryId;
    }

    public void setFeeCategoryId(String feeCategoryId) {
        this.feeCategoryId = feeCategoryId;
    }

    public String getFeeCategoryName() {
        return feeCategoryName;
    }

    public void setFeeCategoryName(String feeCategoryName) {
        this.feeCategoryName = feeCategoryName;
    }

    private String feeCategoryId;
    private String feeCategoryName;

    public MenuCategories(String feeCategoryId, String feeCategoryName) {
        this.feeCategoryId = feeCategoryId;
        this.feeCategoryName = feeCategoryName;
    }


}
