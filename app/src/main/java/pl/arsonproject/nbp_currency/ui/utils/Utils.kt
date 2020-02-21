package pl.arsonproject.nbp_currency.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.getDateString() = this.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")).toString()