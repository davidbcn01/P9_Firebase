package com.example.p9_firebase;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.p9_firebase.databinding.FragmentSignInBinding;
import com.example.p9_firebase.databinding.FragmentSignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;
    private NavController nav;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return (binding = FragmentSignUpBinding.inflate(inflater, container, false)).getRoot();
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            nav = Navigation.findNavController(view);
            binding.emailSignUp.setOnClickListener(v -> {
                String email = binding.email.getText().toString();
                String password = binding.passwd.getText().toString();

                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            nav.navigate(R.id.action_signUpFragment_to_chatFragment);
                        } else{
                            Toast.makeText(requireContext(), task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        }



                     });
            });

        }
    }