package com.example.plantwatering.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.plantwatering.databinding.FragmentSoilMoistureBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class SoilMoistureFragment : Fragment() {

    private var _binding: FragmentSoilMoistureBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSoilMoistureBinding.inflate(inflater, container, false)
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
        setupMoistureChart(binding.moistureChart)
    }

    // Hardcoded logic
    private fun setupMoistureChart(chart: LineChart) {
        val entries = ArrayList<Entry>()
        entries.add(Entry(0f, 40f))
        entries.add(Entry(1f, 45f))
        entries.add(Entry(2f, 42f))
        entries.add(Entry(3f, 38f))
        entries.add(Entry(4f, 50f))
        entries.add(Entry(5f, 42f))

        val dataSet = LineDataSet(entries, "Soil Moisture (%)")
        dataSet.color = Color.parseColor("#1D431F")
        dataSet.setCircleColor(Color.parseColor("#1D431F"))
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawValues(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        dataSet.setDrawFilled(true)
        dataSet.fillColor = Color.parseColor("#A5D6A7")
        dataSet.fillAlpha = 50

        val lineData = LineData(dataSet)
        chart.data = lineData

        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setTouchEnabled(true)
        chart.setPinchZoom(false)
        chart.xAxis.setDrawGridLines(false)
        chart.axisLeft.setDrawGridLines(false)
        chart.axisRight.isEnabled = false
        chart.animateX(1000)
        chart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}