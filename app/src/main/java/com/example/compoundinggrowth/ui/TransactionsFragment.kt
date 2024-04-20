package com.example.compoundinggrowth.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compoundinggrowth.R
import com.example.compoundinggrowth.databinding.FragmentTransactionsBinding
import com.example.compoundinggrowth.ui.MainViewModel
import java.util.Date
import kotlin.random.Random

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val vm: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initAdapter(binding.txnRV)
        initSearchBar(binding.searchTV)

        // Setup createTxnButton
        binding.createTxnButton.setOnClickListener {
            findNavController().navigate(R.id.create_transaction)
        }

        vm.fetchTransactions {

        }

        return root
    }

    // Set up the adapter and recycler view
    private fun initAdapter(rv: RecyclerView) {
        val adapter = TransactionAdapter(vm) {
            Log.d("OneTransaction",
                String.format("OneTransaction name %s",
                    it.name))

            val action = TransactionsFragmentDirections.actionTransactionsFragmentToOneTransaction(it)
            findNavController().navigate(action)

        }

        vm.searchTransactions.observe(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }

        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        rv.addItemDecoration(itemDecor)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(rv.context)

    }

    private fun initSearchBar(editTV : EditText) {

        editTV.addTextChangedListener { editable ->

            if (editable != null
                && editable.isEmpty()) {
                hideKeyboard()
            }

            vm.setSearchTerm(editable.toString())
        }
    }

    private fun hideKeyboard() {
        val view = activity?.currentFocus
        view?.let {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}