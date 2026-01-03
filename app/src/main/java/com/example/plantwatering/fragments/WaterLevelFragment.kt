package com.example.plantwatering.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.plantwatering.databinding.FragmentWaterLevelBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class WaterLevelFragment : Fragment() {
    private var _binding: FragmentWaterLevelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaterLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        setupUI()
    }

    // Hardcoded logic
    private fun setupUI() {
        updateWaterUI(75, binding.pbWaterTank, binding.tvWaterPercent, binding.tvWaterStatus)
        setupWaterChart(binding.waterChart)
    }

    private fun updateWaterUI(percent: Int, pb: ProgressBar, tvP: TextView, tvS: TextView) {
        pb.progress = percent
        tvP.text = "$percent%"

        if (percent < 20) {
            tvS.text = "Sắp Hết Nước!"
            tvS.setTextColor(Color.RED)
        } else {
            tvS.text = "An Toàn"
            tvS.setTextColor(Color.parseColor("#2ECC71"))
        }
    }

    // Hardcoded logic
    private fun setupWaterChart(chart: LineChart) {
        val entries = ArrayList<Entry>()
        entries.add(Entry(0f, 90f))
        entries.add(Entry(1f, 85f))
        entries.add(Entry(2f, 80f))
        entries.add(Entry(3f, 75f))
        entries.add(Entry(4f, 75f))

        val dataSet = LineDataSet(entries, "Mực nước (%)")
        dataSet.apply {
            color = Color.parseColor("#2196F3")
            setCircleColor(Color.parseColor("#2196F3"))
            lineWidth = 3f
            mode = LineDataSet.Mode.STEPPED
            setDrawFilled(true)
            fillColor = Color.parseColor("#BBDEFB")
            setDrawValues(false)
        }

        chart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            xAxis.setDrawGridLines(false)
            axisRight.isEnabled = false
            animateY(1000)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}