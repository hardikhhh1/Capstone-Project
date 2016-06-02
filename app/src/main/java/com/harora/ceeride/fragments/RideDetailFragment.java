package com.harora.ceeride.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.harora.ceeride.R;
import com.harora.ceeride.model.CeeridePlace;
import com.harora.ceeride.model.RideDetail;
import com.harora.ceeride.service.CeerideReceiver;
import com.harora.ceeride.service.CeerideRideService;
import com.harora.ceeride.utils.CeerideRideUtil;
import com.harora.ceeride.utils.MyRideDetailRecyclerViewAdapter;

import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RideDetailFragment extends Fragment implements CeerideReceiver.Callback {

    public static final String PICK_UP_LOCATION = "pick-up-location";
    public static final String DROP_OFF_LOCATION = "drop-off-location";
    public static final String BROADCASTRECEIVER_MSG = "rideDetailsReceived";
    public static final String RIDE_TYPES = "rideTypes";
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private CeeridePlace pickUpLocation;
    private CeeridePlace dropOffLocation;
    private MyRideDetailRecyclerViewAdapter adapter;
    private List<RideDetail> rideDetails;
    private CeerideReceiver receiver;
    private OnListFragmentInteractionListener mListener;

    private Set<CeerideReceiver.RideType> rideTypes;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RideDetailFragment() {
    }

    private void showExceptionMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            pickUpLocation = (CeeridePlace) getArguments().get(PICK_UP_LOCATION);
            dropOffLocation = (CeeridePlace) getArguments().get(DROP_OFF_LOCATION);
        }

        if(pickUpLocation == null){
            showExceptionMessage("Please select pick up location");
        } else if(dropOffLocation == null){
            showExceptionMessage("Please select drop off location");
        }

        if (receiver == null) {
            receiver = new CeerideReceiver(this);
        }

        if (rideTypes == null) {
            rideTypes = new HashSet<>();
        }

        IntentFilter filter = new IntentFilter(BROADCASTRECEIVER_MSG);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        if (this.rideDetails == null) {
            this.getActivity().registerReceiver(receiver, filter);
        } else if (this.rideDetails.size() == 0) {
            this.getActivity().registerReceiver(receiver, filter);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.getActivity().unregisterReceiver(receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ridedetail_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            TwoWayView recyclerView = (TwoWayView) view;
            recyclerView.setHasFixedSize(true);
            final Drawable divider = getResources().getDrawable(R.drawable.divider);
            recyclerView.addItemDecoration(new DividerItemDecoration(divider));
            new CeerideRideUtil(this, pickUpLocation, dropOffLocation).getRideDetails();
        }

        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (rideDetails == null) return;
        Parcelable[] details = Arrays.copyOf(rideDetails.toArray(), rideDetails.size(),
                Parcelable[].class);
        outState.putParcelableArray(CeerideRideService.RIDE_DETAILS_KEY, details);
        outState.putSerializable("rideTypes", rideTypes.toArray());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState == null) return;

        Parcelable[] details = savedInstanceState.getParcelableArray(CeerideRideService.RIDE_DETAILS_KEY);
        Object[] rideTypesSerialized = (Object[]) savedInstanceState.getSerializable(RIDE_TYPES);
        rideTypes = new HashSet<>();
        for (Object o : rideTypesSerialized) {
            rideTypes.add((CeerideReceiver.RideType) o);
        }


        if (details == null) return;
        if (details.length == 0) return;

        RideDetail[] rideDetailsArray = Arrays.copyOf(details, details.length, RideDetail[].class);
        List<RideDetail> savedRideDetails = Arrays.asList(rideDetailsArray);
        this.rideDetails = new ArrayList<>();
        this.rideDetails.addAll(savedRideDetails);
        setAdapter();
    }

    @Override
    public void onRideDetails(List<RideDetail> rideDetails, CeerideReceiver.RideType rideType) {
        if (rideTypes.contains(rideType)) return;

        if (this.rideDetails == null) {
            this.rideDetails = new ArrayList<>();
            adapter = null;
        }

        this.rideDetails.addAll(rideDetails);
        rideTypes.add(rideType);

        // TODO : ADd the object and notify data changed from last item only
        // as its much faster.
        setAdapter();
    }


    private void setAdapter() {
        TwoWayView recyclerView = (TwoWayView) getView();
        if(adapter != null){
            adapter.setRideDetails(this.rideDetails);
            adapter.notifyDataSetChanged();
        } else{
            adapter = new MyRideDetailRecyclerViewAdapter(rideDetails,
                    mListener);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(RideDetail rideDetail);
    }
}
