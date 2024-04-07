package com.example.compoundinggrowth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.compoundinggrowth.databinding.FragmentBudgetsBinding
import com.example.compoundinggrowth.ui.MainViewModel

class BudgetsFragment : Fragment() {

    private var _binding: FragmentBudgetsBinding? = null
    private val vm: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBudgetsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textBudgets.text = "this is the budgets fragment"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}