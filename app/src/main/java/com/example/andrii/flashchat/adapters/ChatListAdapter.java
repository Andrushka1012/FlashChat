package com.example.andrii.flashchat.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Message;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyRecycleViewHolder>{
    private static final String TAG = "ChatListAdapter";
    private Context context;
    private List<Message> mMessages;
    private String mMyID;

    public ChatListAdapter(Context con, List<Message> list,String myID){
        context = con;
        mMessages = list;
        mMyID = myID;
    }

    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View v = null;
        switch (viewType){
           case 0:
               v =  LayoutInflater.from(context).inflate(R.layout.mine_first_message_item,parent,false);
               break;
            case 1:
                v =  LayoutInflater.from(context).inflate(R.layout.to_me_first_message_item,parent,false);
                break;
            case 2:
                v =  LayoutInflater.from(context).inflate(R.layout.mine_message_item,parent,false);
                break;
            case 3:
                v =  LayoutInflater.from(context).inflate(R.layout.to_me_message_item,parent,false);
                break;
            default:
                v =  LayoutInflater.from(context).inflate(R.layout.mine_message_item,parent,false);
       }
       return new MyRecycleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyRecycleViewHolder holder, int position) {
        holder.bindHolder(mMessages.get(position));
        Log.d(TAG,String.valueOf(mMessages.get(position).getType()));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void addMessage(Message msg){
        mMessages.add(msg);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            if (mMessages.get(position).getFrom().getId().equals(mMyID)) return 0;
            else return 1;
        }
        if (mMessages.get(position).getFrom().getId().equals(mMyID)){
            if (mMessages.get(position-1).getFrom().getId().equals(mMyID)) return 2;

            else return 0;
        }else {
            if (mMessages.get(position).getFrom().getId().equals(mMessages.get(position-1).getFrom().getId())) return 3;
            else return 1;
        }
    }

    public class MyRecycleViewHolder extends RecyclerView.ViewHolder{

        private ImageView mPhoto;
        private TextView mText;
        private ImageView mIvPhotoMessage;

        public MyRecycleViewHolder(View itemView) {
            super(itemView);

            mPhoto = itemView.findViewById(R.id.iv_photo);
            mText = itemView.findViewById(R.id.tv_text);
            mIvPhotoMessage = itemView.findViewById(R.id.iv_photo_message);
        }


        void bindHolder(Message msg) {
            if (msg.getType() == Message.MESSAGE_TEXT_TYPE) {
                mText.setVisibility(View.VISIBLE);
                mIvPhotoMessage.setVisibility(View.GONE);
                mText.setText(msg.getText());

            }else{
                mText.setVisibility(View.GONE);
                mIvPhotoMessage.setVisibility(View.VISIBLE);

                String path = msg.getImagePath();
                Uri uri = Uri.fromFile(new File(path));
            mIvPhotoMessage.post(() -> {
                try {
                    Bitmap origin = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                    Matrix matrix = new Matrix();

                    int x = origin.getWidth()/mIvPhotoMessage.getWidth();
                    double height = origin.getHeight()/x;
                    int width = mIvPhotoMessage.getWidth();

                    if (origin.getHeight()<origin.getWidth()){
                        matrix.postRotate(-90);
                        height = width;
                        width = (int) (mIvPhotoMessage.getWidth()*1.3);
                    }

                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(origin,width, Math.toIntExact(Math.round(height)),true);

                    Bitmap rotated= Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                    mIvPhotoMessage.setImageBitmap(rotated);
                } catch (IOException e) {
                    Log.e(TAG,"Error with taking bitmap from uri",e);
                }
            });

            }
        }




        }
}
