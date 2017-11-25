package com.example.andrii.flashchat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Person;

import java.util.List;

public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.MyHolder>{
    private final String TAG = "GroupsListAdapter";
    private Context context;
    private List<Person> mGroups;

    public GroupsListAdapter(Context con, List<Person> list){
        context = con;
        mGroups = list;
    }


    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.groups_list_item, parent, false);

        return new GroupsListAdapter.MyHolder(v);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        private ImageView mPhoto;
        private TextView mName;
        private TextView mLastMessageDate;
        private TextView mParticipants;



        public MyHolder(View itemView) {
            super(itemView);
            mPhoto = itemView.findViewById(R.id.iv_photo);
            mName = itemView.findViewById(R.id.tv_name);
        }

        public void bindHolder(Person person) {

        }
    }
}
