package com.example.plantwatering.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.plantwatering.databinding.FragmentSoilInfoBinding

class SoilInfoFragment : Fragment() {

    private var _binding: FragmentSoilInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSoilInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.placeholderButton.setOnClickListener {
            val action = SoilInfoFragmentDirections.actionSoilInfoFragmentToWaterPumpFragment()
                this.findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}