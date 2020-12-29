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
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.InterstitialAd
import com.huawei.hms.ads.sdk.InterstitialActivity

/**
 * Activity for displaying an interstitial ad.
 */
class InterstitialActivity : BaseActivity() {
    private var displayRadioGroup: RadioGroup? = null
    private var loadAdButton: Button? = null
    private var interstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.interstitial_ad)
        setContentView(R.layout.activity_interstitial)

        // Initialize the HUAWEI Ads SDK.
        HwAds.init(this)
        displayRadioGroup = findViewById(R.id.display_radio_group)
        loadAdButton = findViewById(R.id.load_ad)
        loadAdButton!!.setOnClickListener(View.OnClickListener { loadInterstitialAd() })
    }

    private val adListener: AdListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            Toast.makeText(this@InterstitialActivity, "Ad loaded", Toast.LENGTH_SHORT).show()
            // Display an interstitial ad.
            showInterstitial()
        }

        override fun onAdFailed(errorCode: Int) {
            Toast.makeText(this@InterstitialActivity, "Ad load failed with error code: $errorCode",
                    Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Ad load failed with error code: $errorCode")
        }

        override fun onAdClosed() {
            super.onAdClosed()
            Toast.makeText(this@InterstitialActivity, "Ad closed", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "onAdClosed")
        }

        override fun onAdClicked() {
            Log.d(TAG, "onAdClicked")
            super.onAdClicked()
        }

        override fun onAdOpened() {
            Log.d(TAG, "onAdOpened")
            super.onAdOpened()
        }
    }

    private fun loadInterstitialAd() {
        interstitialAd = InterstitialAd(this)
        interstitialAd!!.adId = adId
        interstitialAd!!.adListener = adListener
        val adParam = AdParam.Builder().build()
        interstitialAd!!.loadAd(adParam)
    }

    private val adId: String
        private get() = if (displayRadioGroup!!.checkedRadioButtonId == R.id.display_image) {
            getString(R.string.image_ad_id)
        } else {
            getString(R.string.video_ad_id)
        }

    private fun showInterstitial() {
        // Display an interstitial ad.
        if (interstitialAd != null && interstitialAd!!.isLoaded) {
            interstitialAd!!.show()
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private val TAG = InterstitialActivity::class.java.simpleName
    }
}