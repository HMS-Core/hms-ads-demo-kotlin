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

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.ads.consent.bean.AdProvider
import com.huawei.hms.ads.consent.constant.ConsentStatus
import com.huawei.hms.ads.consent.constant.DebugNeedConsent
import com.huawei.hms.ads.consent.inter.Consent
import com.huawei.hms.ads.consent.inter.ConsentUpdateListener
import com.huawei.hms.ads.sdk.MainActivity
import com.huawei.hms.ads.sdk.dialogs.ConsentDialog
import com.huawei.hms.ads.sdk.dialogs.ProtocolDialog
import com.huawei.hms.ads.sdk.dialogs.ProtocolDialog.ProtocolDialogCallback
import java.util.*

class MainActivity : AppCompatActivity() {
    private var listView: ListView? = null
    private val adFormats: MutableList<AdFormat> = ArrayList()
    private val mHandler = Handler(Handler.Callback { msg ->
        if (hasWindowFocus()) {
            when (msg.what) {
                PROTOCOL_MSG_TYPE -> showPrivacyDialog()
                CONSENT_MSG_TYPE -> checkConsentStatus()
            }
        }
        false
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAdItems()
        listView = findViewById(R.id.item_list_view)
        val adapter = AdSampleAdapter(this@MainActivity, android.R.layout.simple_list_item_1, adFormats)
        listView!!.adapter = adapter
        listView!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val adFormat = adapter.getItem(position)
            val intent = Intent(applicationContext, adFormat!!.targetClass)
            startActivity(intent)
        }

        // Show the dialog for setting user privacy.
        sendMessage(PROTOCOL_MSG_TYPE, MSG_DELAY_MS)
    }

    private fun initAdItems() {
        adFormats.add(AdFormat(getString(R.string.banner_ad), BannerActivity::class.java))
        adFormats.add(AdFormat(getString(R.string.native_ad), NativeActivity::class.java))
        adFormats.add(AdFormat(getString(R.string.reward_ad), RewardActivity::class.java))
        adFormats.add(AdFormat(getString(R.string.interstitial_ad), InterstitialActivity::class.java))
        adFormats.add(AdFormat(getString(R.string.instream_ad), InstreamActivity::class.java))
    }

    /**
     * Display the app privacy protocol dialog box.
     */
    private fun showPrivacyDialog() {
        // If a user does not agree to the service agreement, the service agreement dialog is displayed.
        if (getPreferences(AdsConstant.SP_PROTOCOL_KEY, AdsConstant.DEFAULT_SP_PROTOCOL_VALUE) == 0) {
            Log.i(TAG, "Show protocol dialog.")
            val dialog = ProtocolDialog(this)
            dialog.setCallback(object : ProtocolDialogCallback {
                override fun agree() {
                    sendMessage(CONSENT_MSG_TYPE, MSG_DELAY_MS)
                }

                override fun cancel() {
                    // if the user selects the CANCEL button, exit application.
                    finish()
                }
            })
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        } else {
            sendMessage(CONSENT_MSG_TYPE, MSG_DELAY_MS)
        }
    }

    /**
     * If a user has not set consent, the consent dialog box is displayed.
     */
    private fun showConsentDialog(adProviders: List<AdProvider>) {
        Log.i(TAG, "Show consent dialog.")
        val dialog = ConsentDialog(this, adProviders)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun getPreferences(key: String?, defValue: Int): Int {
        val preferences = getSharedPreferences(AdsConstant.SP_NAME, Context.MODE_PRIVATE)
        val value = preferences.getInt(key, defValue)
        Log.i(TAG, "Key:$key, Preference value is: $value")
        return value
    }

    private fun sendMessage(what: Int, delayMillis: Int) {
        val msg = Message.obtain()
        msg.what = what
        mHandler.sendMessageDelayed(msg, delayMillis.toLong())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.ad_sample_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.privacy_settings -> startActivity(Intent(applicationContext, ProtocolActivity::class.java))
            R.id.consent_settings -> startActivity(Intent(applicationContext, ConsentActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    private fun checkConsentStatus() {
        val adProviderList: MutableList<AdProvider> = ArrayList()
        val consentInfo = Consent.getInstance(this)
        consentInfo.addTestDeviceId("********")
        consentInfo.setDebugNeedConsent(DebugNeedConsent.DEBUG_NEED_CONSENT)
        consentInfo.requestConsentUpdate(object : ConsentUpdateListener {
            override fun onSuccess(consentStatus: ConsentStatus?, isNeedConsent: Boolean, adProviders: List<AdProvider>?) {
                Log.i(TAG, "ConsentStatus: $consentStatus, isNeedConsent: $isNeedConsent")
                if (isNeedConsent) {
                    if (adProviders != null && adProviders.isNotEmpty()) {
                        adProviderList.addAll(adProviders)
                    }
                    showConsentDialog(adProviderList)
                }
            }

            override fun onFail(errorDescription: String?) {
                Log.e(TAG, "User's consent status failed to update: $errorDescription")
                if (getPreferences(AdsConstant.SP_CONSENT_KEY, AdsConstant.DEFAULT_SP_CONSENT_VALUE) < 0) {
                    // In this example, if the request fails, the consent dialog box is still displayed. In this case, the ad publisher list is empty.
                    showConsentDialog(adProviderList)
                }
            }
        })
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val PROTOCOL_MSG_TYPE = 100
        private const val CONSENT_MSG_TYPE = 200
        private const val MSG_DELAY_MS = 1000
    }
}