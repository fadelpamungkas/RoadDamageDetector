package com.example.roaddamagedetector.ui

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roaddamagedetector.R
import com.example.roaddamagedetector.databinding.FragmentHomeBinding
import com.example.roaddamagedetector.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        val roadAdapter = RoadAdapter()
        val factory = ViewModelFactory.getInstance(context?.applicationContext as Application)
        val viewModel : HomeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        viewModel.getAllData().observe(viewLifecycleOwner, { data ->
            roadAdapter.setData(data)
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = roadAdapter

    }

}