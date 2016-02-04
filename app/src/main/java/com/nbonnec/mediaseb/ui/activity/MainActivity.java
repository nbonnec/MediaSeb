package com.nbonnec.mediaseb.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nbonnec.mediaseb.MediasebApp;
import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.services.MSSService;
import com.nbonnec.mediaseb.data.services.MSSServiceImpl;
import com.nbonnec.mediaseb.models.MediaList;
import com.nbonnec.mediaseb.network.Finder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private List<MediaList> list_title = new ArrayList<>();

    private static Observable<MediaList> resultsObservable;
    private Subscription resultsSubscription;

    @Inject
    MSSService mssService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inject dependencies
        MediasebApp app = MediasebApp.get(getApplicationContext());
        app.inject(this);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    /*public void loadNews() {
        resultsObservable = mssService
                .getResults("walking")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        resultsSubscription = resultsObservable.subscribe(new Observer<MediaList>() {
            @Override
            public void onCompleted() {
                resultsObservable = null;
            }

            @Override
            public void onError(Throwable e) {
                resultsObservable = null;
            }

            @Override
            public void onNext(MediaList news) {
                Log.d("MEDIASEB", String.format("First title = %s", news.getMedias().get(0).getTitle()));
            }
        });
    }
    */
}
