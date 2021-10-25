package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

enum class TypeOfLinerInequality {
    FromMinusBtoAToPlusInfinity ,
    NotResult ,
    XBelongsToEverything,
    FromMinusInfinityToMinusBToA;
    var x: Double? = null
}
class LinerInequality {
    fun linerInequality(a: Double, b: Double) : TypeOfLinerInequality {
        val res: TypeOfLinerInequality
        when {
            a > 0 -> {
                res = TypeOfLinerInequality.FromMinusBtoAToPlusInfinity
                res.x = -b / a
            }
            a == 0.0 -> {
                res = if (b <= 0) {
                    TypeOfLinerInequality.NotResult
                } else {
                    TypeOfLinerInequality.XBelongsToEverything
                }
            }
            else -> {
                res = TypeOfLinerInequality.FromMinusInfinityToMinusBToA
                res.x = -b / a
            }
        }
        return res
    }
}

class MainActivity : AppCompatActivity() {

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val textView = findViewById<TextView>(R.id.result)
        outState.putString(RESULT, textView.text.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextNumberA = findViewById<EditText>(R.id.a)
        val editTextNumberB = findViewById<EditText>(R.id.b)

        val textViewResult = findViewById<TextView>(R.id.result)
        if (savedInstanceState != null)
            textViewResult.text = savedInstanceState.getString(RESULT)

        val buttonResult = findViewById<Button>(R.id.button)
        val buttonToShare = findViewById<Button>(R.id.buttonToShare)

        buttonResult.setOnClickListener {
            val textA = editTextNumberA.text
            val textB = editTextNumberB.text
            val a = textA.toString().toDoubleOrNull()
            val b = textB.toString().toDoubleOrNull()

           if (a != null) {
                if (a < Double.MAX_VALUE) {
                    if (b != null) {
                       if (b < Double.MAX_VALUE) {
                           val linerInequalityForTest = LinerInequality()
                           when (val res = linerInequalityForTest.linerInequality(a , b)) {
                               TypeOfLinerInequality.FromMinusBtoAToPlusInfinity -> textViewResult.text = getString(R.string.answer_text) + "(${res.x} ; +∞)"
                               TypeOfLinerInequality.NotResult -> textViewResult.text = getString(R.string.there_is_one_decision)
                               TypeOfLinerInequality.XBelongsToEverything -> textViewResult.text = getString(R.string.x_belongs_to_everything)
                               TypeOfLinerInequality.FromMinusInfinityToMinusBToA -> textViewResult.text = getString(R.string.answer_text) + "(-∞ ; ${res.x})"
                           }
                      } else Toast.makeText(this, getString(R.string.MAX_VALUE_for_b), Toast.LENGTH_SHORT).show()
                    } else Toast.makeText(this, getString(R.string.b_incorrectly), Toast.LENGTH_SHORT).show()
                } else Toast.makeText(this, getString(R.string.MAX_VALUE_for_a), Toast.LENGTH_SHORT).show()
            } else Toast.makeText(this, getString(R.string.a_incorrectly), Toast.LENGTH_SHORT).show()
        }
        buttonToShare.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, textViewResult.text.toString())
            intent.type = "text/plain"
            val intentCreateChooser = Intent.createChooser(intent, null)
            startActivity(intentCreateChooser)
        }
    }
    companion object {
        const val RESULT = "RESULT"
    }
}