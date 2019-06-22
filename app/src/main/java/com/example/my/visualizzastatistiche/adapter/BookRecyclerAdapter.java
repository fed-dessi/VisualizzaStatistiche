package com.example.my.visualizzastatistiche.adapter;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.my.visualizzastatistiche.R;
import com.example.my.visualizzastatistiche.models.Book;
import com.example.my.visualizzastatistiche.util.Utility;

import java.util.ArrayList;

public class BookRecyclerAdapter extends RecyclerView.Adapter<BookRecyclerAdapter.ViewHolder>{
    private static final String TAG = "BookRecyclerAdapter";

    private ArrayList<Book> mBooks;
    private BookRecyclerAdapter.OnBookListener mOnBookListener;

    //Private variables to switch the background color
    private int lastVenditaID = 0;
    private String currentColor = "#4CAF50";

    public BookRecyclerAdapter(ArrayList<Book> mBooks, OnBookListener mOnBookListener) {
        this.mBooks = mBooks;
        this.mOnBookListener = mOnBookListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.book_list_layout, viewGroup, false);
        return new BookRecyclerAdapter.ViewHolder(view, mOnBookListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        //Set the date in a correct format
        try{
            String dataFormattata = Utility.getMonthFromNumber(mBooks.get(i).getDate());
            viewHolder.date.setText(dataFormattata);
        }catch(Exception ex){
            Log.e(TAG, "onBindViewHolder: Exception in onBindViewHolder " + ex.getMessage());
        }
        viewHolder.title.setText(mBooks.get(i).getName());


        //Set the background color based on the sale
        if(mBooks.get(i).getVenditaID() == lastVenditaID){
            viewHolder.title.setBackgroundColor(Color.parseColor(currentColor));
        } else {
            currentColor = colorSwitcher(currentColor);
            lastVenditaID = mBooks.get(i).getVenditaID();
            viewHolder.title.setBackgroundColor(Color.parseColor(currentColor));
        }
        //If the background is red, then make the text white
        if(currentColor.equals("#EC0A0A")){
            viewHolder.title.setTextColor(Color.parseColor("#FFFFFF"));
        }else{
            viewHolder.title.setTextColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title; //add timestamp later;
        TextView date;
        BookRecyclerAdapter.OnBookListener onbookListener;
        public ViewHolder(@NonNull View itemView, BookRecyclerAdapter.OnBookListener onbookListener) {
            super(itemView);
            title = itemView.findViewById(R.id.book_title);
            date = itemView.findViewById(R.id.sale_date);
            this.onbookListener = onbookListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onbookListener.onBookClick(getAdapterPosition());
        }
    }

    public interface OnBookListener{
        void onBookClick(int position);
    }

    private String colorSwitcher(String currentColor){
        String newColor = "#4CAF50";
        if(currentColor.equals("#4CAF50")){
            newColor = "#EC0A0A";
            return newColor;
        }
        return newColor;
    }
}
