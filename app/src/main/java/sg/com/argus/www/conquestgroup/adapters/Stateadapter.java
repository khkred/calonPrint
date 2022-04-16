package sg.com.argus.www.conquestgroup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.models.Client;

public class Stateadapter extends BaseAdapter {

    private ArrayList<Client> list;
    private Context context;
    private LayoutInflater infalInflater;
    private TextView txt_branchname;

    public Stateadapter(Context activity, ArrayList<Client> wedservicelist) {
        // TODO Auto-generated constructor stub
        this.list = wedservicelist;
        this.context = activity;
        infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = infalInflater.inflate(R.layout.item_religion, null, true);
        txt_branchname = (TextView) convertView.findViewById(R.id.txt_branchname);
        final Client ch = (Client) getItem(position);
        txt_branchname.setText(ch.getStateDescEn());
        return convertView;
    }
}
