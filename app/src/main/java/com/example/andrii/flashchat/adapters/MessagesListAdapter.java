package com.example.andrii.flashchat.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrii.flashchat.Activities.CorrespondenceListActivity;
import com.example.andrii.flashchat.Activities.ProfileActivity;
import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.DB.UserNamesBd;
import com.example.andrii.flashchat.data.MessagePersonItem;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.actions.ActionGetPersonData;
import com.example.andrii.flashchat.tools.ImageTools;
import com.example.andrii.flashchat.tools.QueryAction;
import com.example.andrii.flashchat.tools.QueryPreferences;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.Observer;

import static android.widget.Toast.LENGTH_LONG;

public class MessagesListAdapter extends RecyclerView.Adapter<MessagesListAdapter.MyRecycleViewHolder> {
    private Context context;
    private List<MessagePersonItem> mItems;
    private RecyclerView mHorizontalRecyclerView;
    private List<Person> mOnlineList;

    public MessagesListAdapter(Context con, List<MessagePersonItem> itemsList, List<Person> onlineList){
        context = con;
        mItems = itemsList;
        mOnlineList = onlineList;
    }

    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0){
            View v = LayoutInflater.from(context).inflate(R.layout.recycle_view_file,parent,false);
            return new MyRecycleViewHolder(v);
        }
        else{
            View v = LayoutInflater.from(context).inflate(R.layout.messages_list_item,parent,false);
            return new MyRecycleViewHolder(v);
        }
    }

    public void setContent(List<MessagePersonItem> items,List<Person> onlineList){
        mItems = items;
        mOnlineList = onlineList;
    }

    @Override
    public void onBindViewHolder(MyRecycleViewHolder holder, int position) {
        if (position == 0) return;
        holder.bindHolder(mItems.get(position-1));
    }

    @Override
    public int getItemCount() {
        return mItems.size()+1;
    }

    @Override
    public int getItemViewType(int position) {

        if(position == 0) return 0;
        else
            return 1;
    }
    
    public void setHorizontalRecycleViewList(List<Person> personList){
        mOnlineList = personList;
        if (mHorizontalRecyclerView != null){
            mHorizontalRecyclerView.setAdapter(new HorizontalRecycleViewAdapter(context,mOnlineList));
            mHorizontalRecyclerView.getAdapter().notifyDataSetChanged();
        }


    }


    class MyRecycleViewHolder extends RecyclerView.ViewHolder{
        private ImageView mOnline;
        private ImageView mPhoto;
        private TextView mName;
        private TextView mLastMessage;
        private ImageView mRead;
        private TextView mDate;

        MyRecycleViewHolder(View itemView) {
            super(itemView);
            mHorizontalRecyclerView = itemView.findViewById(R.id.recycler_view_horizontal);
            if (mHorizontalRecyclerView != null){
                mHorizontalRecyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
                mHorizontalRecyclerView.setAdapter(new HorizontalRecycleViewAdapter(context,mOnlineList));
                return;
            }
            mOnline = itemView.findViewById(R.id.iv_online);
            mPhoto = itemView.findViewById(R.id.iv_photo);
            mName = itemView.findViewById(R.id.tv_name);
            mLastMessage = itemView.findViewById(R.id.tv_last_message);
            mRead = itemView.findViewById(R.id.iv_read);
            mDate = itemView.findViewById(R.id.tv_date);


            itemView.setOnClickListener(v -> {
                String id = mItems.get(getAdapterPosition() - 1).getSenderId();
                Realm realm = Realm.getDefaultInstance();
                UserNamesBd unbd = realm.where(UserNamesBd.class)
                        .equalTo("userId",id)
                        .findFirst();
                String name = unbd == null?null:unbd.getName();

                Person person = new Person(id,name);
                Intent intent = CorrespondenceListActivity.newIntent(context, person,mOnline.getVisibility() == View.VISIBLE);
                context.startActivity(intent);
            });

            itemView.setOnLongClickListener(view ->{
                PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                popupMenu.inflate(R.menu.profile_context_menu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.action_profile:
                            ProfileActivity.startActivity(context,mItems.get(getAdapterPosition()-1).getSenderId());
                            return true;
                        default:
                            return false;
                    }

                });
                popupMenu.show();
                return true;
            });


        }


        void bindHolder(MessagePersonItem item) {
            ImageTools tools = new ImageTools(context);
            String id = item.getSenderId();
            if (id.equals(QueryPreferences.getActiveUserId(context))) id = item.getRecipient_id();

            Person person = new Person(id,item.getName());
            tools.downloadPersonImage(mPhoto,person,true);


            mName.setText(item.getName() == null?"Error with getting name":item.getName());

            mLastMessage.setText(item.getText().length() <= 30 ?item.getText():"Message.");

            if (item.getSenderId().equals(QueryPreferences.getActiveUserId(context))){
                if (item.getRead() == 1){
                    tools.downloadPersonImage(mRead,new Person(item.getSenderId(),""),true);
                    mRead.setVisibility(View.VISIBLE);
                }else mRead.setVisibility(View.GONE);
            }else mRead.setVisibility(View.GONE);

            mDate.setText(item.getDate().toString().substring(0,16));
            boolean online = false;
            for (Person p:mOnlineList){
                if (p.getId().equals(item.getSenderId())) online = true;
            }
            mOnline.setVisibility(online?View.VISIBLE:View.GONE);


        }
    }


}
