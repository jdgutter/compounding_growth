package com.example.compoundinggrowth.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.compoundinggrowth.databinding.FragmentHomeBinding
import com.example.compoundinggrowth.model.Transaction
import com.example.compoundinggrowth.ui.MainViewModel
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.time.LocalDate
import java.util.Date
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

        val textView: TextView = binding.textHome
        textView.text = "This is the Home fragment"
        val symbol = "GOOG"

        Log.d("requestQuote", "Request stock price of $symbol")
        //vm.getStockQuote(symbol)

        vm.observeStockQuote().observe(viewLifecycleOwner) {

            if (it?.globalQuote != null) {
                binding.stockInfo.text =
                    "Stock price of ${it.globalQuote.symbol} = ${it.globalQuote.price}"
            } else {
                binding.stockInfo.text = "response is null!"
            }
        }


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

        val entries : MutableList<Entry> = mutableListOf()

        for (item in transactions) {
            val entry = Entry(item.date.time.toFloat(), item.amount.toFloat())
            entries.add(entry)
        }

        val dataSet = LineDataSet(entries, "Label")
        dataSet.color = Color.CYAN
        dataSet.valueTextColor = Color.RED

        val lineData = LineData(dataSet)
        nwChart.data = lineData
        nwChart.invalidate()
    }

    private fun setupCashflowChart(transactions : List<Transaction>) {
        val cashFlowChart = binding.cashFlowChart

        var income = 0f
        var expense = 0f

        for (item in transactions) {

            if (item.category in Transaction.incomeCategories) {
                income += item.amount.toFloat()
            } else if (item.category in Transaction.expenseCategories) {
                expense += item.amount.toFloat()
            }
        }

        val entries : MutableList<BarEntry> = mutableListOf()
        entries.add(BarEntry(0f, income))
        entries.add(BarEntry(1f, expense))


        val dataSet = BarDataSet(entries, "BarDataSet")

        dataSet.color = Color.GREEN
        dataSet.valueTextColor = Color.YELLOW

        val barData = BarData(dataSet)
        cashFlowChart.data = barData

        cashFlowChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}