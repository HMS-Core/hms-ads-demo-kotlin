/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.huawei.hms.ads.sdk

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.banner.BannerView
import com.huawei.hms.ads.sdk.BannerActivity
import java.util.*

/**
 * Activity for displaying a banner ad.
 */
class BannerActivity : BaseActivity() {
    private var bannerView: BannerView? = null
    private var defaultBannerView: BannerView? = null
    private var adFrameLayout: FrameLayout? = null
    private var sizeRadioGroup: RadioGroup? = null
    private var colorRadioGroup: RadioGroup? = null

    /**
     * Button tapping event listener.
     */
    private val buttonListener = View.OnClickListener { v ->
        defaultBannerView!!.visibility = View.INVISIBLE
        if (bannerView != null) {
            adFrameLayout!!.removeView(bannerView)
            bannerView!!.destroy()
        }

        // Call new BannerView(Context context) to create a BannerView class.
        bannerView = BannerView(v.context)

        // Set an ad slot ID.
        bannerView!!.adId = getString(R.string.banner_ad_id)

        // Set the background color and size based on user selection.
        val adSize = getBannerAdSize(sizeRadioGroup!!.checkedRadioButtonId)
        bannerView!!.bannerAdSize = adSize
        val color = getBannerViewBackground(colorRadioGroup!!.checkedRadioButtonId)
        bannerView!!.setBackgroundColor(color)
        adFrameLayout!!.addView(bannerView)
        bannerView!!.adListener = adListener
        bannerView!!.setBannerRefresh(30)
        bannerView!!.loadAd(AdParam.Builder().build())
    }

    /**
     * Ad listener.
     */
    private val adListener: AdListener = object : AdListener() {
        override fun onAdLoaded() {
            // Called when an ad is loaded successfully.
            showToast("Ad loaded.")
        }

        override fun onAdFailed(errorCode: Int) {
            // Called when an ad fails to be loaded.
            showToast(String.format(Locale.ROOT, "Ad failed to load with error code %d.", errorCode))
        }

        override fun onAdOpened() {
            // Called when an ad is opened.
            showToast(String.format("Ad opened "))
        }

        override fun onAdClicked() {
            // Called when a user taps an ad.
            showToast("Ad clicked")
        }

        override fun onAdLeave() {
            // Called when a user has left the app.
            showToast("Ad Leave")
        }

        override fun onAdClosed() {
            // Called when an ad is closed.
            showToast("Ad closed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.banner_ad)
        setContentView(R.layout.activity_banner)
        val bannerTitle = findViewById<TextView>(R.id.text_banner)
        bannerTitle.text = "This is banner ads sample."
        sizeRadioGroup = findViewById(R.id.size_radioGroup)
        colorRadioGroup = findViewById(R.id.color_radioGroup)

        // Load the default banner ad.
        loadDefaultBannerAd()

        // Set the button for loading an ad.
        val loadButton = findViewById<Button>(R.id.refreshButton)
        loadButton.setOnClickListener(buttonListener)
        adFrameLayout = findViewById(R.id.ad_frame)
    }

    /**
     * Load the default banner ad.
     */
    private fun loadDefaultBannerAd() {
        // Obtain BannerView based on the configuration in layout/activity_main.xml.
        defaultBannerView = findViewById(R.id.hw_banner_view)
        defaultBannerView!!.adListener = adListener
        defaultBannerView!!.setBannerRefresh(REFRESH_TIME.toLong())
        val adParam = AdParam.Builder().build()
        defaultBannerView!!.loadAd(adParam)
    }

    private fun showToast(message: String) {
        Toast.makeText(this@BannerActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun getBannerAdSize(checkedId: Int): BannerAdSize? {
        var adSize: BannerAdSize? = null
        when (checkedId) {
            R.id.size_320_50 -> adSize = BannerAdSize.BANNER_SIZE_320_50
            R.id.size_320_100 -> adSize = BannerAdSize.BANNER_SIZE_320_100
            R.id.size_300_250 -> adSize = BannerAdSize.BANNER_SIZE_300_250
            R.id.size_smart -> adSize = BannerAdSize.BANNER_SIZE_SMART
            R.id.size_360_57 -> adSize = BannerAdSize.BANNER_SIZE_360_57
            R.id.size_360_144 -> adSize = BannerAdSize.BANNER_SIZE_360_144
            else -> {
            }
        }
        return adSize
    }

    private fun getBannerViewBackground(checkedId: Int): Int {
        var color = Color.TRANSPARENT
        when (checkedId) {
            R.id.color_white -> color = Color.WHITE
            R.id.color_black -> color = Color.BLACK
            R.id.color_red -> color = Color.RED
            R.id.color_transparent -> color = Color.TRANSPARENT
            else -> {
            }
        }
        return color
    }

    companion object {
        private const val REFRESH_TIME = 30
    }
}