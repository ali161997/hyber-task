package com.example.hyber_task;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hyber_task.adapters.ItemAdapter;
import com.example.hyber_task.view_models.ItemViewModel;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private ItemViewModel viewModel;
    private ItemAdapter itemAdapter;
    private CircularProgressIndicator progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view);
        progressIndicator=findViewById(R.id.progress_main);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.setContext(this);
        if(!viewModel.isNetworkConnected()){
            progressIndicator.setVisibility(View.GONE);
            Toast.makeText(this,"No Internet Available",Toast.LENGTH_LONG).show();
        }
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
            progressIndicator.setVisibility(View.GONE);
            itemAdapter.notifyDataSetChanged();
        });

    }


}
