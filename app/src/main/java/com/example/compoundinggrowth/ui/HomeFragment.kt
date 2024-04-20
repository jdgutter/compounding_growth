package com.example.compoundinggrowth.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.compoundinggrowth.R
import com.example.compoundinggrowth.databinding.FragmentHomeBinding
import com.example.compoundinggrowth.model.Transaction
import com.example.compoundinggrowth.ui.MainViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val vm: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        vm.transactionList.observe(viewLifecycleOwner) {
            setupNWChart(it)
            setupCashflowChart(it)
        }

        vm.fetchTransactions {

        }

        return root
    }

    private fun setupNWChart(transactions : List<Transaction>) {
        val nwChart = binding.nwChart

        val description = Description()
        description.text = ""
        nwChart.description = description
        nwChart.legend.isEnabled = false
        nwChart.setPinchZoom(false)

        val entries : MutableList<Entry> = mutableListOf()
        val sortedTransactions = transactions.sortedBy { it.date }
        var totalSum = 0.0

        sortedTransactions.forEach {

            if (it.category in Transaction.incomeCategories) {
                totalSum += it.amount
            } else if (it.category in Transaction.expenseCategories) {
                totalSum -= it.amount
            } else if (it.isStockTransaction()) {
                totalSum += it.amount * it.stockPriceAtTransaction!!
            }

            entries.add(Entry(it.date.time.toFloat(), totalSum.toFloat()))
        }

        val dataSet = LineDataSet(entries, "")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK
        dataSet.lineWidth = 2f
        dataSet.setCircleColor(Color.RED)
        dataSet.setDrawValues(false)

        val lineData = LineData(dataSet)
        nwChart.data = lineData

        val xAxis = nwChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = DateFormatter()
        xAxis.setDrawGridLines(false)

        nwChart.axisLeft.isEnabled = true
        nwChart.axisRight.isEnabled = false
        nwChart.description.isEnabled = false

        nwChart.invalidate()
    }

    companion object {
        class DateFormatter : ValueFormatter() {
            private val dateFormat = SimpleDateFormat("MMM yyyy", Locale.US)

            override fun getFormattedValue(value: Float): String {
                return dateFormat.format(Date(value.toLong()))
            }
        }
    }

    private fun setupCashflowChart(transactions : List<Transaction>) {
        val cashFlowChart = binding.cashFlowChart

        cashFlowChart.setDrawBarShadow(false)

        // Set up description and chart info
        val description = Description()
        description.text = ""
        cashFlowChart.description = description
        cashFlowChart.legend.isEnabled = false
        cashFlowChart.setPinchZoom(false)

        // Set up X-axis
        val xAxis = cashFlowChart.xAxis
        xAxis.setDrawAxisLine(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.isEnabled = true


        xAxis.setLabelCount(2)

        val values = arrayOf("Expense", "Income")
        xAxis.valueFormatter = IndexAxisValueFormatter(values)
        xAxis.textSize = 16.0F

        val yRight = cashFlowChart.axisRight
        yRight.setDrawAxisLine(true)
        yRight.setDrawGridLines(false)
        yRight.isEnabled = true

        var income = 0f
        var expense = 0f

        for (item in transactions) {

            if (item.category in Transaction.incomeCategories) {
                income += item.amount.toFloat()
            } else if (item.category in Transaction.expenseCategories) {
                expense += item.amount.toFloat()
            }
        }

        val yLeft = cashFlowChart.axisLeft
        yLeft.isEnabled = false
        yLeft.axisMinimum = 0F
        yLeft.axisMaximum = (income + expense)

        Log.d("setupCashflowChart", "income total ${income}")
        Log.d("setupCashflowChart", "expense total ${expense}")

        val entries : MutableList<BarEntry> = mutableListOf()
        entries.add(BarEntry(0f, expense))
        entries.add(BarEntry(1f, income))

        val dataSet = BarDataSet(entries, "BarDataSet")

        dataSet.setColors(
            Color.RED,
            Color.GREEN
        )

        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 16.0F

        val barData = BarData(dataSet)
        cashFlowChart.data = barData

        cashFlowChart.setExtraOffsets(0F, 0F, 50F, 0F)
        cashFlowChart.setDrawBarShadow(true)
        cashFlowChart.animateY(2000)

        cashFlowChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}