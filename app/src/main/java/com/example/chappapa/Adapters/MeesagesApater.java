package com.example.chappapa.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chappapa.Models.Message;
import com.example.chappapa.R;
import com.example.chappapa.databinding.ItemReceivBinding;
import com.example.chappapa.databinding.ItemSendBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MeesagesApater extends RecyclerView.Adapter{
    Context context;
    ArrayList<Message> list;

    final  int item_sent=1;
    final int item_receive=2;

    public  MeesagesApater(Context context, ArrayList<Message> list){
        this.context=context;
        this.list=list;
    }

    @Override
    public int getItemViewType(int position) {
        Message message=list.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return  item_sent;
        }else{
            return item_receive;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==item_sent){
            View view= LayoutInflater.from(context).inflate(R.layout.item_send,parent,false);
            return new sentVeiwHolder(view);
        }else{
            View view= LayoutInflater.from(context).inflate(R.layout.item_receiv,parent,false);
            return new reciverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message=list.get(position);
        if (holder.getClass().equals(sentVeiwHolder.class)){
            sentVeiwHolder veiwHolder=(sentVeiwHolder) holder;
            veiwHolder.binding.mesaage.setText(message.getMessage());
        }else{
            reciverViewHolder veiwHolder=(reciverViewHolder) holder;
            veiwHolder.binding.message.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class sentVeiwHolder extends RecyclerView.ViewHolder{
        ItemSendBinding binding;
        public sentVeiwHolder(@NonNull View itemView) {
            super(itemView);
            binding=ItemSendBinding.bind(itemView);
        }
    }

    public  class  reciverViewHolder extends RecyclerView.ViewHolder{
        ItemReceivBinding binding;
        public reciverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=ItemReceivBinding.bind(itemView);
        }
    }
}
