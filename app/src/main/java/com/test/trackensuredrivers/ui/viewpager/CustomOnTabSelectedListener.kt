package com.test.trackensuredrivers.ui.viewpager

import com.google.android.material.tabs.TabLayout

abstract class CustomOnTabSelectedListener: TabLayout.OnTabSelectedListener {
    abstract override fun onTabSelected(tab: TabLayout.Tab)

    override fun onTabUnselected(tab: TabLayout.Tab?) {}
    override fun onTabReselected(tab: TabLayout.Tab?) {}
}