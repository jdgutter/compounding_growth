package com.example.compoundinggrowth.ui

import android.graphics.Color
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
import com.example.compoundinggrowth.databinding.OneTransactionBinding
import com.example.compoundinggrowth.model.Transaction

class OneTransactionFragment: Fragment() {

    private val vm : MainViewModel by activityViewModels()
    private var _binding: OneTransactionBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // SafeArgs plugins
    private val args: OneTransactionFragmentArgs by navArgs()
    private lateinit var transaction : Transaction

    private val categories: Array<String> by lazy {
        resources.getStringArray(R.array.categories)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OneTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        transaction = args.Transaction

        // Setup visible/invisible views based on isStockTransaction()
        if (transaction.isStockTransaction()) {
            binding.currentStockPrice.visibility = View.VISIBLE
            binding.categorySpinner.visibility = View.GONE
            vm.getStockQuote(transaction.stockSymbol!!)
        } else {
            binding.currentStockPrice.visibility = View.GONE
            binding.categorySpinner.visibility = View.VISIBLE
        }

        // Get value based on isStockTransaction()
        val value = if (transaction.isStockTransaction()) {
            transaction.amount * transaction.stockPriceAtTransaction!!
        } else {
            transaction.amount
        }

        // Set transaction info into view
        binding.txnAmount.text = String.format("$%.2f", value)
        binding.txnDate.text = transaction.date.toString()
        binding.txnName.text = transaction.name
        binding.owner.text = "Owner: ${transaction.ownerName}"

        vm.stockQuote.observe(viewLifecycleOwner) {
            val price = it.globalQuote.price.toDouble()
            binding.currentStockPrice.text = String.format("$%.2f", price)
        }

        // Setup spinner
        binding.categorySpinner.adapter = createAdapterFromResource(R.array.categories)
        val categoryIdx = categories.indexOf(transaction.category)

        // Set spinner initial position
        if (categoryIdx != -1) {
            binding.categorySpinner.setSelection(categoryIdx)
        } else {
            binding.categorySpinner.setSelection(0)
        }

        setupSpinnerListener(binding.categorySpinner)

        // Setup Delete Button
        binding.deleteBut.setOnClickListener {

            if (transaction.ownerUid == vm.getCurrentAuthUser().uid) {
                vm.removeTransaction(transaction)
            } else {
                vm.removeViewer(transaction)
            }

            findNavController().popBackStack()
        }

    }

    private fun createAdapterFromResource(arrayResource: Int):
            ArrayAdapter<CharSequence> {
        val adapter = ArrayAdapter.createFromResource(requireContext(),
            arrayResource,
            android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

    private fun setupSpinnerListener(spinner: Spinner) {

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val category = parent.getItemAtPosition(position).toString()
                transaction.category = category
                vm.updateTransaction(transaction)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}