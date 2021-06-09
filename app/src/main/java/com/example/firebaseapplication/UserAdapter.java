package com.example.firebaseapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseapplication.model.User;
import com.example.firebaseapplication.model.UserResponse;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private ArrayList<UserResponse> listData;
    public onItemClick event;

    public UserAdapter(Context context, ArrayList<UserResponse> listData) {
        this.context = context;
        this.listData = listData;
    }

    public void setListData(ArrayList<UserResponse> listData) {
        this.listData = listData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserResponse data = listData.get(position);
        holder.onBind(data, position);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView name, email;
        private UserResponse user;
        private int position;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            itemView.setOnClickListener(v -> {
                if (event != null) {
                    event.onClick(user, position);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (event != null) {
                    event.onDelete(user, position);
                }
                return false;
            });
        }

        public void onBind(UserResponse data, int dataPosition) {
            user = data;
            position = dataPosition;
            name.setText(data.getName());
            email.setText(data.getEmail());
        }
    }

    public interface onItemClick {
        public void onClick(UserResponse data, int position);
        public void onDelete(UserResponse data, int position);
    }
}
