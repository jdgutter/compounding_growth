package com.example.compoundinggrowth.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.compoundinggrowth.R
import com.example.compoundinggrowth.databinding.OneBudgetBinding
import com.example.compoundinggrowth.model.Budget
import com.example.compoundinggrowth.model.Transaction
import com.google.android.material.snackbar.Snackbar

class OneBudgetFragment: Fragment() {

    private val vm : MainViewModel by activityViewModels()
    private var _binding: OneBudgetBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // SafeArgs plugins
    private val args: OneBudgetFragmentArgs by navArgs()
    private lateinit var budget : Budget

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OneBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        budget = args.Budget

        binding.budgetCategory.text = budget.category
        binding.budgetRemaining.text = String.format("Remaining budget: $%.2f", budget.remaining)
        binding.currentBudgetedAmount.setText(budget.budgeted.toString())

        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.okButton.setOnClickListener {
            val newBudget = binding.currentBudgetedAmount.text.toString().toDoubleOrNull()

            if (newBudget != null) {
                budget.budgeted = newBudget
                vm.updateBudget(budget)
                findNavController().popBackStack()
            } else {
                Snackbar.make(it,
                    "Budgeted Amount must be a valid Dollar amount",
                    Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}