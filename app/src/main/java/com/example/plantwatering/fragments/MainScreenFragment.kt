package com.example.plantwatering.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.plantwatering.HivemqViewModel
import com.example.plantwatering.databinding.FragmentMainScreenBinding

class MainScreenFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HivemqViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSend.setOnClickListener {
            sendMqttMessage()
        }

        binding.toAirTempButton.setOnClickListener {
            val action = MainScreenFragmentDirections.actionMainScreenFragmentToAirTemperatureFragment()
            this.findNavController().navigate(action)
        }

        binding.toAirHumidButton.setOnClickListener {
            val action = MainScreenFragmentDirections.actionMainScreenFragmentToAirHumidityFragment()
            this.findNavController().navigate(action)
        }

        binding.toSoilMoistButton.setOnClickListener {
            val action = MainScreenFragmentDirections.actionMainScreenFragmentToSoilMoistureFragment()
            this.findNavController().navigate(action)
        }

        binding.toWaterPumpButton.setOnClickListener {
            val action = MainScreenFragmentDirections.actionMainScreenFragmentToWaterPumpFragment()
            this.findNavController().navigate(action)
        }

        binding.toWaterLevelButton.setOnClickListener {
            val action = MainScreenFragmentDirections.actionMainScreenFragmentToWaterLevelFragment()
            this.findNavController().navigate(action)
        }
    }

    private fun sendMqttMessage() {
        viewModel.publishMessage(
            topic = "android/test",
            message = "Hello from Android"
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}