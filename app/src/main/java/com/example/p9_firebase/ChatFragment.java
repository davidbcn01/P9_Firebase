package com.example.p9_firebase;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.p9_firebase.databinding.FragmentChatBinding;
import com.example.p9_firebase.databinding.FragmentSignInBinding;
import com.example.p9_firebase.databinding.ViewholderMensajeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private NavController nav;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mdb;
    private List<Mensaje> chat = new ArrayList<>();
    private String COLECCION_MENSAJES = "mensajes";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return (binding = FragmentChatBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nav = Navigation.findNavController(view);
        mAuth = FirebaseAuth.getInstance();
        mdb = FirebaseFirestore.getInstance();

        ChatAdapter chatAdapter = new ChatAdapter();
        binding.chat.setAdapter(chatAdapter);



        binding.enviar.setOnClickListener(v -> {
            String texto = binding.mensaje.getText().toString();
            String fecha = LocalDateTime.now().toString();
            String email = mAuth.getCurrentUser().getEmail();
            String nombre = mAuth.getCurrentUser().getDisplayName();
            String foto = mAuth.getCurrentUser().getPhotoUrl().toString();
            mdb.collection("mensajes").add(new Mensaje(texto, fecha,email,nombre,foto));
            binding.mensaje.setText("");

        });


        binding.adjuntar.setOnClickListener(v -> {
            galeria.launch("image/*");
        });
        mdb.collection("mensajes").orderBy("fecha").addSnapshotListener((value, error) -> {
           chat.clear();
            value.forEach(document ->{
                chat.add(new Mensaje(
                        document.getString("mensaje"),
                document.getString("fecha"),
                document.getString("email"),
                document.getString("nombre"),
                        document.getString("foto"),
                        document.getString("meme")
                ));

            });
            chatAdapter.notifyDataSetChanged();
            binding.chat.scrollToPosition(chat.size()-1);



        });

    }


    class ChatAdapter extends RecyclerView.Adapter<MensajeViewHolder>{

        @NonNull
        @Override
        public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MensajeViewHolder(ViewholderMensajeBinding.inflate(getLayoutInflater(),parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
            Mensaje mensaje = chat.get(position);
            holder.binding.nombre.setText(mensaje.nombre);
            holder.binding.fecha.setText(mensaje.fecha);
            if(mensaje.meme != null){
                Glide.with(requireView()).load(mensaje.meme).into(holder.binding.meme);
                holder.binding.mensaje.setText("");
                holder.binding.mensaje.setVisibility(View.GONE);
                holder.binding.meme.setVisibility(View.VISIBLE);
            }else{
                holder.binding.mensaje.setText(mensaje.mensaje);
                Glide.with(requireView()).load("null").into(holder.binding.meme);
                holder.binding.meme.setVisibility(View.GONE);
                holder.binding.mensaje.setVisibility(View.VISIBLE);

            }
            holder.binding.mensaje.setText(mensaje.mensaje);
            Glide.with(requireView()).load(mensaje.foto).into(holder.binding.foto);


            if(mensaje.email.equals(mAuth.getCurrentUser().getEmail())){
                Log.e("abcd","EY WE");
                holder.binding.root.setGravity(Gravity.END);
            }else {
                Log.e("abcd","AAAAAAAAAAAAAAAAAAAAAAA");
                holder.binding.root.setGravity(Gravity.START);
            }

        }

        @Override
        public int getItemCount() {
            return chat.size();
        }
    }



    static class MensajeViewHolder extends RecyclerView.ViewHolder{
        ViewholderMensajeBinding binding;
        public MensajeViewHolder(@NonNull ViewholderMensajeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    ActivityResultLauncher<String> galeria = registerForActivityResult(new ActivityResultContracts.GetContent(), uri-> {
        FirebaseStorage.getInstance().getReference("imagenes/"+ UUID.randomUUID()).putFile(uri)
                .continueWithTask(task -> task.getResult().getStorage().getDownloadUrl())
                .addOnSuccessListener(url->{
                mdb.collection(COLECCION_MENSAJES).add(new Mensaje("",
                        LocalDateTime.now().toString(),
                        mAuth.getCurrentUser().getDisplayName(),
                        mAuth.getCurrentUser().getEmail(),
                        mAuth.getCurrentUser().getPhotoUrl().toString(),
                        url.toString()
                ));
        });

    });
}