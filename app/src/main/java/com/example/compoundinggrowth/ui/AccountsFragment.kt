package com.example.compoundinggrowth.ui

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
import com.example.compoundinggrowth.databinding.FragmentAccountsBinding

class AccountsFragment : Fragment() {

    private var _binding: FragmentAccountsBinding? = null
    private val vm: MainViewModel by activityViewModels()

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

        binding.accountToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                showAccounts()
            } else {
                showInvestments()
            }
        }

        return root
    }

    private fun initAdapter(rv: RecyclerView) {

        val adapter = TransactionAdapter(vm) {
            Log.d("OneTransaction",
                String.format("OneTransaction name %s",
                    it.name))

            val action = TransactionsFragmentDirections.actionTransactionsFragmentToOneTransaction(it)
            findNavController().navigate(action)

        }

        vm.transactionList.observe(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }

        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        rv.addItemDecoration(itemDecor)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(rv.context)

    }

    private fun showAccounts() {
        val chart = binding.accountsChart
    }

    private fun showInvestments() {
        val chart = binding.accountsChart


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}