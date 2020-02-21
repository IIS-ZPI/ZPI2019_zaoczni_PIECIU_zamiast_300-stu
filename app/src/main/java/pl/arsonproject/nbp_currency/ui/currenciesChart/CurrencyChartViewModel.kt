package pl.arsonproject.nbp_currency.ui.currenciesChart

import android.app.Application
import android.app.DatePickerDialog
import android.os.Build
import android.view.View
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.data.Set
import com.anychart.enums.TooltipPositionMode
import com.anychart.graphics.vector.Stroke
import kotlinx.coroutines.launch
import pl.arsonproject.nbp_currency.repository.ApiFactory
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class CurrencyChartViewModel(application: Application) : AndroidViewModel(application) {

    var entryList = arrayListOf<CustomDataEntry>()

    var firstName = ""
    var secondName = ""

    @RequiresApi(Build.VERSION_CODES.O)
    val dateFrom =
        ObservableField<String>(LocalDate.now(ZoneId.systemDefault()).minusDays(7).toString())
    @RequiresApi(Build.VERSION_CODES.O)
    val dateTo = ObservableField<String>(LocalDate.now(ZoneId.systemDefault()).toString())

    val currencyList = ObservableField<List<String>>()
    val errorMessage = MutableLiveData<String>()
    val chart = MutableLiveData<Cartesian>()

    init {
        if (entryList.count() == 0) {
            // todo : #1 Stworzyc nowa warstwe aplikacji - REPOSITORY
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
            setChart()
        }
    }

    fun setChart(): Cartesian {
        chart.value = AnyChart.cartesian()
        chart.value!!.animation(true)

        chart.value!!.crosshair().enabled(true)
        chart.value!!.crosshair()
            .yLabel(true) // TODO ystroke
            .yStroke(null as Stroke?, null, null, null as String?, null as String?)

        chart.value!!.tooltip().positionMode(TooltipPositionMode.POINT)

        chart.value!!.title("Porównanie wybranych walut")

        chart.value!!.yAxis(0)
            .title("Cena")

        chart.value!!.xAxis(0)
            .labels()
            .padding(5.0, 5.0, 5.0, 5.0)
        chart.value!!.xAxis(0)
            .title("Data")

        return chart.value!!
    }

    fun setChartDate(currencyName: String,currencyNameSecond : String) {
        if (entryList == null)
            entryList = arrayListOf<CustomDataEntry>()

        //todo: #1
        viewModelScope.launch {
            try {
                var api = ApiFactory.nbpApi
                var response = api.getCurrencyStatByDateAsync(
                    "a",
                    currencyName,
                    dateFrom.get()!!.toString().format("YYYY-MM-dd"),
                    dateTo.get()!!.toString().format("YYYY-MM-dd")
                ).await()

                var responseSecond = api.getCurrencyStatByDateAsync(
                    "a",
                    currencyNameSecond,
                    dateFrom.get()!!.toString().format("YYYY-MM-dd"),
                    dateTo.get()!!.toString().format("YYYY-MM-dd")
                ).await()

                response.body()?.rates?.forEachIndexed{ index , it ->
                    entryList.add(
                        CustomDataEntry(
                            it.effectiveDate,
                            it.midPrice,
                            responseSecond.body()?.rates?.get(index)?.midPrice ?: 0
                        )
                    )
                }
                var cartesian = chart.value!!

                var set = Set.instantiate()
                set.data(entryList.toList())

                var mapping1 = set.mapAs("{x : 'x', value : 'value'}")
                var mapping2 = set.mapAs("{x : 'x', value : 'value2'}")

                var line1 = cartesian.line(mapping1)
                line1.color("RED")
                line1.name(currencyName)

                var line2 = cartesian.line(mapping2)
                line2.color("GREEN")
                line2.name(currencyNameSecond)

            } catch (e: Exception) {

            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dateToClick(frag: Fragment) {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val picker = DatePickerDialog(
            frag.requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                dateTo.set(
                    LocalDate.of(year, monthOfYear + 1, dayOfMonth).format(
                        DateTimeFormatter.ofPattern("YYYY-MM-dd")
                    )
                )
            }, year, month, day
        )

        picker.datePicker.maxDate = calendar.timeInMillis
        calendar.add(Calendar.YEAR, -1)
        picker.datePicker.minDate = calendar.time.time

        picker.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dateFromClick(frag: Fragment) {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val picker = DatePickerDialog(
            frag.requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                dateFrom.set(
                    LocalDate.of(year, monthOfYear + 1, dayOfMonth).format(
                        DateTimeFormatter.ofPattern("YYYY-MM-dd")
                    )
                )
            }, year, month, day
        )

        picker.datePicker.maxDate = calendar.time.time
        calendar.add(Calendar.YEAR, -1)
        picker.datePicker.minDate = calendar.time.time
        picker.show()
    }

    fun onSelectItemSecond(
        parent: AdapterView<*>?,
        view: View?,
        pos: Int,
        id: Long
    ) {
        secondName = parent?.selectedItem.toString()
        setChartDate(firstName,secondName)
    }

    fun onSelectItemFirst(
        parent: AdapterView<*>?,
        view: View?,
        pos: Int,
        id: Long
    ) {
        firstName = parent?.selectedItem.toString()
        setChartDate(firstName,secondName)
    }

    class CustomDataEntry : ValueDataEntry {
        constructor(x: String, value: Number, value2: Number) : super(x, value) {
            setValue("value2", value2)
        }
    }
}