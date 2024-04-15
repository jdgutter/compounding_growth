package com.example.compoundinggrowth.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.compoundinggrowth.databinding.BudgetRowBinding
import com.example.compoundinggrowth.model.Budget
import com.example.compoundinggrowth.model.Transaction

class BudgetsAdapter (private val viewModel: MainViewModel)
    : ListAdapter<Budget, BudgetsAdapter.VH>(Diff()) {


    // ViewHolder pattern holds row binding
    inner class VH(val rowBinding : BudgetRowBinding)
        : RecyclerView.ViewHolder(rowBinding.root) {

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
        binding.rowBudget.text = budgetItem.budgeted.toString()
        binding.rowRemaining.text = budgetItem.remaining.toString()

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