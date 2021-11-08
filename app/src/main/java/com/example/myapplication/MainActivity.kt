package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

enum class TypeOfLinerInequality {
    FromMinusBtoAToPlusInfinity,
    NotResult,
    XBelongsToEverything,
    FromMinusInfinityToMinusBToA;
    var x: Double? = null
}

enum class TypeOfError {
    MinValueForB,
    MaxValueForB,
    BIncorrectly,
    MinValueForA,
    MaxValueForA,
    AIncorrectly;
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

interface LinerInequalityView {
    fun viewResult(result: TypeOfLinerInequality?)
    fun showError(error: TypeOfError)
}

class Presenter {
    private val model = LinerInequality()
    var linerInequalityView: LinerInequalityView? = null
    private var lastResult: TypeOfLinerInequality? = null
    fun click(a: String, b: String) {
        val mainA = a.toDoubleOrNull()
        val mainB = b.toDoubleOrNull()
        if (mainA != null) {
            if (mainA < Double.MAX_VALUE) {
                if (mainA > -Double.MAX_VALUE) {
                    if (mainB != null) {
                        if (mainB < Double.MAX_VALUE) {
                            if (mainB > -Double.MAX_VALUE) {
                                val result = model.linerInequality(mainA, mainB)
                                lastResult = result
                                linerInequalityView?.viewResult(lastResult)
                            } else linerInequalityView?.showError(TypeOfError.MinValueForB)
                        } else linerInequalityView?.showError(TypeOfError.MaxValueForB)
                    } else linerInequalityView?.showError(TypeOfError.BIncorrectly)
                } else linerInequalityView?.showError(TypeOfError.MinValueForA)
            } else linerInequalityView?.showError(TypeOfError.MaxValueForA)
        } else linerInequalityView?.showError(TypeOfError.AIncorrectly)
    }
    fun afterAttach() {
        if (lastResult != null)
            linerInequalityView?.viewResult(lastResult!!)
    }
}

class Context private constructor() {
    private val presenter = Presenter()
    fun getPresenter(): Presenter {
        return presenter
    }
    companion object {
        private var context: Context? = null
        fun get() : Context {
            if (context == null)
                context = Context()
            return context!!
        }
    }
}

class MainActivity : AppCompatActivity(), LinerInequalityView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val presenter = Context.get().getPresenter()
        presenter.linerInequalityView = this
        presenter.afterAttach()

        findViewById<Button>(R.id.button).setOnClickListener {
            presenter.click(findViewById<EditText>(R.id.a).text.toString(), findViewById<EditText>(R.id.b).text.toString())
        }

        findViewById<Button>(R.id.buttonToShare).setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, findViewById<TextView>(R.id.result).text.toString())
            intent.type = "text/plain"
            val intentCreateChooser = Intent.createChooser(intent, null)
            startActivity(intentCreateChooser)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun viewResult(result: TypeOfLinerInequality?) {
        when (result) {
            TypeOfLinerInequality.FromMinusBtoAToPlusInfinity -> findViewById<TextView>(R.id.result).text = getString(R.string.answer_text) + "(${result.x} ; +∞)"
            TypeOfLinerInequality.NotResult -> findViewById<TextView>(R.id.result).text = getString(R.string.there_is_one_decision)
            TypeOfLinerInequality.XBelongsToEverything -> findViewById<TextView>(R.id.result).text = getString(R.string.x_belongs_to_everything)
            TypeOfLinerInequality.FromMinusInfinityToMinusBToA -> findViewById<TextView>(R.id.result).text = getString(R.string.answer_text) + "(-∞ ; ${result.x})"
        }
    }

    override fun showError(error: TypeOfError) {
       when(error) {
           TypeOfError.MaxValueForA -> Toast.makeText(this, getString(R.string.MAX_VALUE_for_a), Toast.LENGTH_SHORT).show()
           TypeOfError.MinValueForA -> Toast.makeText(this, getString(R.string.MIN_VALUE_for_a), Toast.LENGTH_SHORT).show()
           TypeOfError.MaxValueForB -> Toast.makeText(this, getString(R.string.MAX_VALUE_for_b), Toast.LENGTH_SHORT).show()
           TypeOfError.MinValueForB -> Toast.makeText(this, getString(R.string.MIN_VALUE_for_b), Toast.LENGTH_SHORT).show()
           TypeOfError.BIncorrectly -> Toast.makeText(this, getString(R.string.b_incorrectly), Toast.LENGTH_SHORT).show()
           TypeOfError.AIncorrectly -> Toast.makeText(this, getString(R.string.a_incorrectly), Toast.LENGTH_SHORT).show()
       }
    }
}