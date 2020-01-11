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
import com.example.sanida.togoapp.Models.Request;
import com.example.sanida.togoapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Request> requests;
    String userPhoto, userName;
    FirebaseUser currentUser;

    public RequestAdapter(Context context, ArrayList<Request> requests) {
        this.context=context;
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.requests=requests;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }
    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = layoutInflater.inflate(R.layout.request, parent,false);
        return new RequestAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {
        Request request=requests.get(position);
        String txt="wants to ride with you\nfrom " + request.getTrip().getStartLocation() + "\nto " + request.getTrip().getEndLocation();
        holder.txtView.setText(txt);

        fetchUserData(request.getRider(), holder);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtView;
        TextView userName;
        ImageView userImg;

        ViewHolder(final View itemView) {
            super(itemView);
            txtView = itemView.findViewById(R.id.txtView);
            userImg = itemView.findViewById(R.id.photo);
            userName=itemView.findViewById(R.id.userName);

        }

        @Override
        public void onClick(View view) {
        }

    }





    @Override
    public int getItemCount() {
        return requests.size();
    }

    private void fetchUserData(String user, final RequestAdapter.ViewHolder holder) {

        final RequestAdapter.ViewHolder viewHolder = holder;

        FirebaseDatabase.getInstance().getReference().child("/users").child(user).addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

                        if (Objects.equals(postSnapShot.getKey(), "photo")) {
                            userPhoto = Objects.requireNonNull(postSnapShot.getValue()).toString();
                            try{
                                Glide.with(context)
                                        .load(FirebaseStorage.getInstance().getReference().
                                                child("/images").
                                                child(userPhoto))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(viewHolder.userImg);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        if (Objects.equals(postSnapShot.getKey(), "name")) {
                            userName = Objects.requireNonNull(postSnapShot.getValue()).toString();
                            viewHolder.userName.setText(userName);
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
