package sg.com.argus.www.conquestgroup.models;

public class TableItem {
    private String[] text;
    private int[] width;
    private int[] align;

    public TableItem() {
        text = new String[]{"test","test"};
        width = new int[]{1,1};
        align = new int[]{0,2};
    }

    public TableItem(String[] text){
        this.text = text;
        align = new int[]{0,2};
    }


    public String[] getText() {
        return text;
    }

    public void setText(String[] text) {
        this.text = text;
    }

    public int[] getWidth() {
        return width;
    }

    public void setWidth(int[] width) {
        this.width = width;
    }

    public int[] getAlign() {
        return align;
    }

    public void setAlign(int[] align) {
        this.align = align;
    }

}
