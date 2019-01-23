package damasco.placefinderapp.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import damasco.placefinderapp.R;
import damasco.placefinderapp.entity.SubKategori;

/**
 * Created by Habib Mustofa on 11/11/2017.
 */

public class ListSubKategoriAdapter extends BaseAdapter {

    private final Context context;
    private final List<SubKategori> data;

    public ListSubKategoriAdapter(Context context, @NonNull List<SubKategori> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public SubKategori getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mTitle.setText(data.get(position).getName());
        holder.mDesctiption.setText(data.get(position).getKategori());
        return convertView;
    }

    private class ViewHolder {

        TextView mTitle, mDesctiption;

        ViewHolder(View view) {
            this.mTitle = view.findViewById(R.id.list_item_title);
            this.mDesctiption = view.findViewById(R.id.list_item_description);
        }
    }
}
