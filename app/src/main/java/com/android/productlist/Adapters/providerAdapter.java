package com.android.productlist.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.productlist.R;

import java.util.Collections;
import java.util.List;

public class providerAdapter extends RecyclerView.Adapter<providerAdapter.viewholder> {

    List<String> providers;
    List<String> unique_provider;
    int count = 1;

    public providerAdapter(List<String> providers,List<String> unique_provider)
    {
        this.providers = providers;
        this.unique_provider = unique_provider;

    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new viewholder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        if (!providers.isEmpty()) {
            if (unique_provider.size() < providers.size()) {
                count = Collections.frequency(providers, unique_provider.get(position));
                System.out.println(count);
            } else {
                count = 1;
            }
            holder.tv_title.setText(unique_provider.get(position));
            holder.tv_subtitle.setText(String.valueOf(count));
        }
    }

    @Override
    public int getItemCount() {
        return unique_provider.size();

    }
    public class viewholder extends RecyclerView.ViewHolder
    {
        TextView tv_title,tv_subtitle;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_subtitle = itemView.findViewById(R.id.tv_subtitle);
        }
    }
}
