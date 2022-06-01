package sg.com.argus.www.conquestgroup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.activities.BluetoothActivity;
import sg.com.argus.www.conquestgroup.models.Bag;

public class BagViewAdapter extends RecyclerView.Adapter<BagViewAdapter.BagViewHolder> {

    Context context;
    ArrayList<Bag> bagArrayList;

    // Constructor for the BagViewAdapter

    public BagViewAdapter(Context context, ArrayList<Bag> bagArrayList) {

        this.context = context;
        this.bagArrayList = bagArrayList;
    }

    View view;

    @NonNull
    @Override
    public BagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.bag_list_item,parent,false);

        BagViewHolder bagViewHolder = new BagViewHolder(view);
        return bagViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BagViewHolder holder, int position) {

        final Bag bag = bagArrayList.get(position);

        if (bag.getBagCountLabel()  != null) {
            holder.bagLabelTV.setText(bag.getBagCountLabel());
            holder.bagWeightTV.setText(bag.getBagWeight());
        }
        holder.removeBtn.setOnClickListener(view1 -> {

            BluetoothActivity.decreaseBagCount(position, context);
            bagArrayList.remove(position);
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return bagArrayList.size();
    }

    public static class BagViewHolder  extends RecyclerView.ViewHolder{

        TextView bagLabelTV;
        TextView bagWeightTV;
        Button removeBtn;

        public BagViewHolder(View itemView) {
            super(itemView);
            bagLabelTV = itemView.findViewById(R.id.bag_label_text_view);
            bagWeightTV = itemView.findViewById(R.id.bag_weight_text_view);
            removeBtn = itemView.findViewById(R.id.remove_btn);

        }



    }
}

