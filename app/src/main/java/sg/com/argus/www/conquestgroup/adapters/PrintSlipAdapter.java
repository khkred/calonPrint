package sg.com.argus.www.conquestgroup.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.interfaces.ItemClickListener;
import sg.com.argus.www.conquestgroup.models.PrintSlip;

public class PrintSlipAdapter extends RecyclerView.Adapter<PrintSlipAdapter.ViewHolder> {
    private List<PrintSlip> printSlips;
    private ItemClickListener clickListener;

    public PrintSlipAdapter(List<PrintSlip> printSlips) {
        this.printSlips = printSlips;
           }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView dateTV;
        public ViewHolder(View view) {
            super(view);

            dateTV = view.findViewById(R.id.date_text_view);
        view.setOnClickListener(this);
        }

        public TextView getDateTV() {
            return dateTV;
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
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

    }

    @Override
    public int getItemCount() {
        return printSlips.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }




}
