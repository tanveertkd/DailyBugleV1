package com.example.baseplate.dailybugle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<pojo> {

    private static final String LOG_TAG = NewsAdapter.class.getName();
    public NewsAdapter(@NonNull Context context, List<pojo> pojoList) {
        super(context, 0, pojoList);
    }

    private String formatDate(String dateObject) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat simpleDateFormatOut = new SimpleDateFormat("dd/MM h:mm a");
        String formattedDate = null;
        try {
            formattedDate = simpleDateFormatOut.format(simpleDateFormat.parse(dateObject));
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Problem Parsing date", e);
        }
        return formattedDate;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }

        pojo currentNewItem = getItem(position);

        TextView headlineView = (TextView) listItemView.findViewById(R.id.headline);
        headlineView.setText(currentNewItem.getHeadline());

        TextView headerView = (TextView) listItemView.findViewById(R.id.news_header);
        headerView.setText(currentNewItem.getHead());

        TextView authorView = (TextView) listItemView.findViewById(R.id.news_author);
        headerView.setText(currentNewItem.getAuthor());

        TextView sectionView = (TextView) listItemView.findViewById(R.id.news_section);
        sectionView.setText(currentNewItem.getSection());

        TextView dateView = (TextView) listItemView.findViewById(R.id.news_date);
        String formattedDate = formatDate(currentNewItem.getDate());
        dateView.setText(formattedDate);

        return listItemView;
    }

}
