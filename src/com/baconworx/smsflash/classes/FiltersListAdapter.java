package com.baconworx.smsflash.classes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.baconworx.smsflash.R;

import java.util.List;

public class FiltersListAdapter extends ArrayAdapter<FiltersListItem> {
    private final Context context;
    private final List<FiltersListItem> values;

    public FiltersListAdapter(Context context, List<FiltersListItem> values) {
        super(context, android.R.layout.simple_list_item_1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolderItem();

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.filterrow, parent, false);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.label);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.id = values.get(position).getId();
            viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.filterRow);

            convertView.setTag(viewHolder);
            convertView.setId(viewHolder.id);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        viewHolder.textView.setText(values.get(position).getText());

        if (values.get(position).isGroup()) {
            viewHolder.imageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageView.setVisibility(View.INVISIBLE);
        }

        if (values.get(position).isSelected()) {
            viewHolder.layout.setBackgroundColor(Color.GRAY);
        } else {
            viewHolder.layout.setBackgroundColor(Color.TRANSPARENT);
        }


        return convertView;
    }
    public void clearSelection() {
        for (FiltersListItem item : values) item.setSelected(false);
        notifyDataSetChanged();
    }

    public static class ViewHolderItem {
        ImageView imageView;
        int id;
        private TextView textView;
        private LinearLayout layout;
        public TextView getTextView() {
            return textView;
        }
        public void setTextView(TextView textView) {
            this.textView = textView;
        }
        public ImageView getImageView() {
            return imageView;
        }
        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public View getLayout() { return layout; }
        public void setLayout(LinearLayout layout) { this.layout = layout; }
    }
}
