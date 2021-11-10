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
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

@Dao
interface LinerInequalityDao {

    @Query("SELECT * FROM LinerInequalityRoom")
    fun getAll(): LinerInequalityRoom

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

@DelicateCoroutinesApi
interface LinerInequalityView {
    fun viewResult(result: TypeOfLinerInequality?, mainActivity: MainActivity)
    fun showError(error: TypeOfError)
}

@DelicateCoroutinesApi
class Presenter {
    private val model = LinerInequality()
    var linerInequalityView: LinerInequalityView? = null
    private var lastResult: TypeOfLinerInequality? = null
    @DelicateCoroutinesApi
    fun click(a: String, b: String, mainActivity: MainActivity) {
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
                                linerInequalityView?.viewResult(result, mainActivity)
                            } else linerInequalityView?.showError(TypeOfError.MinValueForB)
                        } else linerInequalityView?.showError(TypeOfError.MaxValueForB)
                    } else linerInequalityView?.showError(TypeOfError.BIncorrectly)
                } else linerInequalityView?.showError(TypeOfError.MinValueForA)
            } else linerInequalityView?.showError(TypeOfError.MaxValueForA)
        } else linerInequalityView?.showError(TypeOfError.AIncorrectly)
    }
    fun afterAttach(mainActivity: MainActivity) {
        if (lastResult != null)
            linerInequalityView?.viewResult(lastResult!!, mainActivity)
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

        val presenter = Context.get().getPresenter()
        presenter.linerInequalityView = this
        presenter.afterAttach(this)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java,"linerInequality").build()
        val linerInequalityDao = db.linerInequalityDao()

        GlobalScope.launch(Dispatchers.IO) {
            val result = LinerInequalityRoom(linerInequalityDao.getAll().result)
            launch(Dispatchers.Main) {
                findViewById<TextView>(R.id.result).text = result.result
            }
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            presenter.click(findViewById<EditText>(R.id.a).text.toString(), findViewById<EditText>(R.id.b).text.toString(), this)
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
    override fun viewResult(result: TypeOfLinerInequality?, mainActivity: MainActivity) {
        when (result) {

            TypeOfLinerInequality.FromMinusBtoAToPlusInfinity -> {

                val resultFromMinusBtoAToPlusInfinity = getString(R.string.answer_text) + " " + "(${result.x} ; +∞)"
                findViewById<TextView>(R.id.result).text = resultFromMinusBtoAToPlusInfinity

                val db = Room.databaseBuilder(mainActivity, AppDatabase::class.java,"linerInequality").build()
                val linerInequalityDao = db.linerInequalityDao()

                GlobalScope.launch(Dispatchers.IO) {
                    linerInequalityDao.dropAll()
                    linerInequalityDao.insertAll(LinerInequalityRoom(resultFromMinusBtoAToPlusInfinity))
                }
            }

            TypeOfLinerInequality.NotResult -> {

                val resultNotResult =  getString(R.string.answer_text) + " " + getString(R.string.there_is_one_decision)
                findViewById<TextView>(R.id.result).text = resultNotResult
                val db = Room.databaseBuilder(mainActivity, AppDatabase::class.java,"linerInequality").build()
                val linerInequalityDao = db.linerInequalityDao()

                GlobalScope.launch(Dispatchers.IO) {
                    linerInequalityDao.dropAll()
                    linerInequalityDao.insertAll(LinerInequalityRoom(resultNotResult))
                }
            }
            TypeOfLinerInequality.XBelongsToEverything -> {

                val resultXBelongsToEverything = getString(R.string.answer_text) + " " + getString(R.string.x_belongs_to_everything)
                findViewById<TextView>(R.id.result).text = resultXBelongsToEverything
                val db = Room.databaseBuilder(mainActivity, AppDatabase::class.java,"linerInequality").build()
                val linerInequalityDao = db.linerInequalityDao()

                GlobalScope.launch(Dispatchers.IO) {
                    linerInequalityDao.dropAll()
                    linerInequalityDao.insertAll(LinerInequalityRoom(resultXBelongsToEverything))
                }
            }

            TypeOfLinerInequality.FromMinusInfinityToMinusBToA -> {

                val resultFromMinusInfinityToMinusBToA = getString(R.string.answer_text) + " " + "(-∞ ; ${result.x})"
                findViewById<TextView>(R.id.result).text = resultFromMinusInfinityToMinusBToA

                val db = Room.databaseBuilder(mainActivity, AppDatabase::class.java,"linerInequality").build()
                val linerInequalityDao = db.linerInequalityDao()

                GlobalScope.launch(Dispatchers.IO) {
                    linerInequalityDao.dropAll()
                    linerInequalityDao.insertAll(LinerInequalityRoom(resultFromMinusInfinityToMinusBToA))
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