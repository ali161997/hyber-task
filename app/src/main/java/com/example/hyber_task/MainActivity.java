package com.example.hyber_task;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hyber_task.adapters.ItemAdapter;
import com.example.hyber_task.interfaces.OnItemClickListner;
import com.example.hyber_task.models.Item;
import com.example.hyber_task.view_models.ItemViewModel;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private ItemViewModel viewModel;
    private ItemAdapter itemAdapter;
    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            for (int i = 0; viewModel.getItemsList().getValue().size() > i; i++) {
                try {
                    if (viewModel.getItemsList().getValue().get(i).getDownloadID() == id) {
                        Log.i(TAG, "onReceive: " + i + " " + id);
                        viewModel.getItemsList().getValue().get(i).setDownloaded(true);
                        viewModel.getItemsList().getValue().get(i).setDownloading(false);
                        itemAdapter.notifyItemChanged(i);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onReceive: " + e.getMessage());
                }
            }
        }
    };
    private DownloadManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.setContext(this);
        viewModel.getItemsFromRepository();
        itemAdapter = new ItemAdapter(new ArrayList<>(), this, position -> {
            Log.i(TAG, "onItemClick: position Clicked :" + position);
            if (position == 2) {
                viewModel.getItemsList().getValue().get(position).setUrl(viewModel.getItemsList().getValue().get(position).getUrl().replace("(", ""));
            }
            viewModel.getItemsList().getValue().get(position).setDownloading(true);
            
            viewModel.downloadFileFromRepository(position);
            itemAdapter.notifyItemChanged(position);

//            try {
//
//                manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//                DownloadManager.Request request = new DownloadManager.Request(url);
//                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
//                request.setVisibleInDownloadsUi(true);
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
//                long reference = manager.enqueue(request);
//                getPrecentage(reference, position);
//                Log.i(TAG, "onItemClick: id start" + reference);
//                viewModel.getItemsList().getValue().get(position).setDownloadID(reference);
//
//            } catch (Exception e) {
//                Log.i(TAG, "onBindViewHolder: " + e.getMessage());
//            }

        });

        recyclerView.setAdapter(itemAdapter);
        viewModel.getItemsList().observe(this, items -> {
            itemAdapter.setList(items);
            itemAdapter.notifyDataSetChanged();
        });

    }

    private void getPrecentage(Long id, int position) {
        final double[] progress = {0.0};
       Timer timer= new Timer();
       timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(id);

                Cursor c = manager.query(query);
                if (c.moveToFirst()) {
                    int sizeIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                    int downloadedIndex = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                    long size = c.getInt(sizeIndex);
                    long downloaded = c.getInt(downloadedIndex);
                    if (size != -1) progress[0] = downloaded * 100.0 / size;
                    viewModel.getItemsList().getValue().get(position).setDownloadPercentage((int) progress[0]);
                    if (progress[0] ==100){
                        timer.cancel();

                    }


                }
            }
        }, 0, 100);//put here time 1000 milliseconds=1 second

        // At this point you have the progress as a percentage.
    }
}
