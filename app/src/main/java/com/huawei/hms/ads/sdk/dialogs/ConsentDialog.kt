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
package com.huawei.hms.ads.sdk.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.huawei.hms.ads.consent.bean.AdProvider
import com.huawei.hms.ads.consent.constant.ConsentStatus
import com.huawei.hms.ads.consent.inter.Consent
import com.huawei.hms.ads.sdk.AdsConstant
import com.huawei.hms.ads.sdk.R
import com.huawei.hms.ads.sdk.dialogs.ConsentDialog.ConsentDialogCallback

/**
 * Control on consent-related dialog boxes.
 */
class ConsentDialog(
private val mContext: Context, private
val madProviders: List<AdProvider>) : Dialog(mContext, R.style.dialog)
{
    var inflater: LayoutInflater? = null
    var contentLayout: LinearLayout? = null
    var titleTv: TextView? = null
    var initInfoTv: TextView? = null
    var moreInfoTv: TextView? = null
    var partnersListTv: TextView? = null
    var consentDialogView: View? = null
    var initView: View? = null
    var moreInfoView: View? = null
    var partnersListView: View? = null
    var consentYesBtn: Button? = null
    var consentNoBtn: Button? = null
    var moreInfoBackBtn: Button? = null
    var partnerListBackBtn: Button? = null
    var mCallback: ConsentDialogCallback? = null

    /**
     * Consent dialog box callback interface.
     */
    interface ConsentDialogCallback {
        /**
         * Update a user selection result.
         *
         * @param consentStatus ConsentStatus
         */
        fun updateConsentStatus(consentStatus: ConsentStatus)
    }

    /**
     * Set a dialog box callback.
     *
     * @param callback callback
     */
    fun setCallback(callback: ConsentDialogCallback?) {
        mCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dialogWindow: Window? = window
        dialogWindow!!.requestFeature(Window.FEATURE_NO_TITLE)
        inflater = LayoutInflater.from(mContext)
        consentDialogView = inflater!!.inflate(R.layout.dialog_consent, null)
        setContentView(consentDialogView!!)
        titleTv = findViewById<TextView>(R.id.consent_dialog_title_text)
        titleTv!!.text = mContext.getString(R.string.consent_title)
        initView = inflater!!.inflate(R.layout.dialog_consent_content, null)
        moreInfoView = inflater!!.inflate(R.layout.dialog_consent_moreinfo, null)
        partnersListView = inflater!!.inflate(R.layout.dialog_consent_partner_list, null)

        // Add content to the initialization dialog box.
        showInitConsentInfo()
    }

    /**
     * Update the consent status.
     *
     * @param consentStatus ConsentStatus
     */
    private fun updateConsentStatus(consentStatus: ConsentStatus) {
        // Update the consent status.
        Consent.getInstance(mContext).setConsentStatus(consentStatus)

        // Save a user selection to the local SP.
        val preferences = mContext.getSharedPreferences(AdsConstant.SP_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(AdsConstant.SP_CONSENT_KEY, consentStatus.value).commit()

        // Callback activity
        if (mCallback != null) {
            mCallback!!.updateConsentStatus(consentStatus)
        }
    }

    /**
     * Display initial consent content.
     */
    private fun showInitConsentInfo() {
        addContentView(initView)

        // Add a button and text link and their tapping events to the initialization page.
        addInitButtonAndLinkClick(consentDialogView)
    }

    /**
     * Add a button and a text link and their tapping events to the initial page.
     *
     * @param rootView rootView
     */
    private fun addInitButtonAndLinkClick(rootView: View?) {
        consentYesBtn = rootView!!.findViewById(R.id.btn_consent_init_yes)
        consentYesBtn!!.setOnClickListener(View.OnClickListener {
            dismiss()
            updateConsentStatus(ConsentStatus.PERSONALIZED)
        })
        consentNoBtn = rootView.findViewById(R.id.btn_consent_init_skip)
        consentNoBtn!!.setOnClickListener(View.OnClickListener {
            dismiss()
            updateConsentStatus(ConsentStatus.NON_PERSONALIZED)
        })
        initInfoTv = rootView.findViewById(R.id.consent_center_init_content)
        initInfoTv!!.movementMethod = ScrollingMovementMethod.getInstance()
        val initText = mContext.getString(R.string.consent_init_text)
        val spanInitText = SpannableStringBuilder(initText)

        // Set the listener on the event for tapping some text.
        val initTouchHere: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                showTouchHereInfo()
            }
        }
        val colorSpan = ForegroundColorSpan(Color.parseColor("#0000FF"))
        val initTouchHereStart = mContext.resources.getInteger(R.integer.init_here_start)
        val initTouchHereEnd = mContext.resources.getInteger(R.integer.init_here_end)
        spanInitText.setSpan(initTouchHere, initTouchHereStart, initTouchHereEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanInitText.setSpan(colorSpan, initTouchHereStart, initTouchHereEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        initInfoTv!!.text = spanInitText
        initInfoTv!!.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Display the content on the more information page.
     */
    fun showTouchHereInfo() {
        addContentView(moreInfoView)

        // Set the listener on the tapping event on the more information page.
        addMoreInfoButtonAndLinkClick(consentDialogView)
    }

    /**
     * Add a button and a text link and their tapping events to the more information page.
     *
     * @param rootView rootView
     */
    private fun addMoreInfoButtonAndLinkClick(rootView: View?) {
        moreInfoBackBtn = rootView!!.findViewById(R.id.btn_consent_more_info_back)
        moreInfoBackBtn!!.setOnClickListener(View.OnClickListener { showInitConsentInfo() })
        moreInfoTv = rootView.findViewById(R.id.consent_center_more_info_content)
        moreInfoTv!!.movementMethod = ScrollingMovementMethod.getInstance()
        val moreInfoText = mContext.getString(R.string.consent_more_info_text)
        val spanMoreInfoText = SpannableStringBuilder(moreInfoText)

        // Set the listener on the event for tapping some text.
        val moreInfoTouchHere: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                showPartnersListInfo()
            }
        }
        val colorSpan = ForegroundColorSpan(Color.parseColor("#0000FF"))
        val moreInfoTouchHereStart = mContext.resources.getInteger(R.integer.more_info_here_start)
        val moreInfoTouchHereEnd = mContext.resources.getInteger(R.integer.more_info_here_end)
        spanMoreInfoText.setSpan(moreInfoTouchHere, moreInfoTouchHereStart, moreInfoTouchHereEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanMoreInfoText.setSpan(colorSpan, moreInfoTouchHereStart, moreInfoTouchHereEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        moreInfoTv!!.text = spanMoreInfoText
        moreInfoTv!!.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Display the partner list page.
     */
    fun showPartnersListInfo() {
        partnersListTv = partnersListView!!.findViewById(R.id.partners_list_content)
        partnersListTv!!.movementMethod = ScrollingMovementMethod.getInstance()
        partnersListTv!!.text = ""
        val learnAdProviders = madProviders
        if (learnAdProviders != null) {
            for (learnAdProvider in learnAdProviders) {
                val link = ("<font color='#0000FF'><a href=" + learnAdProvider.privacyPolicyUrl + ">"
                        + learnAdProvider.name + "</a>")
                partnersListTv!!.append(Html.fromHtml(link))
                partnersListTv!!.append("  ")
            }
        } else {
            partnersListTv!!.append(" 3rd party’s full list of advertisers is empty")
        }
        partnersListTv!!.movementMethod = LinkMovementMethod.getInstance()
        addContentView(partnersListView)

        // Set the listener on the tapping event on the partner list page.
        addPartnersListButtonAndLinkClick(consentDialogView)
    }

    /**
     * Add a button and a text link and their tapping events to the partner list page.
     *
     * @param rootView rootView
     */
    private fun addPartnersListButtonAndLinkClick(rootView: View?) {
        partnerListBackBtn = rootView!!.findViewById(R.id.btn_partners_list_back)
        partnerListBackBtn!!.setOnClickListener(View.OnClickListener { showTouchHereInfo() })
    }

    /**
     * Add layout content in the dialog box that is displayed.
     */
    private fun addContentView(view: View?) {
        contentLayout = findViewById<LinearLayout>(R.id.consent_center_layout)
        contentLayout!!.removeAllViews()
        contentLayout!!.addView(view)
    }

}