package com.dofun.zhw.xposed.githubtools

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        Log.e("power_gson", "", IllegalArgumentException("类型解析异常：" + "123" + "，后台返回的类型为：" + "222"))
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.dofun.zhw.xposed.githubtools", appContext.packageName)
    }
}