package com.example.compoundinggrowth.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.compoundinggrowth.R
import com.example.compoundinggrowth.databinding.CreateTransactionBinding
import com.example.compoundinggrowth.model.Transaction
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CreateTransactionFragment : Fragment() {

    private val vm : MainViewModel by activityViewModels()
    private var _binding: CreateTransactionBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val categories: Array<String> by lazy {
        resources.getStringArray(R.array.categories)
    }

    private var name : String = "unknown"
    private var amount : Double = 0.0
    private var date : Date = Date()
    private var category : String = "unknown"
    private var stockSymbol : String? = null
    private var stockPriceAtTransaction : Double? = null
    private var viewer : String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        // Setup Date Button
        binding.dateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val dateString = "${monthOfYear + 1}/$dayOfMonth/$year"
                val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                date = sdf.parse(dateString)!!
                binding.txnDate.text = "Selected Date: $dateString"
            }, year, month, day)

            dpd.show()
        }

        // Setup spinner
        binding.categorySpinner.adapter = createAdapterFromResource(R.array.categories)
        setupSpinnerListener(binding.categorySpinner)

        binding.okButton.setOnClickListener {

            if (isFieldsAreValid()) {

                amount = binding.txnAmount.text.toString().toDouble()

                viewer = binding.viewerEmail.text.toString()

                if (binding.accountInvestmentToggle.isChecked) {
                    name = binding.txnName.text.toString().uppercase()
                    stockSymbol =  vm.stockQuote.value!!.globalQuote.symbol
                    stockPriceAtTransaction = vm.stockQuote.value!!.globalQuote.price.toDouble()
                } else {
                    name = binding.txnName.text.toString()
                    category = categories[binding.categorySpinner.selectedItemPosition]
                }

                vm.createTransaction(name, amount, date, category, viewer, stockSymbol, stockPriceAtTransaction)
                findNavController().popBackStack()
            } else {
                Snackbar.make(it, "Please double check all fields have been set", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.accountInvestmentToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                showCreateInvestment()
            } else {
                showCreateCashTransaction()
            }
        }

    }

    private fun isFieldsAreValid() : Boolean {

        if (binding.accountInvestmentToggle.isChecked) {

            val symbol = binding.txnName.text.toString()

            return (binding.txnAmount.text.toString().toDoubleOrNull() != null
                    && binding.txnDate.text.isNotEmpty()
                    && vm.stockQuote.value != null
                    && vm.stockQuote.value!!.globalQuote.symbol.equals(symbol, ignoreCase = true))

        } else {
            return (binding.txnName.text.isNotEmpty()
                    && binding.txnAmount.text.toString().toDoubleOrNull() != null
                    && binding.txnDate.text.isNotEmpty())
        }
    }

    private fun showCreateInvestment() {
        binding.categorySpinner.visibility = View.GONE
        binding.txnName.hint = "Stock Symbol"

        binding.txnName.addTextChangedListener {
            Log.d("requestQuote", "Request stock price of ${it.toString()}")
            vm.getStockQuote(it.toString())
        }

    }

    private fun showCreateCashTransaction() {
        binding.categorySpinner.visibility = View.VISIBLE
        binding.txnName.hint = "Transaction name"

        binding.txnName.addTextChangedListener {

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
                category = parent.getItemAtPosition(position).toString()
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