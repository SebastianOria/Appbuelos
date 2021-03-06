package com.example.aplicacion;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clases.Contactos;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragments extends Fragment {
    private ArrayList<Contactos>a = new ArrayList<Contactos>();
    private View ChatViewUnica;
    private RecyclerView ChatLista;
    ArrayList<String> lista = new ArrayList<String>();


    private DatabaseReference databaseReference, contactosRef;

    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static String CurrentUserId = auth.getCurrentUser().getUid();



    public ChatsFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        databaseReference = FirebaseDatabase.getInstance().getReference();
        contactosRef = FirebaseDatabase.getInstance().getReference().child(CurrentUserId);
        ChatViewUnica = inflater.inflate(R.layout.fragment_chats_fragments, container, false);
        ChatLista = (RecyclerView) ChatViewUnica.findViewById(R.id.chat_lista);

        ChatLista.setHasFixedSize(true);
        ChatLista.setLayoutManager(new LinearLayoutManager(getContext()));
        return ChatViewUnica;


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contactos> options =
                new FirebaseRecyclerOptions.Builder<Contactos>().setQuery(databaseReference.child("Usuarios"), Contactos.class).build();
        FirebaseRecyclerAdapter<Contactos, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contactos, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatsViewHolder chatsViewHolder, int i, @NonNull Contactos model) {

                databaseReference.child("Usuarios").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot dataSnapshot : snapshot.getChildren() ) {

                            final String id = dataSnapshot.getKey();


                            boolean a = id.equals(CurrentUserId);
                            boolean b = lista.contains(id);

                                if(!a && !b) {

                                String nombre = snapshot.child(id).child("nombre").getValue(String.class);
                                String Foto = snapshot.child(id).child("Foto").getValue(String.class);;
                                String token = snapshot.child(id).child("token").getValue().toString();

                                Picasso.get()
                                        .load(Foto)
                                        .error(R.drawable.foto)
                                        .placeholder(R.drawable.foto)
                                        .into(chatsViewHolder.profilePic);

                                chatsViewHolder.name.setText(nombre);

                                chatsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getContext(), ChatActivity.class);
                                        intent.putExtra("user_id", id);
                                        intent.putExtra("user_nombre", nombre);
                                        intent.putExtra("user_imagen", Foto);
                                        startActivity(intent);
                                    }
                                });
                            }
                          lista.add(id);

                        }
                        }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
                ChatsViewHolder viewHolder = new ChatsViewHolder(view);
                return viewHolder;
            }
        };


        ChatLista.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profilePic;
        private TextView name;


        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profilePic);
            name = itemView.findViewById(R.id.nombre);
        }
    }

    }

