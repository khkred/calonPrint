package sg.com.argus.www.conquestgroup.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sg.com.argus.www.conquestgroup.R;
import sg.com.argus.www.conquestgroup.models.Lot;

public class LotCategoryAdapter extends ArrayAdapter<Lot> {

    List<Lot> lotList;
    public LotCategoryAdapter(@NonNull Context context, @NonNull List<Lot> lots) {
        super(context, 0, lots);
        lotList = new ArrayList<Lot>(lots);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lot_list,parent,false);
        }
        TextView lotIdTV = convertView.findViewById(R.id.lot_id_text_view);
        Lot lotItem = getItem(position);

        if (lotItem != null) {
            lotIdTV.setText(lotItem.getLotId());
        }
        return  convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return  lotFilter;
    }

    private  Filter lotFilter =  new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            List<Lot> suggestions = new ArrayList<>();

            if(charSequence == null|| charSequence.toString().isEmpty()) {
                suggestions.add((Lot) lotList);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Lot lot: lotList){
                    if(lot.getLotId().toLowerCase().contains(filterPattern)){
                        suggestions.add(lot);
                    }
                }
            }
            results.values = suggestions;

            results.count = suggestions.size();

            return  results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            clear();
            addAll((Collection<? extends Lot>) filterResults.values);
            notifyDataSetChanged();

        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return  ((Lot) resultValue).getLotId();
        }
    };
}
