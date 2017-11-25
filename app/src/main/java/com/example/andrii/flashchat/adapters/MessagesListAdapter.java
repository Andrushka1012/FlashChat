package com.example.andrii.flashchat.adapters;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.TestData;

import java.util.List;

public class MessagesListAdapter extends RecyclerView.Adapter<MessagesListAdapter.MyRecycleViewHolder> {
    private Context context;
    private List<Person> mPersons;

    public MessagesListAdapter(Context con, List<Person> list){
        context = con;
        mPersons = list;
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

    @Override
    public void onBindViewHolder(MyRecycleViewHolder holder, int position) {
        if (position == 0) return;
        holder.bindHolder(mPersons.get(position-1));
    }

    @Override
    public int getItemCount() {
        return mPersons.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) return 0;
        else
            return 1;
    }

    public class MyRecycleViewHolder extends RecyclerView.ViewHolder{

        private ImageView mOnline;
        private ImageView mPhoto;
        private TextView mName;
        private TextView mLastMessage;
        private ImageView mRead;
        private TextView mDate;
        private RecyclerView mHorizontalRecyclerView;


        public MyRecycleViewHolder(View itemView) {
            super(itemView);
            mHorizontalRecyclerView = itemView.findViewById(R.id.recycler_view_horizontal);
            if (mHorizontalRecyclerView != null){
                mHorizontalRecyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
                mHorizontalRecyclerView.setAdapter(new HorizontalRecycleViewAdapter(context, TestData.getPersons(10)));
                return;
            }
            mOnline = itemView.findViewById(R.id.iv_online);
            mPhoto = itemView.findViewById(R.id.iv_photo);
            mName = itemView.findViewById(R.id.tv_name);
            mLastMessage = itemView.findViewById(R.id.tv_last_message);
            mRead = itemView.findViewById(R.id.iv_read);
            mDate = itemView.findViewById(R.id.tv_date);
        }


        public void bindHolder(Person person) {
            mName.setText(person.getName());
            mLastMessage.setText("Message");
        }
    }


}
