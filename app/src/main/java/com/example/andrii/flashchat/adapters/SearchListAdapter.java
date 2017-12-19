package com.example.andrii.flashchat.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrii.flashchat.Activitys.ProfileActivity;
import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.SearchItem;
import com.example.andrii.flashchat.data.SingletonConnection;
import com.example.andrii.flashchat.data.actions.ActionGetPersonData;
import com.example.andrii.flashchat.tools.ImageTools;
import com.example.andrii.flashchat.tools.QueryAction;
import com.google.gson.Gson;

import java.util.List;

import rx.Observable;
import rx.Observer;

import static android.widget.Toast.LENGTH_LONG;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.MyHolder> {

    private Context context;
    private List<SearchItem> mItems;

    public SearchListAdapter(Context con, List<SearchItem> list){
        context = con;
        mItems = list;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.online_list_item, parent, false);

        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.bindHolder(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private ImageView mOnline;
        private ImageView mPhoto;
        private TextView mName;



        public MyHolder(View itemView) {
            super(itemView);

            mOnline = itemView.findViewById(R.id.iv_online);
            mPhoto = itemView.findViewById(R.id.iv_photo);
            mName = itemView.findViewById(R.id.tv_name);

            itemView.setOnCreateContextMenuListener((contextMenu, view, contextMenuInfo) ->
                    contextMenu.add(0,view.getId(),0,"Profile"));

            itemView.setOnLongClickListener(view ->{
                PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                popupMenu.inflate(R.menu.profile_context_menu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.action_profile:
                            final Person[] person = new Person[1];
                            ActionGetPersonData action = new ActionGetPersonData(mItems.get(getAdapterPosition()).getId());
                            Observable<String> observable = QueryAction.executeAnswerQuery(context,action, "qwe");
                            observable.subscribe(new Observer<String>() {
                                @Override
                                public void onCompleted() {
                                    SingletonConnection.getInstance().close();
                                    Intent intent = ProfileActivity.newIntent(context, person[0]);
                                    context.startActivity(intent);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    SingletonConnection.getInstance().close();
                                }

                                @Override
                                public void onNext(String s) {
                                    if (s.equals("not found")) {
                                        Toast.makeText(context, "Server error", LENGTH_LONG).show();
                                    } else {
                                        Gson gson = new Gson();
                                        person[0] = gson.fromJson(s, Person.class);
                                    }

                                }
                            });

                        return true;
                    default:
                        return false;
                    }

                });
                popupMenu.show();
                return true;
            });

            itemView.setOnClickListener(v ->{
                SearchItem item = mItems.get(getAdapterPosition());
               // context.startActivity(CorrespondenceListActivity.newIntent(context,item));
            });

        }

        public void bindHolder(SearchItem item) {
            mName.setText(item.getName());
            mOnline.setVisibility(item.isOnline()?View.VISIBLE:View.GONE);
            Person p = new Person(item.getId(),item.getName(),"","","","",item.getImageSrc());
            ImageTools tools = new ImageTools(context);
            tools.downloadPersonImage(mPhoto,p);

        }
    }
}
