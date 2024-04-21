package com.example.compoundinggrowth.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.compoundinggrowth.model.Transaction
import com.example.compoundinggrowth.databinding.RowTransactionBinding

class TransactionAdapter (private val viewModel: MainViewModel,
                          private val navigateToOneTxn: (Transaction)->Unit)
        : ListAdapter<Transaction, TransactionAdapter.VH>(Diff()) {


    // ViewHolder pattern holds row binding
    inner class VH(val txnRowBinding : RowTransactionBinding)
        : RecyclerView.ViewHolder(txnRowBinding.root) {
        init {

            // Set on click listener for txnRowBinding
            txnRowBinding.root.setOnClickListener {
                navigateToOneTxn(getItem(bindingAdapterPosition))
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val rowBinding = RowTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)

        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val binding = holder.txnRowBinding

        val list = currentList
        val txn = list[position]

        binding.rowName.text = txn.name
        binding.rowCategory.text = txn.category

        val value = if (txn.stockSymbol != null) {
            txn.amount * txn.stockPriceAtTransaction!!
        } else {
            txn.amount
        }
        
        binding.rowAmount.text = String.format("$%.2f", value)

    }

    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
                    && oldItem.name == newItem.name
                    && oldItem.ownerUid == newItem.ownerUid
                    && oldItem.ownerName == newItem.ownerName
                    && oldItem.uuid == newItem.uuid
                    && oldItem.amount == newItem.amount
                    && oldItem.date == newItem.date
                    && oldItem.category == newItem.category
                    && oldItem.stockSymbol == newItem.stockSymbol
                    && oldItem.stockPriceAtTransaction == newItem.stockPriceAtTransaction
                    && oldItem.timeStamp == newItem.timeStamp
        }
    }
}