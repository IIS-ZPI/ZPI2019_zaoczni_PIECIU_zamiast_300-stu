package pl.arsonproject.nbp_currency.ui.currenciesComp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import pl.arsonproject.nbp_currency.R
import pl.arsonproject.nbp_currency.databinding.FragmentCurrenciesCompBinding

class CurrenciesCompFragment : Fragment() {

    private lateinit var currenciesCompViewModel: CurrenciesCompViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currenciesCompViewModel =
            ViewModelProviders.of(this).get(CurrenciesCompViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentCurrenciesCompBinding>(
            inflater,
            R.layout.fragment_currencies_comp,
            container,
            false
        )

        binding.vm = currenciesCompViewModel
        setUI()
        return binding.root
    }

    private fun setUI() {
        currenciesCompViewModel.errorMessage.observe(this, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
    }
}