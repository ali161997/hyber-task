package com.example.hyber_task;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hyber_task.adapters.ItemAdapter;
import com.example.hyber_task.view_models.ItemViewModel;

import java.util.ArrayList;

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
            if (position == 2) {
                viewModel.getItemsList().getValue().get(position).setUrl(viewModel.getItemsList().getValue().get(position).getUrl().replace("(", ""));
            }
            viewModel.getItemsList().getValue().get(position).setDownloading(true);

            viewModel.downloadFileFromRepository(position);
            itemAdapter.notifyItemChanged(position);

        });

        recyclerView.setAdapter(itemAdapter);
        viewModel.getItemsList().observe(this, items -> {
            itemAdapter.setList(items);
            itemAdapter.notifyDataSetChanged();
        });

    }


}
