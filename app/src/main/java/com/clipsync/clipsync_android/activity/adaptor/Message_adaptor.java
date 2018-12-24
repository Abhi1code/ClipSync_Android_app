package com.clipsync.clipsync_android.activity.adaptor;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.clipsync.clipsync_android.R;
import java.util.ArrayList;

public class Message_adaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Message_gs> arrayList;

    public Message_adaptor(Context context, ArrayList<Message_gs> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayList.get(position).isSelf()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {

            case 0:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_message_layout, parent, false);
                return new Viewholder0(view);
            case 1:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_message_layout, parent, false);
                return new Viewholder1(view2);

        }
         return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()){
            case 0 :
                Viewholder0 right = (Viewholder0)holder;
                right.sender_name.setText(arrayList.get(position).getName());
                right.time.setText("("+arrayList.get(position).getTime()+") ");
                right.msg.setText(arrayList.get(position).getMsg());
                break;
            case 1 :
                Viewholder1 left = (Viewholder1)holder;
                left.sender_name.setText(arrayList.get(position).getName());
                left.time.setText("("+arrayList.get(position).getTime()+") ");
                left.msg.setText(arrayList.get(position).getMsg());
                break;
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class Viewholder0 extends RecyclerView.ViewHolder {

        public TextView sender_name, time, msg;
        public ConstraintLayout layout;

        public Viewholder0(View itemView) {
            super(itemView);
            sender_name = itemView.findViewById(R.id.sender_name);
            time = itemView.findViewById(R.id.time);
            msg = itemView.findViewById(R.id.msg);
            layout = itemView.findViewById(R.id.layout);
        }
    }

    public static class Viewholder1 extends RecyclerView.ViewHolder {

        public TextView sender_name, time, msg;
        public ConstraintLayout layout;

        public Viewholder1(View itemView) {
            super(itemView);
            sender_name = itemView.findViewById(R.id.sender_name);
            time = itemView.findViewById(R.id.time);
            msg = itemView.findViewById(R.id.msg);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}
