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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compoundinggrowth.api.AlphaVantageApi
import com.example.compoundinggrowth.databinding.FragmentAccountsBinding
import com.example.compoundinggrowth.model.Transaction
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat

class AccountsFragment : Fragment() {

    private var _binding: FragmentAccountsBinding? = null
    private val vm: MainViewModel by activityViewModels()
    private lateinit var adapter : TransactionAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.accountToggle.isChecked = vm.accountsInvestmentsToggle

        initAdapter(binding.accountsRV)

        // Initial chart
        if (binding.accountToggle.isChecked) {
            showInvestments()
        } else {
            showAccounts()
        }

        binding.accountToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            vm.accountsInvestmentsToggle = isChecked

            if (isChecked) {
                showInvestments()
            } else {
                showAccounts()
            }
        }

        return root
    }

    private fun initAdapter(rv: RecyclerView) {

        adapter = TransactionAdapter(vm) {
            Log.d("OneTransaction",
                String.format("OneTransaction name %s",
                    it.name))

            val action = AccountsFragmentDirections.actionAccountsFragmentToOneTransaction(it)
            findNavController().navigate(action)

        }

        vm.transactionList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.filter {
                if (vm.accountsInvestmentsToggle) {
                    it.stockSymbol != null
                } else {
                    it.stockSymbol == null
                }}.toMutableList())
        }

        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        rv.addItemDecoration(itemDecor)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(rv.context)

    }

    private fun showAccounts() {
        val accountsList = vm.transactionList.value!!.filter { it.stockSymbol == null }
        setupNWChart(accountsList)
        adapter.submitList(accountsList)
    }

    private fun setupNWChart(transactions : List<Transaction>) {
        val nwChart = binding.accountsChart

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
        xAxis.valueFormatter = HomeFragment.Companion.DateFormatter()
        xAxis.setDrawGridLines(false)

        nwChart.axisLeft.isEnabled = true
        nwChart.axisRight.isEnabled = false
        nwChart.description.isEnabled = false

        nwChart.invalidate()
    }

    private fun showInvestments() {
        val investmentList = vm.transactionList.value!!.filter { it.isStockTransaction() }
        vm.getDailyStockPrices(investmentList.map { it.stockSymbol!! })

        vm.dailyStockPrices.observe(viewLifecycleOwner) {
            setupInvestmentChart(investmentList, it)
        }

        adapter.submitList(investmentList)
    }

    private fun setupInvestmentChart(transactions : List<Transaction>,
                                     dailyQuotes : List<AlphaVantageApi.DailyQuoteResponse>) {
        val chart = binding.accountsChart

        val description = Description()
        description.text = ""
        chart.description = description
        chart.legend.isEnabled = false
        chart.setPinchZoom(false)

        val entries : List<Entry> = calculateInvestmentValues(transactions, dailyQuotes)

        val dataSet = LineDataSet(entries, "")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK
        dataSet.lineWidth = 2f
        dataSet.setCircleColor(Color.RED)
        dataSet.setDrawValues(false)

        val lineData = LineData(dataSet)
        chart.data = lineData

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = HomeFragment.Companion.DateFormatter()
        xAxis.setDrawGridLines(false)

        chart.axisLeft.isEnabled = true
        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false

        chart.invalidate()
    }

    private fun calculateInvestmentValues(transactions: List<Transaction>,
                                          dailyQuotes: List<AlphaVantageApi.DailyQuoteResponse>): List<Entry> {
        val symbolToDailyData = dailyQuotes.associate { it.metaData.symbol to it.timeSeriesDaily }
        val entries = mutableListOf<Entry>()

        // Group and sort transactions by date
        val groupedTransactions = transactions.groupBy { it.date }
        val sortedDates = groupedTransactions.keys.sorted()

        // Calculate the portfolio value over time
        val portfolio = mutableMapOf<String, Double>() // Symbol to number of shares

        sortedDates.forEach { date ->

            groupedTransactions[date]?.forEach { transaction ->
                val shares = portfolio.getOrDefault(transaction.stockSymbol, 0.0)
                val symbol = transaction.stockSymbol!!
                portfolio[symbol] = shares + transaction.amount
            }

            var dailyValue = 0.0
            portfolio.forEach { (symbol, shares) ->
                symbolToDailyData[symbol]?.let { dailyMap ->
                    val dateString = SimpleDateFormat("yyyy-MM-dd").format(date)
                    dailyMap[dateString]?.let {
                        dailyValue += shares * it.close.toDouble()
                    }
                }
            }

            entries.add(Entry(date.time.toFloat(), dailyValue.toFloat()))
        }

        return entries
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}