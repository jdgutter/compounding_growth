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

        binding.txnAmount.text = transaction.amount.toString()
        binding.txnDate.text = transaction.date.toString()
        binding.txnName.text = transaction.name
        binding.txnAccount.text = transaction.account
        binding.txnAccount.text = transaction.account

        // Setup spinner
        binding.categorySpinner.adapter = createAdapterFromResource(R.array.categories)

        val categoryIdx = categories.indexOf(transaction.category)

        if (categoryIdx != -1) {
            binding.categorySpinner.setSelection(categoryIdx)
        } else {
            binding.categorySpinner.setSelection(0)
        }

        setupSpinnerListener(binding.categorySpinner)

        binding.deleteBut.setOnClickListener {
            vm.removeTransaction(transaction)
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

                if (position != 0) {
                    val category = parent.getItemAtPosition(position).toString()
                    transaction.category = category
                }
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