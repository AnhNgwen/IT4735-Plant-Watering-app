package com.example.plantwatering.fragments

import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.plantwatering.databinding.FragmentWaterPumpBinding
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class WaterPumpFragment : Fragment() {

    private var _binding: FragmentWaterPumpBinding? = null
    private val binding get() = _binding!!

    private var isPumpOn = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaterPumpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        setupUI()
    }

    private fun setupUI() {
        initPumpUI()
        setupPumpButton()
    }

    private fun initPumpUI() {
        binding.apply {
            swScheduleMode.isChecked = false
            layoutScheduleSettings.isEnabled = false
            layoutScheduleSettings.alpha = 0.4f

            swAutoMode.isChecked = false
            layoutAutoSettings.isEnabled = false
            layoutAutoSettings.alpha = 0.4f
        }
    }

    private fun setupPumpButton() {
        binding.btnPumpPower.setOnClickListener {
            isPumpOn = !isPumpOn
            updatePumpUI(binding.btnPumpPower, binding.tvPumpStatus)
        }

        binding.swScheduleMode.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutScheduleSettings.apply {
                isEnabled = isChecked
                alpha = if (isChecked) 1.0f else 0.4f
            }
        }

        binding.btnSelectTime.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, h, m ->
                binding.btnSelectTime.text = String.format("Giờ: %02d:%02d", h, m)
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        binding.swAutoMode.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutAutoSettings.apply {
                isEnabled = isChecked
                alpha = if (isChecked) 1.0f else 0.4f
            }
        }

        binding.sbThreshold.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                binding.tvThresholdValue.text = "$progress%"
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }

    private fun updatePumpUI(button: MaterialButton, statusText: TextView) {
        if (isPumpOn) {
            button.setStrokeColorResource(android.R.color.holo_green_light)
            button.setIconTintResource(android.R.color.holo_green_light)
            statusText.text = "MÁY BƠM ĐANG CHẠY"
            statusText.setTextColor(Color.parseColor("#2ECC71"))
        } else {
            button.setStrokeColorResource(android.R.color.white)
            button.setIconTintResource(android.R.color.white)
            statusText.text = "MÁY BƠM ĐANG TẮT"
            statusText.setTextColor(Color.parseColor("#1D431F"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}