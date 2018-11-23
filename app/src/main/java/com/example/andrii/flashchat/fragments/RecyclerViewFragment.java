package com.example.andrii.flashchat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.adapters.MessagesListAdapter;
import com.example.andrii.flashchat.adapters.OnlineListAdapter;
import com.example.andrii.flashchat.data.DB.MessageDb;
import com.example.andrii.flashchat.data.Model.MessagePersonItem;
import com.example.andrii.flashchat.data.Model.Person;
import com.example.andrii.flashchat.tools.QueryPreferences;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RecyclerViewFragment extends Fragment{
    public static final int FRAGMENT_TYPE_MESSAGES = 1;
    public static final int FRAGMENT_TYPE_ONLINE = 2;
    private static final String FRAGMENT_TYPE_ARGUMENT = "FRAGMENT_TYPE_ARGUMENT";
    private int type;

    private Realm mRealm;
    private RecyclerView mRecyclerView;
    private TextView tvInformation;


    public static RecyclerViewFragment newInstance(int type){
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FRAGMENT_TYPE_ARGUMENT,type);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mRealm = Realm.getDefaultInstance();
        type = getArguments().getInt(FRAGMENT_TYPE_ARGUMENT);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycler_view,container,false);
        tvInformation = v.findViewById(R.id.tvInformation);

        initializeRecyclerView(v);

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    public void updateUi(List<Person> onlineList){
        switch (type){
            case FRAGMENT_TYPE_MESSAGES:
                setUpMessagesList(onlineList);
                break;
            case FRAGMENT_TYPE_ONLINE:
                setUpOnlineList(onlineList);
                break;
        }
    }

    private void initializeRecyclerView(View v){
        switch (type){
            case FRAGMENT_TYPE_MESSAGES:
                mRecyclerView = v.findViewById(R.id.recycler_view);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                setUpMessagesList(null);
                break;
            case FRAGMENT_TYPE_ONLINE:
                mRecyclerView = v.findViewById(R.id.recycler_view);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                setUpOnlineList(null);
                tvInformation.setText("Loading..");
                break;
        }
    }

    private void setUpMessagesList(List<Person> onlineList){
        RealmResults<MessageDb>resultsSender = mRealm.where(MessageDb.class)
                .equalTo("recipient_id",QueryPreferences.getActiveUserId(getActivity()))
                .distinctValues("senderId")
                .sort("date", Sort.DESCENDING)
                .findAll();
        RealmResults<MessageDb>resultsRecipient = mRealm.where(MessageDb.class)
                .equalTo("senderId",QueryPreferences.getActiveUserId(getActivity()))
                .distinctValues("recipient_id")
                .sort("date", Sort.DESCENDING)
                .findAll();

        List<MessagePersonItem> list = MessagePersonItem.convertToList(resultsSender,
                resultsRecipient,
                QueryPreferences.getActiveUserId(getActivity()));

        if (list.isEmpty()){
            showInfo(true);
        }else{
            showInfo(false);
            mRecyclerView.setAdapter(new MessagesListAdapter(getActivity(),list,onlineList));
        }
    }

    private void setUpOnlineList(List<Person> onlineList){
        if (onlineList == null){
            tvInformation.setText("Error with getting information about online users.");
            showInfo(true);
            return;
        }
        if (onlineList.isEmpty()){
            tvInformation.setText("Nobody online.");
            showInfo(true);
            return;
        }

        mRecyclerView.setAdapter(new OnlineListAdapter(getActivity(),onlineList));
        showInfo(false);
    }

    private void showInfo(boolean show){
        tvInformation.setVisibility(show?View.VISIBLE:View.GONE);
        mRecyclerView.setVisibility(show?View.GONE:View.VISIBLE);
    }

}