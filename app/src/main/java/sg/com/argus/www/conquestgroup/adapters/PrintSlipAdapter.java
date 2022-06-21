package sg.com.argus.www.conquestgroup.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.models.PrintSlip;

public class PrintSlipAdapter extends RecyclerView.Adapter<PrintSlipAdapter.ViewHolder> {
    private final List<PrintSlip> printSlips;
    private ItemClickListener itemClickListener;

    public PrintSlipAdapter(List<PrintSlip> printSlips, ItemClickListener listener) {
        this.printSlips = printSlips;
        itemClickListener = listener;
        }

        public  interface  ItemClickListener{
        void onItemClick(PrintSlip printSlip);
        }
    public  class ViewHolder extends RecyclerView.ViewHolder{

        TextView dateTV;
        public ViewHolder(View view) {
            super(view);
            dateTV = view.findViewById(R.id.date_text_view);
        }

        public TextView getDateTV() {
            return dateTV;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.print_slip_card,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.getDateTV().setText(printSlips.get(position).formatted_Date);

        //Set on click listener to cardview
        holder.itemView.setOnClickListener(view -> {
            itemClickListener.onItemClick(printSlips.get(position));
        });
    }



    @Override
    public int getItemCount() {
        return printSlips.size();
    }





}