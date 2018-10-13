package com.example.baseplate.dailybugle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<pojo>>,SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String LOG_TAG = MainActivity.class.getName();
    public static final String API_REQUEST_URL = "https://content.guardianapis.com/search?api-key=150b0c4d-fcad-470a-a579-0424d80c045a&show-tags=contributor&show-fields=headline";
    public static final int LOADER_ID = 1;

    private NewsAdapter mAdapter;
    private TextView mEmptyStateTextView;
    SwipeRefreshLayout mSwipeRefresh;
    private ArrayList<pojo> newsArray= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView newsItemListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsItemListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new NewsAdapter(this, new ArrayList<pojo>());
        newsItemListView.setAdapter(mAdapter);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        newsItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pojo currentNews = mAdapter.getItem(i);
                Uri newsUri = Uri.parse(currentNews.getURL());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(webIntent);
            }
        });

        mSwipeRefresh = findViewById(R.id.swipe);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsArray.clear();
                mAdapter.notifyDataSetChanged();
                getSupportLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
                mSwipeRefresh.setRefreshing(true);
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            ProgressBar loadingView = findViewById(R.id.loading_indicator);
            loadingView.setVisibility(View.GONE);
            mEmptyStateTextView.setText(getText(R.string.no_conn));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.e(LOG_TAG, "OnSharedPrefChanged called");
        if(s.equals(getString(R.string.order_key))||s.equals(getString(R.string.articles_key))){
            mAdapter.clear();
            mEmptyStateTextView.setVisibility(View.GONE);
            View progressBar = findViewById(R.id.loading_indicator);
            progressBar.setVisibility(View.VISIBLE);
            getSupportLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
        }
    }

    @NonNull
    @Override
    public Loader<List<pojo>> onCreateLoader(int id, @Nullable Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPreferences.getString(
                getString(R.string.order_key),
                getString(R.string.order_default)
        );

        String minArticlesToShow = sharedPreferences.getString(
                getString(R.string.articles_key),
                getString(R.string.default_articles)
        );

        Uri baseUri = Uri.parse(API_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("page-size", minArticlesToShow);

        return new NewsLoaders(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<pojo>> loader, List<pojo> data) {
        mEmptyStateTextView.setText(getText(R.string.no_news));
        mSwipeRefresh.setRefreshing(false);
        ProgressBar loadingView = findViewById(R.id.loading_indicator);
        loadingView.setVisibility(View.GONE);

        mAdapter.clear();

        if(data != null && !data.isEmpty()){
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<pojo>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.settings){
            Intent settingsIntent = new Intent(this, Settings.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
