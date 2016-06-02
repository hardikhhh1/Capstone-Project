package com.harora.ceeride.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harora.ceeride.R;

import butterknife.ButterKnife;

public class MyRideDetailRecyclerFragment extends Fragment{

    private RecyclerView ridesAdaptor;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_ridedetail_list, container, false);
        ridesAdaptor = (RecyclerView) view.findViewById(R.id.list);
        ButterKnife.bind(this, view);
        return view;
    }
}
