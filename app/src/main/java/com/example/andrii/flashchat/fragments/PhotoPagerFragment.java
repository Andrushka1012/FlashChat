package com.example.andrii.flashchat.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.andrii.flashchat.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.File;

public class PhotoPagerFragment extends Fragment {
    private static String ID_ARGUMENT = "ID_ARGUMENT";
    private static String msgId;
    public static Fragment newInstance(String msgId){
        Bundle bundle = new Bundle();
        bundle.putString(ID_ARGUMENT,msgId);

        PhotoPagerFragment fragment = new PhotoPagerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_pager,container,false);
        PhotoView imageView = view.findViewById(R.id.iv_photo);

        msgId = getArguments().getString(ID_ARGUMENT);

        String root = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
        File file = new File(root,msgId + ".jpg");
        Picasso.with(getActivity()).load(file).into(imageView);

        return view;
    }
}
