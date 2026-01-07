package com.example.plantwatering.fragments

import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.plantwatering.HivemqViewModel
import com.example.plantwatering.PumpMode
import com.example.plantwatering.databinding.FragmentWaterPumpBinding
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class WaterPumpFragment : Fragment() {

    private var _binding: FragmentWaterPumpBinding? = null
    private val binding get() = _binding!!

    private val hiveViewModel: HivemqViewModel by activityViewModels()

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
        updatePumpUI()
        setupPumpButton()
        binding.btnSaveAutoStateChange.visibility = View.GONE
    }

    private fun updatePumpUI() {
        binding.apply {
            when (hiveViewModel.pumpStatus.value) {
                PumpMode.PUMP_MANUAL -> {
                    swManualMode.isChecked = true
                    btnPumpPower.visibility = View.VISIBLE
                    tvPumpStatus.visibility = View.VISIBLE
                    swScheduleMode.isChecked = false
                    layoutScheduleSettings.visibility = View.GONE
                    swAutoMode.isChecked = false
                    layoutAutoSettings.visibility = View.GONE
                }
                PumpMode.PUMP_SCHEDULE -> {
                    if (hiveViewModel.isPumpOn.value!!) hiveViewModel.toggleManualPumpStatus()
                    swManualMode.isChecked = false
                    btnPumpPower.visibility = View.GONE
                    tvPumpStatus.visibility = View.GONE
                    swScheduleMode.isChecked = true
                    layoutScheduleSettings.visibility = View.VISIBLE
                    swAutoMode.isChecked = false
                    layoutAutoSettings.visibility = View.GONE
                }
                PumpMode.PUMP_AUTO -> {
                    if (hiveViewModel.isPumpOn.value!!) hiveViewModel.toggleManualPumpStatus()
                    swManualMode.isChecked = false
                    btnPumpPower.visibility = View.GONE
                    tvPumpStatus.visibility = View.GONE
                    swScheduleMode.isChecked = false
                    layoutScheduleSettings.visibility = View.GONE
                    swAutoMode.isChecked = true
                    layoutAutoSettings.visibility = View.VISIBLE
                }
            }
        }
        binding.btnSaveAutoStateChange.visibility = View.VISIBLE
    }

    private fun setupPumpButton() {
        binding.btnPumpPower.setOnClickListener {
            hiveViewModel.toggleManualPumpStatus()
            updatePumpStatusText(binding.btnPumpPower, binding.tvPumpStatus)
            binding.btnSaveAutoStateChange.visibility = View.VISIBLE
        }

        binding.swAutoMode.setOnClickListener {
            hiveViewModel.updatePumpStatus(PumpMode.PUMP_AUTO)
            updatePumpUI()
        }

        binding.swScheduleMode.setOnClickListener {
            hiveViewModel.updatePumpStatus(PumpMode.PUMP_SCHEDULE)
            updatePumpUI()
        }

        binding.swManualMode.setOnClickListener {
            hiveViewModel.updatePumpStatus(PumpMode.PUMP_MANUAL)
            updatePumpUI()
        }

        binding.btnSaveAutoStateChange.setOnClickListener {
            when(hiveViewModel.pumpStatus.value) {
                PumpMode.PUMP_SCHEDULE -> {
                    hiveViewModel.publishMessage("settings/mode", "0")
                }
                PumpMode.PUMP_AUTO -> {
                    val progress = binding.sbThreshold.progress
                    hiveViewModel.publishMessage("settings/mode", "1")
                    hiveViewModel.publishMessage("settings/soil_threshold", "$progress")
                }
                PumpMode.PUMP_MANUAL -> {
                    hiveViewModel.publishMessage("settings/mode", "0")
                    updatePumpStatusText(binding.btnPumpPower, binding.tvPumpStatus)
                    hiveViewModel.publishMessage("pump/control",
                        if (hiveViewModel.isPumpOn.value!!) "on" else "off"
                        )
                }
            }
            showToast("Đã cập nhật chế độ bơm")
            binding.btnSaveAutoStateChange.visibility = View.GONE
        }

        binding.btnSelectTime.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, h, m ->
                binding.btnSelectTime.text = String.format("Giờ: %02d:%02d", h, m)
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
            binding.btnSaveAutoStateChange.visibility = View.VISIBLE
        }

        binding.sbThreshold.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                binding.tvThresholdValue.text = "$progress%"
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        binding.btnSaveAutoStateChange.visibility = View.VISIBLE
    }

    private fun updatePumpStatusText(button: MaterialButton, statusText: TextView) {
        if (hiveViewModel.isPumpOn.value!!) {
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

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}