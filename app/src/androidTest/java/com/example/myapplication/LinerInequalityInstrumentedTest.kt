package com.example.myapplication

import android.app.Activity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LinerInequalityInstrumentedTest {
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.myapplication", appContext.packageName)

    }
    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
    @Test
    fun test1() {
        onView(withId(R.id.a)).perform(typeText("1"))
        onView(withId(R.id.b)).perform(typeText("0"))
        onView(withId(R.id.button)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("Ответ:(-0.0 ; +∞)")))
    }
    @Test
    fun test2() {
        onView(withId(R.id.a)).perform(typeText("0"))
        onView(withId(R.id.b)).perform(typeText("1"))
        onView(withId(R.id.button)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("Ответ:x ∈ R")))
    }
    @Test
    fun test3() {
        onView(withId(R.id.a)).perform(typeText("1"))
        onView(withId(R.id.b)).perform(typeText("1"))
        onView(withId(R.id.button)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("Ответ:(-1.0 ; +∞)")))
    }
    @Test
    fun test4() {
        onView(withId(R.id.a)).perform(typeText("10000"))
        onView(withId(R.id.b)).perform(typeText("10"))
        onView(withId(R.id.button)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("Ответ:(-0.001 ; +∞)")))
    }
    @Test
    fun test5() {
        onView(withId(R.id.a)).perform(typeText("123"))
        onView(withId(R.id.b)).perform(typeText("456"))
        onView(withId(R.id.button)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("Ответ:(-3.707317073170732 ; +∞)")))
    }
    @Test
    fun test6() {
        onView(withId(R.id.a)).perform(typeText("-10"))
        onView(withId(R.id.b)).perform(typeText("9"))
        onView(withId(R.id.button)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("Ответ:(-∞ ; 0.9)")))
    }
    @Test
    fun test7() {
        onView(withId(R.id.a)).perform(typeText("0"))
        onView(withId(R.id.b)).perform(typeText("0"))
        onView(withId(R.id.button)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("Нет решения")))
    }
    @Test
    fun test8() {
        onView(withId(R.id.button)).perform(click())
        var activity : Activity ?= null
        activityScenarioRule.scenario.onActivity { activity = it }
        onView(withText(R.string.a_incorrectly)).inRoot(
            RootMatchers.withDecorView(
                CoreMatchers.not(
                    CoreMatchers. `is`(
                        activity!!.window.decorView
                    )
                )
            )
        ).check(
            matches(
                ViewMatchers.isDisplayed()
            )
        )
    }
    @Test
    fun test9() {
        onView(withId(R.id.a)).perform(typeText("0"))
        onView(withId(R.id.button)).perform(click())
        var activity : Activity ?= null
        activityScenarioRule.scenario.onActivity { activity = it }
        onView(withText(R.string.b_incorrectly)).inRoot(
            RootMatchers.withDecorView(
                CoreMatchers.not(
                    CoreMatchers. `is`(
                        activity!!.window.decorView
                    )
                )
            )
        ).check(
            matches(
                ViewMatchers.isDisplayed()
            )
        )
    }
    @Test
    fun test10() {
        onView(withId(R.id.a)).perform(typeText("tete"))
        onView(withId(R.id.button)).perform(click())
        var activity : Activity ?= null
        activityScenarioRule.scenario.onActivity { activity = it }
        onView(withText(R.string.a_incorrectly)).inRoot(
            RootMatchers.withDecorView(
                CoreMatchers.not(
                    CoreMatchers. `is`(
                        activity!!.window.decorView
                    )
                )
            )
        ).check(
            matches(
                ViewMatchers.isDisplayed()
            )
        )
    }
    @Test
    fun test11() {
        onView(withId(R.id.a)).perform(typeText("10"))
        onView(withId(R.id.b)).perform(typeText("tete"))
        onView(withId(R.id.button)).perform(click())
        var activity : Activity ?= null
        activityScenarioRule.scenario.onActivity { activity = it }
        onView(withText(R.string.b_incorrectly)).inRoot(
            RootMatchers.withDecorView(
                CoreMatchers.not(
                    CoreMatchers. `is`(
                        activity!!.window.decorView
                    )
                )
            )
        ).check(
            matches(
                ViewMatchers.isDisplayed()
            )
        )
    }
    @Test
    fun test12() {
        onView(withId(R.id.a)).perform(typeText("1e2000"))
        onView(withId(R.id.button)).perform(click())
        var activity: Activity? = null
        activityScenarioRule.scenario.onActivity { activity = it }
        onView(withText(R.string.MAX_VALUE_for_a)).inRoot(
            RootMatchers.withDecorView(
                CoreMatchers.not(
                    CoreMatchers.`is`(
                        activity!!.window.decorView
                    )
                )
            )
        ).check(
            matches(
                ViewMatchers.isDisplayed()
            )
        )
    }
    @Test
    fun test13() {
        onView(withId(R.id.a)).perform(typeText("1"))
        onView(withId(R.id.b)).perform(typeText("1e2000"))
        onView(withId(R.id.button)).perform(click())
        var activity: Activity? = null
        activityScenarioRule.scenario.onActivity { activity = it }
        onView(withText(R.string.MAX_VALUE_for_b)).inRoot(
            RootMatchers.withDecorView(
                CoreMatchers.not(
                    CoreMatchers.`is`(
                        activity!!.window.decorView
                    )
                )
            )
        ).check(
            matches(
                ViewMatchers.isDisplayed()
            )
        )
    }
}

