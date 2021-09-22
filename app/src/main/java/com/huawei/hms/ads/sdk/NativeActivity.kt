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

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.VideoConfiguration
import com.huawei.hms.ads.nativead.*

class NativeActivity : BaseActivity() {

    private lateinit var bigImage: RadioButton
    private lateinit var threeSmall: RadioButton
    private lateinit var smallImage: RadioButton
    private lateinit var videoWithText: RadioButton
    private lateinit var appDownloadBtn: RadioButton
    private lateinit var loadBtn: Button
    private lateinit var adScrollView: ScrollView
    private var globalNativeAd: NativeAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native)

        bigImage = findViewById(R.id.radio_button_large)
        threeSmall = findViewById(R.id.radio_button_three_small)
        smallImage = findViewById(R.id.radio_button_small)
        videoWithText = findViewById(R.id.radio_button_video)
        appDownloadBtn = findViewById(R.id.radio_button_app_download_button)

        loadBtn = findViewById(R.id.btn_load)
        adScrollView = findViewById(R.id.scroll_view_ad)

        loadBtn.setOnClickListener(View.OnClickListener { loadAd(getAdId()) })

        loadAd(getAdId())
    }

    /**
     * Initialize ad slot ID and layout template.
     *
     * @return ad slot ID
     */
    private fun getAdId(): String {
        var adId = getString(R.string.ad_id_native)
        when {
            bigImage!!.isChecked -> {
                adId = getString(R.string.ad_id_native)
            }
            smallImage!!.isChecked -> {
                adId = getString(R.string.ad_id_native_small)
            }
            threeSmall!!.isChecked -> {
                adId = getString(R.string.ad_id_native_three)
            }
            videoWithText!!.isChecked -> {
                adId = getString(R.string.ad_id_native_video)
            }
            appDownloadBtn!!.isChecked -> {
                adId = getString(R.string.ad_id_native_video)
            }
        }
        return adId
    }

    /**
     * Load a native ad.
     *
     * @param adId ad slot ID.
     */
    private fun loadAd(adId: String) {
        updateStatus(null, false)
        val builder = NativeAdLoader.Builder(this, adId)
        builder.setNativeAdLoadedListener { nativeAd -> // Call this method when an ad is successfully loaded.
            updateStatus(getString(R.string.status_load_ad_success), true)

            // Display native ad.
            showNativeAd(nativeAd)
        }.setAdListener(object : AdListener() {
            override fun onAdLoaded() {
                updateStatus(getString(R.string.status_load_ad_finish), true)
            }

            override fun onAdFailed(errorCode: Int) {
                // Call this method when an ad fails to be loaded.
                updateStatus(getString(R.string.status_load_ad_fail) + errorCode, true)
            }
        })
        val videoConfiguration = VideoConfiguration.Builder()
            .setStartMuted(true)
            .build()
        val adConfiguration = NativeAdConfiguration.Builder()
            .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.BOTTOM_RIGHT) // Set custom attributes.
            .setVideoConfiguration(videoConfiguration)
            .setRequestMultiImages(true)
            .build()
        val nativeAdLoader = builder.setNativeAdOptions(adConfiguration).build()
        nativeAdLoader.loadAd(AdParam.Builder().build())
        updateStatus(getString(R.string.status_ad_loading), false)
    }

    /**
     * Display native ad.
     *
     * @param nativeAd native ad object that contains ad materials.
     */
    private fun showNativeAd(nativeAd: NativeAd) {
        // Destroy the original native ad.
        if (null != globalNativeAd) {
            globalNativeAd!!.destroy()
        }
        globalNativeAd = nativeAd
        val nativeView: View = createNativeView(nativeAd, adScrollView)!!
        if (nativeView != null) {
            globalNativeAd!!.setDislikeAdListener { // Call this method when an ad is closed.
                updateStatus(getString(R.string.ad_is_closed), true)
                adScrollView.removeView(nativeView)
            }

            // Add NativeView to the app UI.
            adScrollView.removeAllViews()
            adScrollView.addView(nativeView)
        }
    }

    /**
     * Create a nativeView by creativeType and fill in ad material.
     *
     * @param nativeAd   native ad object that contains ad materials.
     * @param parentView parent view of nativeView.
     */
    private fun createNativeView(nativeAd: NativeAd, parentView: ViewGroup): View? {
        val createType = nativeAd.creativeType
        Log.i(TAG, "Native ad createType is $createType")
        if (createType == 2 || createType == 102) {
            // Large image
            return NativeViewFactory.createImageOnlyAdView(nativeAd, parentView)
        } else if (createType == 3 || createType == 6) {
            // Large image with text or video with text
            return NativeViewFactory.createMediumAdView(nativeAd, parentView)
        } else if (createType == 103 || createType == 106) {
            // Large image with text or Video with text, using AppDownloadButton template.
            return NativeViewFactory.createAppDownloadButtonAdView(nativeAd, parentView)
        } else if (createType == 7 || createType == 107) {
            // Small image with text-
            return NativeViewFactory.createSmallImageAdView(nativeAd, parentView)
        } else if (createType == 8 || createType == 108) {
            // Three small images with text
            return NativeViewFactory.createThreeImagesAdView(nativeAd, parentView)
        } else {
            return null
        }
    }

    /**
     * Update tip and status of the load button.
     *
     * @param text           tip.
     * @param loadBtnEnabled status of the load button.
     */
    private fun updateStatus(text: String?, loadBtnEnabled: Boolean) {
        if (null != text) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
        loadBtn.isEnabled = loadBtnEnabled
    }

    override fun onDestroy() {
        if (null != globalNativeAd) {
            globalNativeAd!!.destroy()
        }
        super.onDestroy()
    }

    companion object{
        private val TAG = NativeActivity::class.java.simpleName
    }
}