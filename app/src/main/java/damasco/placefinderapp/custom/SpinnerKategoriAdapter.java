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
import damasco.placefinderapp.entity.Kategori;
import damasco.placefinderapp.entity.SubKategori;

/**
 * Created by Habib Mustofa on 12/11/2017.
 */

public class SpinnerKategoriAdapter extends BaseAdapter {

    private List<Kategori> data = new ArrayList<>();
    private Context context;

    public SpinnerKategoriAdapter(@NonNull Context context, @NonNull List<Kategori> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Kategori getItem(int position) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(data.get(position).getName());
        return convertView;
    }

    private class ViewHolder {

        TextView textView;

        ViewHolder(View view) {
            textView = view.findViewById(R.id.spinner_text);
        }
    }
}
