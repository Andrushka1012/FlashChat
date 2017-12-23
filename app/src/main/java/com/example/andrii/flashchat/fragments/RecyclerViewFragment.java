package com.example.andrii.flashchat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.adapters.GroupsListAdapter;
import com.example.andrii.flashchat.adapters.MessagesListAdapter;
import com.example.andrii.flashchat.adapters.OnlineListAdapter;
import com.example.andrii.flashchat.data.DB.MessageDb;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.TestData;
import com.example.andrii.flashchat.data.actions.ActionGetOnlinePersonData;
import com.example.andrii.flashchat.tools.QueryAction;
import com.example.andrii.flashchat.tools.QueryPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import rx.Observer;

public class RecyclerViewFragment extends Fragment{
    private final String TAG = "RecyclerViewFragment";
    public static final int FRAGMENT_TYPE_MESSAGES = 1;
    public static final int FRAGMENT_TYPE_ONLINE = 2;
    public static final int FRAGMENT_TYPE_GROUPS = 3;
    private static final String FRAGMENT_TYPE_ARGUMENT = "FRAGMENT_TYPE_ARGUMENT";
    private int type;

    private RecyclerView mRecyclerView;
    private TextView tvinformation;

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

        type = getArguments().getInt(FRAGMENT_TYPE_ARGUMENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycler_view,container,false);
        tvinformation = v.findViewById(R.id.tvInformation);
        initializeRecyclerView(v);

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initializeRecyclerView(View v){
        mRecyclerView = v.findViewById(R.id.recycler_view);

        switch (type){
            case FRAGMENT_TYPE_MESSAGES:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                mRecyclerView.setAdapter(new MessagesListAdapter(getActivity(),TestData.getPersons(10)));
                break;
            case FRAGMENT_TYPE_ONLINE:
                ActionGetOnlinePersonData action = new ActionGetOnlinePersonData(QueryPreferences.getActiveUserId(getActivity()));
                QueryAction.executeAnswerQuery(getActivity(),action,TAG)
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                tvinformation.setVisibility(View.VISIBLE);
                                tvinformation.setText("Error with getting information about online users =(");
                            }

                            @Override
                            public void onNext(String s) {
                                if (!s.equals("error")){
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<MessageDb>>(){}.getType();
                                    List<Person> list = gson.fromJson(s,listType);
                                    tvinformation.setVisibility(list.size() == 0?View.VISIBLE:View.GONE);
                                    if(list.size() == 0) tvinformation.setText("No user online");
                                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    mRecyclerView.setAdapter(new OnlineListAdapter(getActivity(),list));
                                }else {
                                    tvinformation.setVisibility(View.VISIBLE);
                                    tvinformation.setText("Error with getting information about online users =(");
                                }


                            }
                        });

                break;
            case FRAGMENT_TYPE_GROUPS:
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
                mRecyclerView.setAdapter(new GroupsListAdapter(getActivity(),TestData.getPersons(10)));
        }
    }

}
