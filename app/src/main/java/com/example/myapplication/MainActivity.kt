package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Entity
data class LinerInequalityRoom(
    @ColumnInfo(name = "result") val result: String,
    @ColumnInfo(name = "value_a") val valueA: String,
    @ColumnInfo(name = "value_b") val valueB: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

@Dao
interface LinerInequalityDao {

    @Query("SELECT * FROM LinerInequalityRoom")
    fun getAll(): LinerInequalityRoom?

    @Insert
    fun insertAll(vararg linerInequalityResult: LinerInequalityRoom)

    @Query("DELETE FROM LinerInequalityRoom")
    fun dropAll()
}

@Database(entities = [LinerInequalityRoom::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun linerInequalityDao(): LinerInequalityDao
}

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
        val result: TypeOfLinerInequality
        when {
            a > 0 -> {
                result = TypeOfLinerInequality.FromMinusBtoAToPlusInfinity
                result.x = -b / a
            }
            a == 0.0 -> {
                result = if (b <= 0) {
                    TypeOfLinerInequality.NotResult
                } else {
                    TypeOfLinerInequality.XBelongsToEverything
                }
            }
            else -> {
                result = TypeOfLinerInequality.FromMinusInfinityToMinusBToA
                result.x = -b / a
            }
        }
        return result
    }
}

@DelicateCoroutinesApi
interface LinerInequalityView {
    fun viewResult(result: TypeOfLinerInequality?, applicationContext: android.content.Context)
    fun showError(error: TypeOfError)
}

@DelicateCoroutinesApi
class Presenter {
    private val model = LinerInequality()
    var linerInequalityView: LinerInequalityView? = null
    private var lastResult: TypeOfLinerInequality? = null
    @DelicateCoroutinesApi
    fun click(a: String, b: String, applicationContext: android.content.Context) {
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
                                linerInequalityView?.viewResult(result, applicationContext)
                            } else linerInequalityView?.showError(TypeOfError.MinValueForB)
                        } else linerInequalityView?.showError(TypeOfError.MaxValueForB)
                    } else linerInequalityView?.showError(TypeOfError.BIncorrectly)
                } else linerInequalityView?.showError(TypeOfError.MinValueForA)
            } else linerInequalityView?.showError(TypeOfError.MaxValueForA)
        } else linerInequalityView?.showError(TypeOfError.AIncorrectly)
    }
    fun afterAttach(applicationContext: android.content.Context) {
        if (lastResult != null)
            linerInequalityView?.viewResult(lastResult!!, applicationContext)
    }
}

@DelicateCoroutinesApi
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

@DelicateCoroutinesApi
class MainActivity : AppCompatActivity(), LinerInequalityView {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textViewResult = findViewById<TextView>(R.id.result)
        val textViewValueA = findViewById<EditText>(R.id.valueA)
        val textViewValueB = findViewById<EditText>(R.id.valueB)


        val presenter = Context.get().getPresenter()
        presenter.linerInequalityView = this
        presenter.afterAttach(applicationContext)

        val linerInequalityDao = connectWithDataBase(applicationContext)

        GlobalScope.launch(Dispatchers.IO) {
            val valuesFromDataBase = linerInequalityDao.getAll()
            if (valuesFromDataBase != null) {
                val result = LinerInequalityRoom(valuesFromDataBase.result,
                                                 valuesFromDataBase.valueA,
                                                 valuesFromDataBase.valueB)
                launch(Dispatchers.Main) {
                    textViewResult.text = result.result
                    textViewValueA.setText(result.valueA)
                    textViewValueB.setText(result.valueB)
                }
            }
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            presenter.click(
                textViewValueA.text.toString(),
                textViewValueB.text.toString(),
                this
            )
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

    private fun connectWithDataBase(applicationContext: android.content.Context): LinerInequalityDao =
        Room.databaseBuilder(applicationContext, AppDatabase::class.java,"linerInequality").build().linerInequalityDao()

    private fun dropAndInsertDate(applicationContext: android.content.Context, textViewResult: TextView, valueA: String, valueB: String) {
        val linerInequalityDao = connectWithDataBase(applicationContext)
        linerInequalityDao.dropAll()
        linerInequalityDao.insertAll(LinerInequalityRoom(textViewResult.text.toString(), valueA, valueB))
    }

    @SuppressLint("SetTextI18n")
    override fun viewResult(result: TypeOfLinerInequality?, applicationContext: android.content.Context) {
        val valueA = findViewById<EditText>(R.id.valueA).text.toString()
        val valueB = findViewById<EditText>(R.id.valueB).text.toString()
        val textViewResult = findViewById<TextView>(R.id.result)
        when (result) {

            TypeOfLinerInequality.FromMinusBtoAToPlusInfinity -> {
                textViewResult.text = getString(R.string.result) + " " + "(${result.x} ; +∞)"

                GlobalScope.launch(Dispatchers.IO) {
                    dropAndInsertDate(applicationContext, textViewResult, valueA, valueB)
                }
            }

            TypeOfLinerInequality.NotResult -> {
                textViewResult.text = getString(R.string.result) + " " + getString(R.string.there_is_one_decision)

                GlobalScope.launch(Dispatchers.IO) {
                    dropAndInsertDate(applicationContext, textViewResult, valueA, valueB)
                }
            }
            TypeOfLinerInequality.XBelongsToEverything -> {
                textViewResult.text = getString(R.string.result) + " " + getString(R.string.x_belongs_to_everything)

                GlobalScope.launch(Dispatchers.IO) {
                    dropAndInsertDate(applicationContext, textViewResult, valueA, valueB)
                }
            }

            TypeOfLinerInequality.FromMinusInfinityToMinusBToA -> {
                textViewResult.text = getString(R.string.result) + " " + "(-∞ ; ${result.x})"

                GlobalScope.launch(Dispatchers.IO) {
                    dropAndInsertDate(applicationContext, textViewResult, valueA, valueB)
                }
            }
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