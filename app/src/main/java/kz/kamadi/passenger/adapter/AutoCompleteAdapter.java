package kz.kamadi.passenger.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import kz.kamadi.passenger.utils.GeoCoder;

/**
 * Created by Madiyar on 04.02.2015.
 */
public class AutoCompleteAdapter extends ArrayAdapter implements Filterable {

    private ArrayList resultList;
    private ArrayList tempList;
    private Context context;

    public AutoCompleteAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Object getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {


            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
//                Log.e("Char",constraint.toString());
                if (constraint != null) {
                    GeoCoder.getLocationsByName(constraint.toString(),context, new GeoCoder.VolleyCallback() {
                        @Override
                        public void onSuccess(Object result) {
                            tempList = (ArrayList) result;
                        }
                    });
                }
                if (tempList == null) {
                    tempList = new ArrayList(0);
                    Log.e("resultList", "NULL");
                }

                filterResults.values = tempList;
                filterResults.count = tempList.size();
                Log.e("tempList", tempList.size() + "");

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                resultList = (ArrayList)results.values;
                Log.e("resultList", resultList.size() + "");
                if (results.count > 0) {
                    notifyDataSetChanged();

                } else {
                    notifyDataSetInvalidated();

                }
            }
        };
    }


}
