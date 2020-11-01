package com.yellow.widgetdemo.widgetsUI

import android.app.Activity
import android.graphics.Color
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Bundle
import com.yellow.widgetdemo.R
import com.yellow.widgetdemo.bean.PanelContent
import com.yellow.widgetdemo.widgets.RotationTableView
import kotlinx.android.synthetic.main.layout_rotation_table_activity.rotate_bt
import kotlinx.android.synthetic.main.layout_rotation_table_activity.rotation_table
import kotlin.random.Random

/**
 * Created by Freeman on 2020/11/1
 */
open class RotationTableActivity : Activity() {

    private var panelContentList = mutableListOf<PanelContent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_rotation_table_activity)
        init()
    }

    private fun init() {
        rotation_table.post {
            initTablePanelColor(rotation_table.width / 2f)
            initTablePanelContent()
        }
        rotate_bt.setOnClickListener {
            val targetIndex = Random.nextInt(panelContentList.size)
            rotation_table.startRotate()
            rotate_bt.postDelayed({
                rotation_table.setDrawResultIndex(targetIndex)
            }, 3000)
        }
    }

    private fun initTablePanelColor(radius: Float) {
        val specialPanelColor = RadialGradient(0f, 0f, radius,
            intArrayOf(Color.parseColor("#FFE600"), Color.parseColor("#FDB500")),
            null, Shader.TileMode.MIRROR)
        val radialGradient1 = RadialGradient(0f, 0f, radius,
            intArrayOf(Color.parseColor("#EE8CF9"), Color.parseColor("#AF4CF7")),
            null, Shader.TileMode.MIRROR)
        val radialGradient2 = RadialGradient(0f, 0f, radius,
            intArrayOf(Color.parseColor("#DF34FF"), Color.parseColor("#901EEE")),
            null, Shader.TileMode.MIRROR)
        val tablePanelColor = arrayOf(radialGradient1, radialGradient2)

        rotation_table.setTablePanelColor(specialPanelColor, tablePanelColor)
    }

    private fun initTablePanelContent() {
        panelContentList.add(PanelContent(R.drawable.rotaion_table_panel_icon, "奖品1"))
        panelContentList.add(PanelContent(R.drawable.rotaion_table_panel_icon, "奖品2"))
        panelContentList.add(PanelContent(R.drawable.rotaion_table_panel_icon, "奖品3"))
        panelContentList.add(PanelContent(R.drawable.rotaion_table_panel_icon, "奖品4"))
        panelContentList.add(PanelContent(R.drawable.rotaion_table_panel_icon, "奖品5"))
        panelContentList.add(PanelContent(R.drawable.rotaion_table_panel_icon, "奖品6"))
        panelContentList.add(PanelContent(R.drawable.rotaion_table_panel_icon, "奖品7"))
        panelContentList.add(PanelContent(R.drawable.rotaion_table_panel_icon, "奖品8"))
        rotation_table.setTablePanelList(
            panelContentList,
            object : RotationTableView.TablePanelAdapter<PanelContent> {
                override fun getText(data: PanelContent, position: Int): String {
                    return data.text
                }

                override fun getIconId(data: PanelContent, position: Int): Int {
                    return data.iconId
                }
            })
    }

}