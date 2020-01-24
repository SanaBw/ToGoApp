package com.example.sanida.togoapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanida.togoapp.Fragments.RiderProfileFragment;
import com.example.sanida.togoapp.Models.User;
import com.example.sanida.togoapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

class RidersAdapter extends RecyclerView.Adapter<RidersAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Object> ridersArray;
    private String riderId;
    private User currentUserModel;


    public RidersAdapter(Context context, HashMap<String, Object> riders) {
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        ridersArray = new ArrayList<>(riders.values());
        this.context = context;
    }


    @NonNull
    @Override
    public RidersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = layoutInflater.inflate(R.layout.rider_profile, parent, false);
        return new ViewHolder(rootView);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView riderPhoto;
        TextView riderName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            riderName = itemView.findViewById(R.id.riderName);
            riderPhoto = itemView.findViewById(R.id.riderPhoto);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RiderProfileFragment riderProfileFragment = new RiderProfileFragment(riderId, currentUserModel);
                    ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentFrame, riderProfileFragment)
                            .commit();

                    TripAdapter.infoPopup.dismiss();
                }
            });
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RidersAdapter.ViewHolder holder, int position) {
        riderId = ridersArray.get(position).toString();
        currentUserModel=TripAdapter.getUserModel(riderId);

        TripAdapter.fetchUserData(context, riderId, holder.riderName, holder.riderPhoto);
    }


    @Override
    public int getItemCount() {
        return ridersArray.size();
    }
}
