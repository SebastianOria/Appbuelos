package com.example.aplicacion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragments extends Fragment {

    private View ChatViewUnica;
    private RecyclerView ChatLista;
    private DatabaseReference Contactos;
    private FirebaseAuth auth;
    private String CurrentUserId;

    public ChatsFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid();
        Contactos = FirebaseDatabase.getInstance().getReference().child(CurrentUserId);
        ChatViewUnica = inflater.inflate(R.layout.fragment_chats_fragments, container, false);
        ChatLista = (RecyclerView) ChatViewUnica.findViewById(R.id.chat_lista);
        ChatLista.setLayoutManager(new LinearLayoutManager(getContext()));
        return ChatViewUnica;
    }

    @Override
    public void onStart(){
        super.onStart();

    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imagenc;
        TextView nombrec;


        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenc = itemView.findViewById(R.id.imagen_contacto);
            nombrec = itemView.findViewById(R.id.nombre_contacto);
        }
    }

}
