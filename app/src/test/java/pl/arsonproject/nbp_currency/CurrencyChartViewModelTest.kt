package pl.arsonproject.nbp_currency

import com.google.gson.GsonBuilder
import okhttp3.*

import org.junit.Test

import org.junit.Assert.*
import pl.arsonproject.nbp_currency.ui.currenciesComp.CurrenciesCompViewModel


class CurrencyChartViewModelTest {
    val curencyCompViewModel = CurrenciesCompViewModel()

    /**
     *The testing method getCurrencyList() return true test if variable from class CurrencyChartViewModel is null
     * We should test it because code in init function (class->CurrencyChartViewModel) will start, when currencyList
     * will be null.
     **/
    @Test
    fun getCurrencyList() {
        assertNull(curencyCompViewModel.currencyList.get())
    }
    /***
     * testApi() is the method for testing request from api.nbp.pl
     * You can set variables date and currency and compare result from api vs results from application
     *
     * We' ve got 2 assertions for test call. First return false test if response is null, however second return true
     * if your response isSuccessful. It's boolean method which return code >= 200 && code < 300.
     */
    @Test
    fun testApi() {
        val client = OkHttpClient()
        val currency = "EUR"
        val date="2020-02-19"
        val request = Request.Builder()
            .url("http://api.nbp.pl/api/exchangerates/rates/a/"+currency+"/"+date+"/?format=json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                assertFalse(response.body() == null)
            }
            assertTrue(response.isSuccessful)
            if(response.isSuccessful) {
                val body = response?.body()?.string()
                val gson = GsonBuilder().create()
                val jsonResponse = gson.toJsonTree(body)
                println(jsonResponse)

            }
        }
    }
}