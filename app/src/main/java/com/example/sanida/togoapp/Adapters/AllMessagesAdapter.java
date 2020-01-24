package com.example.sanida.togoapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanida.togoapp.Fragments.MessageFragment;
import com.example.sanida.togoapp.Models.Message;
import com.example.sanida.togoapp.Models.User;
import com.example.sanida.togoapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class AllMessagesAdapter extends RecyclerView.Adapter<AllMessagesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> allChatters;
    private LayoutInflater layoutInflater;
    private String currentUser;
    private User currentUserModel;

    public AllMessagesAdapter(Context context, ArrayList<User> allChatters) {
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.allChatters=allChatters;
    }


    @NonNull
    @Override
    public AllMessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = layoutInflater.inflate(R.layout.chat, parent, false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentUserModel = TripAdapter.getUserModel(currentUser);

        return new AllMessagesAdapter.ViewHolder(rootView);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName;
        User user;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.particImg);
            userName = itemView.findViewById(R.id.userName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openChat(itemView, user);
                }
            });
        }
    }

    private void openChat(View view, User user) {
        MessageFragment messageFragment = new MessageFragment(context, user, currentUserModel);
        ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentFrame, messageFragment)
                .commit();
    }


    @Override
    public void onBindViewHolder(@NonNull AllMessagesAdapter.ViewHolder holder, int position) {
        User user = allChatters.get(position);
        holder.user = user;
        TripAdapter.fetchUserData(context, user.getId(), holder.userName, holder.userImage);

    }

    @Override
    public int getItemCount() {
        return allChatters.size();
    }


}
