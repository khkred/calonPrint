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
    private  PrintSlipClickListener mPrintSlipListener;

    public PrintSlipAdapter(List<PrintSlip> printSlips, PrintSlipClickListener printSlipClickListener) {
        this.printSlips = printSlips;
        mPrintSlipListener = printSlipClickListener;

    }

    public interface PrintSlipClickListener {
        void onSlipItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView getLotIdTV() {
            return lotIdTV;
        }

        TextView lotIdTV;
        PrintSlipClickListener printSlipClickListener;

        public ViewHolder(View view, PrintSlipClickListener printSlipClickListener) {
            super(view);
            lotIdTV = view.findViewById(R.id.lot_id_text_view);
            view.setOnClickListener(this);
            this.printSlipClickListener = printSlipClickListener;
        }


        @Override
        public void onClick(View view) {
            printSlipClickListener.onSlipItemClick(getAdapterPosition());

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.print_slip_card, viewGroup, false);
        return new ViewHolder(view,mPrintSlipListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.getLotIdTV().setText(printSlips.get(position).lotId);

    }


    @Override
    public int getItemCount() {
        return printSlips.size();
    }


}