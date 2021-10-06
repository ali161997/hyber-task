package com.example.hyber_task.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hyber_task.R;
import com.example.hyber_task.interfaces.OnItemClickListner;
import com.example.hyber_task.models.Item;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ItemAdapter";
    private final Context _context;
    private final OnItemClickListner listener;
    private ArrayList<Item> dataSet;


    public ItemAdapter(ArrayList<Item> items, Context context, OnItemClickListner listener) {
        this._context = context;
        this.listener = listener;
        dataSet = items;
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.textView.setText(dataSet.get(position).getName());
        if (dataSet.get(position).isDownloaded()) {
            itemViewHolder.downloadedTv.setVisibility(View.VISIBLE);
            itemViewHolder.progressIndicator.setVisibility(View.INVISIBLE);
            itemViewHolder.download.setEnabled(false);
        }
        if (dataSet.get((position)).isDownloading()) {
            itemViewHolder.download.setEnabled(false);
            itemViewHolder.progressIndicator.setVisibility(View.VISIBLE);
            itemViewHolder.progressIndicator.setProgress(dataSet.get(position).getDownloadPercentage());
            itemViewHolder.progressIndicator.setContentDescription(Integer.toString(dataSet.get(position).getDownloadPercentage()));
            int progress = dataSet.get(position).getDownloadPercentage();
            itemViewHolder.progressIndicator.setProgressCompat(progress, true);
            //itemViewHolder.progressIndicator.setProgressCompat(dataSet.get(position).getDownloadPercentage(), true);
        }

        itemViewHolder.download.setOnClickListener(view -> {
            listener.onItemClick(position);
        });
    }

    public void setList(ArrayList<Item> items) {
        this.dataSet = items;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView textView;
        MaterialButton download;
        CircularProgressIndicator progressIndicator;
        MaterialTextView downloadedTv;


        public ItemViewHolder(View itemView) {
            super(itemView);
            this.downloadedTv = itemView.findViewById(R.id.downloadeTV);
            this.download = itemView.findViewById(R.id.download);
            this.textView = itemView.findViewById(R.id.name);
            this.progressIndicator = itemView.findViewById(R.id.progress_indicator);
        }

    }
}
