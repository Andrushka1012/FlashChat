package com.example.andrii.flashchat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrii.flashchat.Activitys.CorrespondenceListActivity;
import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Person;

import java.util.List;

public class OnlineListAdapter  extends RecyclerView.Adapter<OnlineListAdapter.MyHolder> {

    private Context context;
    private List<Person> mPersons;

    public OnlineListAdapter(Context con, List<Person> list){
        context = con;
        mPersons = list;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.online_list_item, parent, false);

        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.bindHolder(mPersons.get(position));
    }

    @Override
    public int getItemCount() {
        return mPersons.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        private ImageView mOnline;
        private ImageView mPhoto;
        private TextView mName;


        public MyHolder(View itemView) {
            super(itemView);
            mOnline = itemView.findViewById(R.id.iv_online);
            mPhoto = itemView.findViewById(R.id.iv_photo);
            mName = itemView.findViewById(R.id.tv_name);

            itemView.setOnClickListener(v ->{
                    Person p = mPersons.get(getAdapterPosition());
                    context.startActivity(CorrespondenceListActivity.newIntent(context,p));
            });

        }

        public void bindHolder(Person person) {
            mName.setText(person.getName());
            //loadPhoto
        }
    }

}
