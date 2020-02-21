package com.czterech.operations
//piotrl3w was here
import com.czterech.Interface.ArithmeticsDiv
import java.util.*

class ArithmeticsDiv : ArithmeticsDiv {
    override fun div(x: Double, y: Double): Double{
        try{
            return x / y
        }catch (e : ArithmeticException){
            throw Exception("Nie mozna tak dzielic, z powodu ${e.message}")
        }
    }
}
