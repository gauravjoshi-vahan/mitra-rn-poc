package com.vahan.mitra_playstore.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.PictureDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestBuilder;
import com.moengage.inbox.core.model.InboxMessage;
import com.scottyab.rootbeer.Const;
import com.vahan.mitra_playstore.R;
import com.vahan.mitra_playstore.utils.Constants;
import com.vahan.mitra_playstore.view.MainActivity;


import org.json.JSONException;

import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.MyViewHolder> {
    private Context activity;
    private List<InboxMessage> roughDemos;
    private RequestBuilder<PictureDrawable> requestBuilder;
    private int count;


    public NotificationListAdapter(Context notificationActivity, List<InboxMessage> roughDemos, int count) {
        this.activity = notificationActivity;
        this.roughDemos = roughDemos;
        this.count = count;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            holder.tvNotificationTitle.setText(roughDemos.get(position).getTextContent().getTitle());
            holder.tvNotificationBody.setText(roughDemos.get(position).getTextContent().getMessage());
            holder.relativeLayoutContainer.setOnClickListener((view) -> {
                try {
                    redirectionLogic(roughDemos.get(position).getPayload().getString(Constants.CLICK_ACTION));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }catch (Exception e){

        }

        if (count == 1) {
            if (position == 0) {
                holder.conMain.setBackgroundColor(activity.getColor(R.color.unread_notification_background));
                holder.imgRead.setVisibility(View.VISIBLE);
            }
        }
        holder.tvNotificationBody.setTypeface(null, Typeface.BOLD);
        holder.tvNotificationTitle.setTypeface(null, Typeface.BOLD);
    }

    private void redirectionLogic(String type) {
        if (type != null) {
            if (type.equalsIgnoreCase(Constants.HOME)) {
                activity.startActivity(new Intent(activity, MainActivity.class)
                        .putExtra(Constants.TYPE, type));
            } else if (type.equalsIgnoreCase(Constants.PROFILE)) {
                //   activity.startActivity(new Intent(activity, ProfileActivity.class));
            } else if (type.equalsIgnoreCase(Constants.BORROW)) {
                activity.startActivity(new Intent(activity, MainActivity.class)
                        .putExtra(Constants.TYPE, type));
            } else if (type.equalsIgnoreCase(Constants.INSURE)) {
                activity.startActivity(new Intent(activity, MainActivity.class)
                        .putExtra(Constants.TYPE, type));
            } else if (type.equals(Constants.BANK_ACCOUNT)) {
                //  activity.startActivity(new Intent(activity, AccountActivity.class));
            } else if (type.equals(Constants.DOCUMENT)) {
                //  activity.startActivity(new Intent(activity, UploadActivity.class));
            }

        }
    }

    @Override
    public int getItemCount() {
        return roughDemos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNotificationTitle, tvNotificationBody;
        private RelativeLayout relativeLayoutContainer;
        private ConstraintLayout conMain;
        private ImageView ivLogo, imgRead;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotificationTitle = itemView.findViewById(R.id.txt_title);
            tvNotificationBody = itemView.findViewById(R.id.txt_date);
            relativeLayoutContainer = itemView.findViewById(R.id.container);
            ivLogo = itemView.findViewById(R.id.img_logo);
            conMain = itemView.findViewById(R.id.con_main);
            imgRead = itemView.findViewById(R.id.img_read);
        }
    }
}
