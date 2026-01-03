package com.example.plantwatering.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.plantwatering.databinding.FragmentAirTemperatureBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class AirTemperatureFragment : Fragment() {
    private var _binding: FragmentAirTemperatureBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAirTemperatureBinding.inflate(inflater, container, false)
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
        binding.tvMainValue.text = "28°C"
        binding.pbTempColumn.progress = 28
        setupTempChart(binding.tempChart)
    }

    // Hardcoded logic
    private fun setupTempChart(chart: LineChart) {
        val entries = ArrayList<Entry>()
        entries.add(Entry(8f, 24f))
        entries.add(Entry(10f, 27f))
        entries.add(Entry(12f, 31f))
        entries.add(Entry(14f, 32f))
        entries.add(Entry(16f, 29f))
        entries.add(Entry(18f, 26f))

        val dataSet = LineDataSet(entries, "Temperature (°C)")
        dataSet.apply {
            color = Color.parseColor("#FF5252")
            setCircleColor(Color.parseColor("#FF5252"))
            lineWidth = 2.5f
            circleRadius = 4f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER

            setDrawFilled(true)
            fillColor = Color.parseColor("#FFCDD2")
            fillAlpha = 60
        }

        chart.data = LineData(dataSet)
        chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(false)

            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false

            animateX(1200)
            invalidate()   
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}