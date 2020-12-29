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
import android.widget.TextView
import android.widget.Toast
import com.huawei.hms.ads.*
import com.huawei.hms.ads.banner.BannerView
import com.huawei.hms.ads.consent.bean.AdProvider
import com.huawei.hms.ads.consent.constant.ConsentStatus
import com.huawei.hms.ads.consent.constant.DebugNeedConsent
import com.huawei.hms.ads.consent.inter.Consent
import com.huawei.hms.ads.consent.inter.ConsentUpdateListener
import com.huawei.hms.ads.sdk.ConsentActivity
import com.huawei.hms.ads.sdk.dialogs.ConsentDialog
import com.huawei.hms.ads.sdk.dialogs.ConsentDialog.ConsentDialogCallback
import java.util.*

/**
 * Activity for displaying consent.
 */
class ConsentActivity : BaseActivity(), ConsentDialogCallback {
    private var adView: BannerView? = null
    private var adTypeTv: TextView? = null
    private var requestOptions: RequestOptions? = null
    private val mAdProviders: MutableList<AdProvider> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.consent_settings)
        setContentView(R.layout.activity_consent)
        adTypeTv = findViewById(R.id.ad_load_tip)
        adView = findViewById(R.id.consent_ad_view)

        // Check consent status.
        checkConsentStatus()
    }

    /**
     * Check consent status.
     */
    private fun checkConsentStatus() {
        val consentInfo = Consent.getInstance(this)

        // To ensure that a dialog box is displayed each time you access the code demo, set ConsentStatus to UNKNOWN. In normal cases, the code does not need to be added.
        consentInfo.setConsentStatus(ConsentStatus.UNKNOWN)
        val testDeviceId = consentInfo.testDeviceId
        consentInfo.addTestDeviceId(testDeviceId)

        // After DEBUG_NEED_CONSENT is set, ensure that the consent is required even if a device is not located in a specified area.
        consentInfo.setDebugNeedConsent(DebugNeedConsent.DEBUG_NEED_CONSENT)
        consentInfo.requestConsentUpdate(object : ConsentUpdateListener {
            override fun onSuccess(consentStatus: ConsentStatus, isNeedConsent: Boolean, adProviders: List<AdProvider>) {
                Log.d(TAG, "ConsentStatus: $consentStatus, isNeedConsent: $isNeedConsent")

                // The parameter indicating whether the consent is required is returned.
                if (isNeedConsent) {
                    // If ConsentStatus is set to UNKNOWN, re-collect user consent.
                    if (consentStatus == ConsentStatus.UNKNOWN) {
                        mAdProviders.clear()
                        mAdProviders.addAll(adProviders)
                        showConsentDialog()
                    } else {
                        // If ConsentStatus is set to PERSONALIZED or NON_PERSONALIZED, no dialog box is displayed to collect user consent.
                        loadBannerAd(consentStatus.value)
                    }
                } else {
                    // If a country does not require your app to collect user consent before displaying ads, your app can request a personalized ad directly.
                    Log.d(TAG, "User is NOT need Consent")
                    loadBannerAd(ConsentStatus.PERSONALIZED.value)
                }
            }

            override fun onFail(errorDescription: String) {
                Log.e(TAG, "User's consent status failed to update: $errorDescription")
                Toast
                        .makeText(this@ConsentActivity, "User's consent status failed to update: $errorDescription",
                                Toast.LENGTH_LONG)
                        .show()

                // In this demo,if the request fails ,you can load a non-personalized ad by default.
                loadBannerAd(ConsentStatus.NON_PERSONALIZED.value)
            }
        })
    }

    /**
     * Display the consent dialog box.
     */
    private fun showConsentDialog() {
        // Start to process the consent dialog box.
        val dialog = ConsentDialog(this, mAdProviders)
        dialog.setCallback(this)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    override fun updateConsentStatus(consentStatus: ConsentStatus) {
        loadBannerAd(consentStatus.value)
    }

    private fun loadBannerAd(consentStatus: Int) {
        Log.d(TAG, "Load banner ad, consent status: $consentStatus")
        if (consentStatus == ConsentStatus.UNKNOWN.value) {
            removeBannerAd()
        }

        // Obtain global ad singleton variables and add personalized ad request parameters.
        requestOptions = if (HwAds.getRequestOptions() == null) {
            RequestOptions()
        } else {
            HwAds.getRequestOptions()
        }

        // For non-personalized ads, reset this parameter.
        requestOptions = requestOptions!!.toBuilder()
                .setTagForUnderAgeOfPromise(UnderAge.PROMISE_TRUE)
                .setNonPersonalizedAd(consentStatus)
                .build()
        HwAds.setRequestOptions(requestOptions)
        val adParam = AdParam.Builder().build()
        adView!!.adId = getString(R.string.banner_ad_id)
        adView!!.bannerAdSize = BannerAdSize.BANNER_SIZE_SMART
        adView!!.adListener = adListener
        adView!!.loadAd(adParam)
        updateTextViewTips(consentStatus)
    }

    private val adListener: AdListener = object : AdListener() {
        override fun onAdLoaded() {
            // Called when an ad is loaded successfully.
            Toast.makeText(this@ConsentActivity, "Ad loaded successfully", Toast.LENGTH_SHORT).show()
        }

        override fun onAdFailed(errorCode: Int) {
            // Called when an ad fails to be loaded.
            Toast.makeText(this@ConsentActivity, "Ad failed to load", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeBannerAd() {
        if (adView != null) {
            adView!!.removeAllViews()
        }
        updateTextViewTips(ConsentStatus.UNKNOWN.value)
    }

    private fun updateTextViewTips(consentStatus: Int) {
        when {
            ConsentStatus.NON_PERSONALIZED.value == consentStatus -> {
                adTypeTv!!.text = getString(R.string.load_non_personalized_text)
            }
            ConsentStatus.PERSONALIZED.value == consentStatus -> {
                adTypeTv!!.text = getString(R.string.load_personalized_text)
            }
            else -> { // ConsentStatus.UNKNOWN
                adTypeTv!!.text = getString(R.string.no_ads_text)
            }
        }
    }

    companion object {
        private val TAG = ConsentActivity::class.java.simpleName
    }
}