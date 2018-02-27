package com.example.andrii.flashchat.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrii.flashchat.Activities.AttachmentsActivity;
import com.example.andrii.flashchat.Activities.PhotoPagerActivity;
import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.DB.MessageDb;
import com.example.andrii.flashchat.data.Model.Message;
import com.example.andrii.flashchat.data.Model.MessageItem;
import com.example.andrii.flashchat.tools.ImageTools;
import com.example.andrii.flashchat.tools.QueryPreferences;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyRecycleViewHolder> {
    private static final String TAG = "ChatListAdapter";
    private Context context;
    private List<MessageItem> mMessages;
    private String mMyID;

    public ChatListAdapter(Context con, List<MessageItem> list){
        context = con;
        list.sort((messageItem, t1) -> {
            Date date1 = messageItem.getDate();
            Date date2 = t1.getDate();

            boolean before = date1.before(date2);

            return before?-1:date1.equals(date2)?0:1;
        });
        mMessages = list;
        mMyID = QueryPreferences.getActiveUserId(context);
    }

    public Intent addAttachments(){
        Realm realm = Realm.getDefaultInstance();
        ArrayList<String> list = new ArrayList<>();
        for (MessageItem item:mMessages){
            if (item.getType() == Message.MESSAGE_IMAGE_TYPE){
                MessageDb message = realm.where(MessageDb.class).equalTo("msgID",item.getMsgID()).findFirst();
                if (message != null){
                    list.add(item.getMsgID());
                }
            }
        }
        realm.close();
        return AttachmentsActivity.newIntent(context,list);
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

        private TextView mText;
        private ImageView mIvPhotoMessage;

        MyRecycleViewHolder(View itemView) {
            super(itemView);

            mText = itemView.findViewById(R.id.tv_text);
            mIvPhotoMessage = itemView.findViewById(R.id.iv_photo_message);

            mIvPhotoMessage.setOnClickListener(view -> {
                ArrayList<String> list = new ArrayList<>();
                String msgItemStartId = "";
                Realm realm = Realm.getDefaultInstance();
                for (MessageItem item:mMessages){
                    if (item.getType() == Message.MESSAGE_IMAGE_TYPE) {
                        MessageDb message = realm.where(MessageDb.class).equalTo("msgID",item.getMsgID()).findFirst();
                        if (message != null){
                            list.add(item.getMsgID());
                            if(mMessages.get(getAdapterPosition()).getMsgID().equals(item.getMsgID()))msgItemStartId = item.getMsgID();
                        }

                    }
                }
                int msgPosition = list.indexOf(msgItemStartId);
                realm.close();
                Intent intent = PhotoPagerActivity.newIntent(context,list,msgPosition);
                context.startActivity(intent);
            });
        }


        void bindHolder(MessageItem msg) {
            Log.d("qwe","length:" + getItemCount() +" myId:" + mMyID +  " sederId:" + msg.getSenderId() + " recipientId:" + msg.getRecipient_id());

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
                    int originWidth = origin.getWidth();
                    int originHeight = origin.getHeight();

                    int width = mIvPhotoMessage.getWidth();
                    int height = originHeight*width/originWidth;

                    Bitmap finishBitmap = Bitmap.createScaledBitmap(origin,width,height,true);

                    mIvPhotoMessage.setImageBitmap(ImageTools.roundCorners(finishBitmap));

                } catch (Exception e) {
                    Log.e(TAG,"Error with taking bitmap from uri",e);
                    Picasso.with(context)
                            .load(R.drawable.ic_image_broke)
                            .into(mIvPhotoMessage);

                }
            });

            }
        }




        }
}
