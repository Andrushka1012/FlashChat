package com.example.andrii.flashchat.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Message;
import com.example.andrii.flashchat.data.MessageItem;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.tools.ImageTools;
import com.example.andrii.flashchat.tools.QueryPreferences;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyRecycleViewHolder> {
    private static final String TAG = "ChatListAdapter";
    private Context context;
    private List<MessageItem> mMessages;
    private String mMyID;

    public ChatListAdapter(Context con, List<MessageItem> list){
        context = con;
        mMessages = list;
        mMyID = QueryPreferences.getActiveUserId(context);
    }

    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View v;
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

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            if (mMessages.get(position).getSenderId().equals(mMyID)) return 0;
            else return 1;
        }
        if (mMessages.get(position).getSenderId().equals(mMyID)){
            if (mMessages.get(position-1).getSenderId().equals(mMyID)) return 2;

            else return 0;
        }else {
            if (mMessages.get(position).getSenderId().equals(mMessages.get(position-1).getSenderId())) return 3;
            else return 1;
        }
    }

    public void addMessage(MessageItem msg){
        mMessages.add(msg);
        notifyDataSetChanged();


    }

    class MyRecycleViewHolder extends RecyclerView.ViewHolder{

        private FrameLayout mFrameLayout;
        private ImageView mPhoto;
        private TextView mText;
        private ImageView mIvPhotoMessage;

        MyRecycleViewHolder(View itemView) {
            super(itemView);

            mFrameLayout = itemView.findViewById(R.id.framelayout);
            mPhoto = itemView.findViewById(R.id.iv_photo);
            mText = itemView.findViewById(R.id.tv_text);
            mIvPhotoMessage = itemView.findViewById(R.id.iv_photo_message);
        }


        void bindHolder(MessageItem msg) {
            Log.d("qwe","length:" + getItemCount() +" myId:" + mMyID +  " sederId:" + msg.getSenderId() + " recipientId:" + msg.getRecipient_id());
            ImageTools tools = new ImageTools(context);
            Person p = new Person(msg.getSenderId(),"");

            if ((getItemViewType() == 0 || getItemViewType() == 2) && msg.getRead() == 0) mFrameLayout.setBackground(context.getResources().getDrawable(R.color.grey));

            if (getItemViewType() == 0 || getItemViewType() == 1)  tools.downloadPersonImage(mPhoto,p);

            if (msg.getType() == Message.MESSAGE_TEXT_TYPE) {
                mText.setVisibility(View.VISIBLE);
                mIvPhotoMessage.setVisibility(View.GONE);
                mText.setText(msg.getText());

            }else{
                mText.setVisibility(View.GONE);
                mIvPhotoMessage.setVisibility(View.VISIBLE);
                String root = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
                File file = new File(root,msg.getMsgID() + ".jpg");

                Uri uri = Uri.fromFile(file);
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
