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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compoundinggrowth.R
import com.example.compoundinggrowth.databinding.FragmentBudgetsBinding
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

class BudgetsFragment : Fragment() {

    private var _binding: FragmentBudgetsBinding? = null
    private val vm: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val categories: Array<String> by lazy {
        resources.getStringArray(R.array.categories)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBudgetsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initAdapter(binding.budgetRV)

        vm.transactionList.observe(viewLifecycleOwner) {
            setupBudgetChart(it)
        }

        vm.fetchBudgets {

            // Create fixed set of budgets if they don't exist when fetched
            if (vm.budgetList.value?.isEmpty() == true) {
                for (category in categories) {
                    vm.createBudget(category)
                }
            }

        }

        return root
    }

    private fun initAdapter(rv: RecyclerView) {

        val adapter = BudgetsAdapter(vm) {
            Log.d("OneBudget",
                String.format("OneBudget category %s",
                    it.category))

            val action = BudgetsFragmentDirections.actionBudgetsFragmentToOneBudget(it)
            findNavController().navigate(action)

        }

        vm.budgetList.observe(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }

        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        rv.addItemDecoration(itemDecor)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(rv.context)

    }
    private fun setupBudgetChart(transactions : List<Transaction>) {
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
        xAxis.setDrawAxisLine(false)

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