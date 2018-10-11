package com.example.baseplate.dailybugle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

public class NewsLoaders extends AsyncTaskLoader<List<pojo>> {
    public static final String LOG_TAG = NewsLoaders.class.getName();
    private String mUrl;

    public NewsLoaders(@NonNull Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<pojo> loadInBackground() {
        if(mUrl == null) {
            return null;
        }

        List<pojo> pojos = QueryUtils.fetchNewsData(mUrl);
        return pojos;
    }
}
