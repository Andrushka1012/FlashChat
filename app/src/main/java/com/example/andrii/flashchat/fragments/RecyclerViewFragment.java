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

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.adapters.GroupsListAdapter;
import com.example.andrii.flashchat.adapters.MessagesListAdapter;
import com.example.andrii.flashchat.adapters.OnlineListAdapter;
import com.example.andrii.flashchat.data.TestData;

public class RecyclerViewFragment extends Fragment{
    private final String TAG = "RecyclerViewFragment";
    public static final int FRAGMENT_TYPE_MESSAGES = 1;
    public static final int FRAGMENT_TYPE_ONLINE = 2;
    public static final int FRAGMENT_TYPE_GROUPS = 3;
    private static final String FRAGMENT_TYPE_ARGUMENT = "FRAGMENT_TYPE_ARGUMENT";
    private int type;

    private RecyclerView mRecyclerView;

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
        initializeRecyclerView(v);

        return v;
    }


    private void initializeRecyclerView(View v){
        mRecyclerView = v.findViewById(R.id.recycler_view);

        switch (type){
            case FRAGMENT_TYPE_MESSAGES:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                mRecyclerView.setAdapter(new MessagesListAdapter(getActivity(),TestData.getPersons(10)));
                break;
            case FRAGMENT_TYPE_ONLINE:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                mRecyclerView.setAdapter(new OnlineListAdapter(getActivity(),TestData.getPersons(10)));
                break;
            case FRAGMENT_TYPE_GROUPS:
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
                mRecyclerView.setAdapter(new GroupsListAdapter(getActivity(),TestData.getPersons(10)));
        }
    }

}
