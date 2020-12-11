package com.sabo.sabostorev2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sabo.sabostorev2.Common.Common;
import com.sabo.sabostorev2.Model.ItemStoreModel;
import com.sabo.sabostorev2.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private Context context;
    private List<ItemStoreModel> itemStoreModelList;
    private int lastPosition = -1;

    public CategoriesAdapter(Context context, List<ItemStoreModel> itemStoreModelList) {
        this.context = context;
        this.itemStoreModelList = itemStoreModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_categories, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemStoreModel list = itemStoreModelList.get(position);
        String id = list.getId();
        String itemUrl = Common.ITEMS_URL;
        String resultUrl = "";
        switch (id) {
            case "item-01":
                resultUrl = itemUrl + "item-01/" + list.getImage();
                break;
            case "item-02":
                resultUrl = itemUrl + "item-02/" + list.getImage();
                break;
            case "item-03":
                resultUrl = itemUrl + "item-03/" + list.getImage();
                break;
            case "item-04":
                resultUrl = itemUrl + "item-04/" + list.getImage();
                break;
            case "item-05":
                resultUrl = itemUrl + "item-05/" + list.getImage();
                break;
            case "item-06":
                resultUrl = itemUrl + "item-06/" + list.getImage();
                break;
            case "item-07":
                resultUrl = itemUrl + "item-07/" + list.getImage();
                break;
            case "item-08":
                resultUrl = itemUrl + "item-08/" + list.getImage();
                break;
        }

        Picasso.get().load(resultUrl).into(holder.ivItemImg);
        holder.tvItemName.setText(list.getName());
        holder.tvItemDescription.setText(list.getDescription());

        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_rv_item_right);
            view.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return itemStoreModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImg;
        TextView tvItemName, tvItemDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivItemImg = itemView.findViewById(R.id.ivItemImg);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemDescription = itemView.findViewById(R.id.tvItemDescription);
        }
    }
}
