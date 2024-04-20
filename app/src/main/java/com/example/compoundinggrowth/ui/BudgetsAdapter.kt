package com.example.compoundinggrowth.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.compoundinggrowth.databinding.BudgetRowBinding
import com.example.compoundinggrowth.model.Budget

class BudgetsAdapter (private val viewModel: MainViewModel,
                      private val navigateToOneBudget: (Budget)->Unit)
    : ListAdapter<Budget, BudgetsAdapter.VH>(Diff()) {


    // ViewHolder pattern holds row binding
    inner class VH(val rowBinding : BudgetRowBinding)
        : RecyclerView.ViewHolder(rowBinding.root) {
        init {

            // Set on click listener for rowBinding
            rowBinding.root.setOnClickListener {
                navigateToOneBudget(getItem(bindingAdapterPosition))
            }

        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val rowBinding = BudgetRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)

        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val binding = holder.rowBinding

        val list = currentList
        val budgetItem = list[position]

        binding.rowName.text = budgetItem.category
        binding.rowBudget.text = String.format("$%.2f", budgetItem.budgeted)

        val txnList = viewModel.transactionList.value!!.toList()

        var totalUsed : Double = 0.0

        for (item in txnList) {
            if (item.category == budgetItem.category
                && !item.isStockTransaction()) {
                totalUsed += item.amount
            }
        }

        budgetItem.remaining = budgetItem.budgeted - totalUsed

        binding.rowRemaining.text =  String.format("$%.2f", budgetItem.remaining)

    }

    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<Budget>() {
        override fun areItemsTheSame(oldItem: Budget, newItem: Budget): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
        }

        override fun areContentsTheSame(oldItem: Budget, newItem: Budget): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
                    && oldItem.ownerUid == newItem.ownerUid
                    && oldItem.ownerName == newItem.ownerName
                    && oldItem.uuid == newItem.uuid
                    && oldItem.category == newItem.category
                    && oldItem.budgeted == newItem.budgeted
                    && oldItem.timeStamp == newItem.timeStamp
        }
    }
}