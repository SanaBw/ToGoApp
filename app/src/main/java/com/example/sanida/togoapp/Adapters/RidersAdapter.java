package com.example.sanida.togoapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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


    public RidersAdapter(Context context, HashMap<String, Object> riders) {
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        ridersArray = new ArrayList<>(riders.values());
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull RidersAdapter.ViewHolder holder, int position) {
        String id = ridersArray.get(position).toString();

        fetchUserData(id, holder);
    }


    @NonNull
    @Override
    public RidersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = layoutInflater.inflate(R.layout.rider_profile, parent, false);
        return new ViewHolder(rootView);
    }


    @Override
    public int getItemCount() {
        return ridersArray.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView riderPhoto;
        TextView riderName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            riderName = itemView.findViewById(R.id.riderName);
            riderPhoto = itemView.findViewById(R.id.riderPhoto);
        }
    }


    private void fetchUserData(String id, RidersAdapter.ViewHolder holder) {
        final RidersAdapter.ViewHolder viewHolder = holder;

        FirebaseDatabase.getInstance().getReference().child("/users").child(id).addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

                        if (Objects.equals(postSnapShot.getKey(), "photo")) {
                            String userPhoto = Objects.requireNonNull(postSnapShot.getValue()).toString();
                            try {
                                Glide.with(context)
                                        .load(FirebaseStorage.getInstance().getReference().
                                                child("/images").
                                                child(userPhoto))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(viewHolder.riderPhoto);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (Objects.equals(postSnapShot.getKey(), "name")) {
                            String userName = Objects.requireNonNull(postSnapShot.getValue()).toString();
                            viewHolder.riderName.setText(userName);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        }));
    }
}
