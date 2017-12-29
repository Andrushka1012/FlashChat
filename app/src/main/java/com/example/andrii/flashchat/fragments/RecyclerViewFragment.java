package com.example.andrii.flashchat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.adapters.GroupsListAdapter;
import com.example.andrii.flashchat.adapters.MessagesListAdapter;
import com.example.andrii.flashchat.adapters.OnlineListAdapter;
import com.example.andrii.flashchat.data.DB.MessageDb;
import com.example.andrii.flashchat.data.MessagePersonItem;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.TestData;
import com.example.andrii.flashchat.data.actions.ActionGetOnlinePersonData;
import com.example.andrii.flashchat.tools.QueryAction;
import com.example.andrii.flashchat.tools.QueryPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observer;

public class RecyclerViewFragment extends Fragment{
    private final String TAG = "RecyclerViewFragment";
    public static final int FRAGMENT_TYPE_MESSAGES = 1;
    public static final int FRAGMENT_TYPE_ONLINE = 2;
    public static final int FRAGMENT_TYPE_GROUPS = 3;
    private static final String FRAGMENT_TYPE_ARGUMENT = "FRAGMENT_TYPE_ARGUMENT";
    private int type;

    private Realm mRealm;
    private static List<Person> onlineList = new ArrayList<>();
    private RecyclerView mRecyclerViewMessages;
    private static RecyclerView mRecyclerViewOnline;
    private RecyclerView mRecyclerViewGroup;
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

    private void initializeRecyclerView(View v){
        switch (type){
            case FRAGMENT_TYPE_MESSAGES:
                mRecyclerViewMessages = v.findViewById(R.id.recycler_view);
                mRecyclerViewMessages.setLayoutManager(new LinearLayoutManager(getActivity()));
                RealmResults<MessageDb>resultsSender = mRealm.where(MessageDb.class)
                        .distinctValues("senderId")
                        .sort("date", Sort.DESCENDING)
                        .findAll();
                RealmResults<MessageDb>resultsRecipient = mRealm.where(MessageDb.class)
                        .distinctValues("recipient_id")
                        .sort("date", Sort.DESCENDING)
                        .findAll();

                Log.d(TAG,"ResultSenderSize:" + resultsSender.size());
                Log.d(TAG,"ResultRecipientSize:" + resultsRecipient.size());
                List<MessagePersonItem> list = MessagePersonItem.convertToList(resultsSender,resultsRecipient,QueryPreferences.getActiveUserId(getActivity()));
                Log.d(TAG,"ResultListSize:" + list.size());
                mRecyclerViewMessages.setAdapter(new MessagesListAdapter(getActivity(),list,onlineList));

                break;
            case FRAGMENT_TYPE_ONLINE:
                mRecyclerViewOnline = v.findViewById(R.id.recycler_view);
                ActionGetOnlinePersonData action = new ActionGetOnlinePersonData(QueryPreferences.getActiveUserId(getActivity()));
                QueryAction.executeAnswerQuery(action)
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG,"OnError:",e);
                                tvInformation.setVisibility(View.VISIBLE);
                                tvInformation.setText("Error with getting information about online users =(");

                            }

                            @Override
                            public void onNext(String s) {
                                Log.d(TAG,"onNext:" + s);
                                if (!s.equals("error") && s != null){
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<Person>>(){}.getType();
                                    onlineList = gson.fromJson(s,listType);

                                    tvInformation.setVisibility(onlineList.size() == 0?View.VISIBLE:View.GONE);
                                    if(onlineList.size() == 0) tvInformation.setText("No user online");
                                    mRecyclerViewOnline.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    mRecyclerViewOnline.setAdapter(new OnlineListAdapter(getActivity(), onlineList));
                                    if (mRecyclerViewMessages != null) mRecyclerViewMessages.getAdapter().notifyDataSetChanged();
                                }else {
                                    tvInformation.setVisibility(View.VISIBLE);
                                    tvInformation.setText("Error with getting information about online users =(");


                                }


                            }
                        });

                break;
            case FRAGMENT_TYPE_GROUPS:
                mRecyclerViewGroup = v.findViewById(R.id.recycler_view);
                mRecyclerViewGroup.setLayoutManager(new GridLayoutManager(getActivity(),2));
                mRecyclerViewGroup.setAdapter(new GroupsListAdapter(getActivity(),TestData.getPersons(10)));
        }
    }

}
