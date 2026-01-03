package com.example.plantwatering.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.plantwatering.databinding.FragmentAirHumidityBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class AirHumidityFragment : Fragment() {

    private var _binding: FragmentAirHumidityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAirHumidityBinding.inflate(inflater, container, false)
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
        binding.tvMainValue.text = "65%"
        binding.pbHumidity.progress = 65
        setupHumidityChart(binding.humidityChart)
    }

    // Hardcoded logic
    private fun setupHumidityChart(chart: LineChart) {
        val entries = ArrayList<Entry>()
        entries.add(Entry(0f, 60f))
        entries.add(Entry(1f, 65f))
        entries.add(Entry(2f, 70f))
        entries.add(Entry(3f, 62f))
        entries.add(Entry(4f, 58f))
        entries.add(Entry(5f, 65f))

        val dataSet = LineDataSet(entries, "Humidity (%)")
        dataSet.apply {
            color = Color.parseColor("#2196F3")
            setCircleColor(Color.parseColor("#2196F3"))
            lineWidth = 2.5f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = Color.parseColor("#BBDEFB")
            fillAlpha = 70
            setDrawValues(false)
        }

        chart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false
            animateX(1000)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}