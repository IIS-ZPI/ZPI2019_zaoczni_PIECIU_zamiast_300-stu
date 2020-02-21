package pl.arsonproject.nbp_currency.ui.currenciesChart

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_currency_chart.*
import pl.arsonproject.nbp_currency.R
import pl.arsonproject.nbp_currency.databinding.FragmentCurrencyChartBinding


class CurrencyChartFragment : Fragment() {

    private lateinit var viewModel: CurrencyChartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var binding = DataBindingUtil.inflate<FragmentCurrencyChartBinding>(
            inflater,
            R.layout.fragment_currency_chart,
            container,
            false
        )
        viewModel = ViewModelProviders.of(this).get(CurrencyChartViewModel::class.java)
        binding.vm = viewModel
        binding.handler = this

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
    }

    private fun setUI() {
        viewModel.chart.observe(this, Observer {
            currenciesChart.setChart(it)
        })
    }
}
