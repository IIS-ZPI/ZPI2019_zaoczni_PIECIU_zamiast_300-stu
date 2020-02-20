package pl.arsonproject.nbp_currency.ui.currecnyChart

import android.view.View
import android.widget.AdapterView
import androidx.databinding.ObservableDouble
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.arsonproject.nbp_currency.repository.ApiFactory

class CurrencyChartViewModel : ViewModel() {

    val nameFirst = ObservableField<String>()
    val currencyPriceFirst = ObservableDouble()
    val publicationDateFirst = ObservableField<String>()
    val nameSecond = ObservableField<String>()
    val currencyPriceSecond = ObservableDouble()
    val publicationDateSecond = ObservableField<String>()
    val currencyList = ObservableField<List<String>>()
    val errorMessage = MutableLiveData<String>()

    init {
        if (currencyList.get() == null)
            viewModelScope.launch {
                try {
                    var nbpApi = ApiFactory.nbpApi
                    val response = nbpApi.getAllCurrencyAsync("a").await()
                    if (response.isSuccessful()) {
                        var list = arrayListOf<String>()
                        for (curr in response.body()?.get(0)?.rates!!) {
                            list.add(curr.code)
                        }
                        currencyList.set(list)
                    }
                } catch (e: Exception) {
                    errorMessage.value = e.message
                }
            }
    }

    fun onSelectItemFirst(
        parent: AdapterView<*>?,
        view: View?,
        pos: Int,
        id: Long
    ) {
        currencyChangeFirst(parent?.selectedItem.toString())
    }

    private fun currencyChangeFirst(currentCurrency: String) {
        viewModelScope.launch {
            try {
                var nbpApi = ApiFactory.nbpApi
                val response = nbpApi.getCurrencyInfoAsync("a", currentCurrency).await()
                if (response.isSuccessful()) {
                    nameFirst.set(response.body()?.currency)
                    currencyPriceFirst.set(response.body()?.rates?.get(0)?.midPrice!!)
                    publicationDateFirst.set(response.body()?.rates?.get(0)?.effectiveDate!!)
                }
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }

    fun onSelectItemSecond(
        parent: AdapterView<*>?,
        view: View?,
        pos: Int,
        id: Long
    ) {
        currencyChangeSecond(parent?.selectedItem.toString())
    }

    private fun currencyChangeSecond(currentCurrency: String) {
        viewModelScope.launch {
            try {
                var nbpApi = ApiFactory.nbpApi
                val response = nbpApi.getCurrencyInfoAsync("a", currentCurrency).await()
                if (response.isSuccessful()) {
                    nameSecond.set(response.body()?.currency)
                    currencyPriceSecond.set(response.body()?.rates?.get(0)?.midPrice!!)
                    publicationDateSecond.set(response.body()?.rates?.get(0)?.effectiveDate!!)
                }
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }
}