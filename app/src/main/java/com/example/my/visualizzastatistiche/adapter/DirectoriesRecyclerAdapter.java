package com.example.my.visualizzastatistiche.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.my.visualizzastatistiche.R;
import com.example.my.visualizzastatistiche.models.Directory;

import java.util.ArrayList;

public class DirectoriesRecyclerAdapter extends RecyclerView.Adapter<DirectoriesRecyclerAdapter.ViewHolder> {
    private ArrayList<Directory> mDirectories;
    private OnDirectoryListener mOnDirectoryListener;


    public DirectoriesRecyclerAdapter(ArrayList<Directory> directories, OnDirectoryListener onDirectoryListener){
        this.mDirectories = directories;
        this.mOnDirectoryListener = onDirectoryListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.directories_list_layout, viewGroup, false);
        return new ViewHolder(view, mOnDirectoryListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.title.setText(mDirectories.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return mDirectories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title; //add timestamp later;
        OnDirectoryListener ondirectoryListener;
        public ViewHolder(@NonNull View itemView, OnDirectoryListener ondirectoryListener) {
            super(itemView);
            title = itemView.findViewById(R.id.directory_title);
            this.ondirectoryListener = ondirectoryListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ondirectoryListener.onDirectoryClick(getAdapterPosition());
        }
    }

    public interface OnDirectoryListener{
        void onDirectoryClick(int position);
    }
}
