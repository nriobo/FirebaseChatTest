package com.wichisoft.firebasechat.util;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.wichisoft.firebasechat.ChatMessagesActivity;
import com.wichisoft.firebasechat.R;
import com.wichisoft.firebasechat.TrackingService;
import com.wichisoft.firebasechat.model.User;

/**
 * Created by ronnykibet on 11/23/17.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<User> mUsersList;
    private Context mContext;



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView personNameTxtV;
        public TextView personDistanceTxtV;
        public ImageView personImageImgV;


        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            personNameTxtV = (TextView) v.findViewById(R.id.userName);
            personDistanceTxtV = (TextView) v.findViewById(R.id.userDistanceToMe);
            personImageImgV = (ImageView) v.findViewById(R.id.userImage);
        }
    }

    public void add(int position, User person) {
        mUsersList.add(position, person);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mUsersList.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public UsersAdapter(List<User> myDataset, Context context) {
        mUsersList = myDataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.user_single_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final User user = mUsersList.get(position);
        holder.personNameTxtV.setText(user.getDisplayName());


        if ((TrackingService.longitude == 0.0 && TrackingService.latitude == 0.0) ||
                (user.getLatitude() == 0.0 && user.getLongitude() == 0.0))  {

            holder.personDistanceTxtV.setText("-");
            Log.i("LocationX", "pongo - a  " + user.getDisplayName());
        }
        else {
            Log.i("LocationX", user.getDisplayName() + " lat " + user.getLatitude());
            Location loc1 = new Location("");
            loc1.setLatitude(TrackingService.latitude);
            loc1.setLongitude(TrackingService.longitude);

            Location loc2 = new Location("");
            loc2.setLatitude(user.getLatitude());
            loc2.setLongitude(user.getLongitude());

            float distanceInMeters = loc1.distanceTo(loc2);
            if (distanceInMeters > 1000) {
                holder.personDistanceTxtV.setText(Math.floor(distanceInMeters / 1000) + " km");
            }
            else {
                holder.personDistanceTxtV.setText(Math.floor(distanceInMeters) + " m");
            }
        }



        try {
            Picasso.with(mContext).load(user.getImage()).placeholder(R.mipmap.ic_launcher).into(holder.personImageImgV);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //listen to single view layout click
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send this user id to chat messages activity
                goToUpdateActivity(user.getUserId());
            }
        });


    }

    private void goToUpdateActivity(String personId){
        Intent goToUpdate = new Intent(mContext, ChatMessagesActivity.class);
        goToUpdate.putExtra("USER_ID", personId);
        mContext.startActivity(goToUpdate);
    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

}