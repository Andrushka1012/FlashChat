package com.example.andrii.flashchat.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.actions.ActionGetPersonData;
import com.example.andrii.flashchat.tools.ImageTools;
import com.example.andrii.flashchat.tools.QueryAction;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.Observer;

import static android.widget.Toast.LENGTH_LONG;

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

            itemView.setOnLongClickListener(view ->{
                PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                popupMenu.inflate(R.menu.profile_context_menu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.action_profile:
                            ProfileActivity.startActivity(context,mPersons.get(getAdapterPosition()).getId());
                            return true;
                        default:
                            return false;
                    }

                });
                popupMenu.show();
                return true;
            });



            itemView.setOnClickListener(v ->{
                    Person p = mPersons.get(getAdapterPosition());
                    context.startActivity(CorrespondenceListActivity.newIntent(context,p,true));
            });

        }

        public void bindHolder(Person person) {
            mName.setText(person.getName());
            ImageTools tools = new ImageTools(context);
            tools.downloadPersonImage(mPhoto,person);
        }
    }

}
