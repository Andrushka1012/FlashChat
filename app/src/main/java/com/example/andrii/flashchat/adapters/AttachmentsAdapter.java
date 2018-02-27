package com.example.andrii.flashchat.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.andrii.flashchat.Activities.PhotoPagerActivity;
import com.example.andrii.flashchat.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class AttachmentsAdapter extends RecyclerView.Adapter<AttachmentsAdapter.MyHolder>{
    private Context context;
    private ArrayList<String> photosId;

    public AttachmentsAdapter(Context context, ArrayList<String> photos) {
        this.context = context;
        this.photosId = photos;
        photos.sort((messageItem, t1) -> {
            long lSize,rSize;
            String root = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
            File file = new File(root,messageItem + ".jpg");
            if (file.exists()){
                lSize = file.length();
                file = new File(root,t1 + ".jpg");
                if (file.exists()){
                    rSize = file.length();
                }else return 0;
            }else return 0;

            return lSize<rSize?1:-1;
        });
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);

        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500));
        return new MyHolder(imageView);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.bindHolder(photosId.get(position));
    }

    @Override
    public int getItemCount() {
        return photosId.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public MyHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
            imageView.setOnClickListener((view)->{
                Intent intent = PhotoPagerActivity.newIntent(context,photosId,getAdapterPosition());
                context.startActivity(intent);
            });
        }

        public void bindHolder(String msgId){
            String root = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
            File file = new File(root,msgId + ".jpg");
            imageView.post(()-> Picasso.with(context).load(file)
                    .error(R.drawable.ic_image_broke)
                    .resize(imageView.getWidth(),imageView.getHeight())
                    .into(imageView));
        }
    }
}
