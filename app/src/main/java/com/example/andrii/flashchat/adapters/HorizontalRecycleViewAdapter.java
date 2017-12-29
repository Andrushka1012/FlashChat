package com.example.andrii.flashchat.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrii.flashchat.Activities.CorrespondenceListActivity;
import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.tools.ImageTools;

import java.util.List;

public class HorizontalRecycleViewAdapter extends RecyclerView.Adapter<HorizontalRecycleViewAdapter.MyRecycleViewHolder> {

    private Context context;
    private List<Person> mPersons;

    public HorizontalRecycleViewAdapter(Context con, List<Person> list){
        context = con;
        mPersons = list;
    }

    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.horizontal_online_list_item,parent,false);
        return new MyRecycleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyRecycleViewHolder holder, int position) {
        holder.bindHolder(mPersons.get(position));
    }

    @Override
    public int getItemCount() {
        return mPersons.size();
    }

    public class MyRecycleViewHolder extends RecyclerView.ViewHolder{
        ImageView mIvPhoto;
        TextView mName;
        //CheckBox mOnline;
        public MyRecycleViewHolder(View itemView) {
            super(itemView);
            mIvPhoto = itemView.findViewById(R.id.iv_photo);
            mName = itemView.findViewById(R.id.tv_name);

            itemView.setOnClickListener(v -> {
                Person p = mPersons.get(getAdapterPosition());
                context.startActivity(CorrespondenceListActivity.newIntent(context,p));
            });
        }
        public void bindHolder(Person person) {
            ImageTools tools = new ImageTools(context);
            tools.downloadPersonImage(mIvPhoto,person);
            String name;
            if (person.getName().length() >=7) name = person.getName().substring(0,3) + "..";
            else name = person.getName();

            mName.setText(name);
        }
    }
}
