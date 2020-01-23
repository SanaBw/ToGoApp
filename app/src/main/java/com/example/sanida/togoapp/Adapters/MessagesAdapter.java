package com.example.sanida.togoapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanida.togoapp.Models.Message;
import com.example.sanida.togoapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Message> messages;
    private LayoutInflater layoutInflater;
    private String currentUser;

    public MessagesAdapter(Context context, ArrayList<Message> messages) {
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.context=context;
        this.messages=messages;
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = layoutInflater.inflate(R.layout.message, parent, false);
        return new MessagesAdapter.ViewHolder(rootView);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView message, dateTxt;
        LinearLayout linLay;
        ConstraintLayout msgLayout;
        GradientDrawable drawable;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linLay = itemView.findViewById(R.id.linLay);
            msgLayout = itemView.findViewById(R.id.msgLayout);
            message = itemView.findViewById(R.id.messageTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            drawable = new GradientDrawable();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openChat();
                }
            });
        }
    }

    private void openChat() {
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (message.getSender().getId().equals(currentUser)){
            holder.linLay.setGravity(Gravity.RIGHT);
            holder.drawable.setCornerRadius(20);
            holder.drawable.setColor(holder.msgLayout.getResources().getColor(R.color.accentTransparent));
            holder.msgLayout.setBackground(holder.drawable);
        } else {
            holder.linLay.setGravity(Gravity.LEFT);
            holder.drawable.setCornerRadius(20);
            holder.drawable.setColor(holder.msgLayout.getResources().getColor(R.color.greenTransparent));
            holder.msgLayout.setBackground(holder.drawable);
        }

        holder.message.setText(message.getContent());
        holder.dateTxt.setText(message.getDate() + "\t" + message.getTime());


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}
