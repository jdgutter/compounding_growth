package com.example.compoundinggrowth.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.compoundinggrowth.databinding.FragmentHomeBinding
import com.example.compoundinggrowth.ui.MainViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val vm: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        textView.text = "This is the Home fragment"
        val symbol = "GOOG"

        Log.d("requestQuote", "Request stock price of $symbol")
        //vm.getStockQuote(symbol)

        vm.observeStockQuote().observe(viewLifecycleOwner) {

            if (it?.globalQuote != null) {
                binding.stockInfo.text =
                    "Stock price of ${it.globalQuote.symbol} = ${it.globalQuote.price}"
            } else {
                binding.stockInfo.text = "response is null!"
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}