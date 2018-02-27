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
import com.example.andrii.flashchat.data.Model.Person;
import com.example.andrii.flashchat.data.actions.ActionGetPersonData;
import com.example.andrii.flashchat.tools.ImageTools;
import com.example.andrii.flashchat.tools.QueryAction;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

import static android.widget.Toast.LENGTH_LONG;

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
        public MyRecycleViewHolder(View itemView) {
            super(itemView);
            mIvPhoto = itemView.findViewById(R.id.iv_photo);
            mName = itemView.findViewById(R.id.tv_name);

            itemView.setOnClickListener(v -> {
                Person p = mPersons.get(getAdapterPosition());
                context.startActivity(CorrespondenceListActivity.newIntent(context,p,true));
            });

            itemView.setOnClickListener(v -> {
                Intent intent = CorrespondenceListActivity.newIntent(context,mPersons.get(getAdapterPosition()),true);
                context.startActivity(intent);
            });


            itemView.setOnLongClickListener(view ->{
                PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                popupMenu.inflate(R.menu.profile_context_menu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.action_profile:
                            final Person[] person = new Person[1];
                            ActionGetPersonData action = new ActionGetPersonData(mPersons.get(getAdapterPosition()).getId());
                            Observable<String> observable = QueryAction.executeAnswerQuery(action);
                            Subscription subscription = observable.subscribe(new Observer<String>() {
                                @Override
                                public void onCompleted() {
                                    Intent intent = ProfileActivity.newIntent(context, person[0]);
                                    context.startActivity(intent);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e("","onError",e);
                                    Toast.makeText(context, "Server error", LENGTH_LONG).show();
                                    String id = mPersons.get(getAdapterPosition()).getId();
                                    Realm realm = Realm.getDefaultInstance();
                                    String name = realm.where(UserNamesBd.class)
                                            .equalTo("userId",id)
                                            .findFirst()
                                            .getName();
                                    realm.close();
                                    if (name == null) name = "User";
                                    person[0] = new Person(
                                            id,name,new Date().toString(),"Number","email@email.com","gender","offline");
                                    Intent intent = ProfileActivity.newIntent(context, person[0]);
                                    context.startActivity(intent);
                                }

                                @Override
                                public void onNext(String s) {
                                    if (s.equals("error")) {
                                        Toast.makeText(context, "Server error", LENGTH_LONG).show();
                                    } else {
                                        Gson gson = new Gson();
                                        person[0] = gson.fromJson(s, Person.class);

                                    }

                                }
                            });
                            QueryAction.addSubscription(subscription);
                            return true;
                        default:
                            return false;
                    }

                });
                popupMenu.show();
                return true;
            });



        }
        public void bindHolder(Person person) {
            ImageTools tools = new ImageTools(context);
            tools.downloadPersonImage(mIvPhoto,person,false);
            String name;
            if (person.getName().length() >=7) name = person.getName().substring(0,3) + "..";
            else name = person.getName();

            mName.setText(name);
        }
    }
}
