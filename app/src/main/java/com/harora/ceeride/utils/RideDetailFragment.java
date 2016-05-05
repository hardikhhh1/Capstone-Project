package com.harora.ceeride.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.harora.ceeride.R;
import com.harora.ceeride.model.CeeridePlace;
import com.harora.ceeride.model.RideDetail;
import com.harora.ceeride.service.CeerideReciever;
import com.harora.ceeride.service.CeerideRideService;

import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RideDetailFragment extends Fragment implements CeerideReciever.Callback{

    // TODO: Customize parameter argument names
    public static final String ARG_COLUMN_COUNT = "column-count";
    public static final String PICK_UP_LOCATION = "pick-up-location";
    public static final String DROP_OFF_LOCATION = "drop-off-location";
    public static final String BROADCASTRECIEVER_MSG = "rideDetailsRecieved";
    // TODO: Customize parameters

    private int mColumnCount = 4;
    CeeridePlace pickUpLocation;
    CeeridePlace dropOffLocation;
    MyRideDetailRecyclerViewAdapter adapter;
    ArrayList<RideDetail> fragmentRideDetails;
    CeerideReciever reciever;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RideDetailFragment() {
    }



    public void showExceptionMessage(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RideDetailFragment newInstance(int columnCount) {
        RideDetailFragment fragment = new RideDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            pickUpLocation = (CeeridePlace) getArguments().get(PICK_UP_LOCATION);
            dropOffLocation = (CeeridePlace) getArguments().get(DROP_OFF_LOCATION);
        }

        if(pickUpLocation == null){
            showExceptionMessage("Please select pick up location");
        } else if(dropOffLocation == null){
            showExceptionMessage("Please select drop off location");
        }

        reciever = new CeerideReciever(this);

        IntentFilter filter = new IntentFilter(BROADCASTRECIEVER_MSG);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        this.getActivity().registerReceiver(reciever, filter);
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
    public void onRideDetails(ArrayList<RideDetail> rideDetails) {
        TwoWayView recyclerView = (TwoWayView) getView();
        if(fragmentRideDetails == null){
            fragmentRideDetails = new ArrayList<>();
        }
        fragmentRideDetails.addAll(rideDetails);

        // TODO : ADd the object and notify data changed from last item only
        // as its much faster.
        if(adapter != null){
            adapter.setmRideDetails(fragmentRideDetails);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(RideDetail rideDetail);
    }
}
