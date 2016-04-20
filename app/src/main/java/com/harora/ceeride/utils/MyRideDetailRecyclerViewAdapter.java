package com.harora.ceeride.utils;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.harora.ceeride.R;
import com.harora.ceeride.model.RideDetail;
import com.harora.ceeride.utils.RideDetailFragment.OnListFragmentInteractionListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRideDetailRecyclerViewAdapter extends
        RecyclerView.Adapter<MyRideDetailRecyclerViewAdapter.ViewHolder> {

    private List<RideDetail> mRideDetails;
    private final OnListFragmentInteractionListener mListener;

    public MyRideDetailRecyclerViewAdapter(List<RideDetail> rideDetails,
                                           OnListFragmentInteractionListener listener) {
        mRideDetails = rideDetails;
        mListener = listener;
    }

    public List<RideDetail> getmRideDetails() {
        return mRideDetails;
    }

    public void setmRideDetails(List<RideDetail> mRideDetails){
        this.mRideDetails = mRideDetails;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ridedetail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mRideDetail = mRideDetails.get(position);
        holder.setRideDetails();
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mRideDetail);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRideDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public RideDetail mRideDetail;

        @Bind(R.id.ride_icon) public ImageView mRideIcon;
        @Bind(R.id.surcharge_icon) public ImageView mSurchargeIcon;
        @Bind(R.id.surcharge_value) public TextView mSurchargeValue;
        @Bind(R.id.ride_name) public TextView mRideName;
        @Bind(R.id.price_range) public TextView priceRange;
//        @Bind(R.id.time_estimate) public TextView timeEstimate;

        public void setRideDetails(){
            try {
                Drawable appIcon = mRideDetail.getAppIcon(mView.getContext());
                mRideIcon.setImageDrawable(appIcon);
            } catch (PackageManager.NameNotFoundException e){

            }
            Float surchargeValue = mRideDetail.getSurchargeValue();
            if(surchargeValue > 1.0){
                mSurchargeIcon.setVisibility(View.VISIBLE);
                mSurchargeValue.setText(Float.toString(surchargeValue));
            }
            mRideName.setText(mRideDetail.getRideName());
            priceRange.setText(mRideDetail.getRideCostString());
//            timeEstimate.setText(mRideDetail.getTimeEstimate());
        }

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

    }
}
