package com.yellow.widgetdemo.widgetsUI

import android.app.Activity
import android.os.Bundle
import com.yellow.widgetdemo.R

/**
 * Created by Freeman on 2020/11/1
 */
open class RotationTableActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_rotation_table_activity)
        init()
    }

    private fun init() {

    }
}