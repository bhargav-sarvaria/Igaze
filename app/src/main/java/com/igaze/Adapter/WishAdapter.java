package com.igaze.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowId;
import android.widget.ImageView;

import com.igaze.Models.Wish;
import com.igaze.R;
import com.igaze.Utilities.CustomBoldTextView;
import com.igaze.Utilities.CustomTextView;

import java.util.ArrayList;
import java.util.List;

public class WishAdapter extends RecyclerView.Adapter<WishAdapter.cardViewHolder> {
    public Context context;
    public List<Wish> cardList;

    public WishAdapter(Context context, List<Wish> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @Override
    public cardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_wish, null);
        return new cardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final cardViewHolder holder, int position) {
        Wish card = cardList.get(position);

        holder.wishTitle.setText(card.getWishPrefix());
        holder.wish.setText(card.getWish());
        holder.targetDate.setText("Target Date: "+card.getTargetDate());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public void setFilter(List<Wish> list) {
        cardList = new ArrayList<>();
        cardList.addAll(list);
        notifyDataSetChanged();
    }

    public interface ClickListener {
        void onClick(View view, Wish productCard);
    }

    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class cardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CustomTextView wish,targetDate;
        CustomBoldTextView wishTitle;
        ImageView edit,delete;

        public cardViewHolder(View itemView) {
            super(itemView);

            wishTitle = itemView.findViewById(R.id.rowWishTitle);
            wish = itemView.findViewById(R.id.rowWish);
            targetDate = itemView.findViewById(R.id.rowTargetDate);

            edit = itemView.findViewById(R.id.wishEditIcon);
            delete = itemView.findViewById(R.id.wishDeleteIcon);

            edit.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, cardList.get(getAdapterPosition()));
        }
    }
}

