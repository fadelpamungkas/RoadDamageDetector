package com.example.roaddamagedetector.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.roaddamagedetector.R
import com.example.roaddamagedetector.data.local.RoadDataEntity
import com.example.roaddamagedetector.data.local.UserEntity
import com.example.roaddamagedetector.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProfileBinding.bind(view)

        val user = firebaseAuth.currentUser
        if (user != null) {
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener{listener ->
                    if (listener != null) {
                        Log.d("DocumentChange","${listener.id}=>${listener.data}")
                        binding.dtTvName.text = listener.data?.get("name").toString()
                        binding.dtTvEmail.text = listener.data?.get("email").toString()
                    }
                }
        }
    }
}