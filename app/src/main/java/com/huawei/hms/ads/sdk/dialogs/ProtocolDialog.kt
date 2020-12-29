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

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.huawei.hms.ads.sdk.AdsConstant
import com.huawei.hms.ads.sdk.R
import com.huawei.hms.ads.sdk.dialogs.ProtocolDialog.ProtocolDialogCallback

/**
 * Control on privacy-related dialog boxes.
 */
class ProtocolDialog(
private val mContext: Context) : Dialog(mContext, R.style.dialog)
{
    var titleTv: TextView? = null
    var protocolTv: TextView? = null
    var confirmButton: Button? = null
    var cancelButton: Button? = null
    var inflater: LayoutInflater? = null
    var mCallback: ProtocolDialogCallback? = null

    /**
     * Privacy protocol dialog box callback interface.
     */
    interface ProtocolDialogCallback {
        fun agree()
        fun cancel()
    }

    /**
     * Set a dialog box callback.
     *
     * @param callback callback
     */
    fun setCallback(callback: ProtocolDialogCallback?) {
        mCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dialogWindow: Window? = window
        dialogWindow!!.requestFeature(Window.FEATURE_NO_TITLE)
        inflater = LayoutInflater.from(mContext)
        val rootView = inflater!!.inflate(R.layout.dialog_protocol, null) as LinearLayout
        setContentView(rootView)
        titleTv = findViewById<TextView>(R.id.uniform_dialog_title)
        titleTv!!.text = mContext.getString(R.string.protocol_title)
        protocolTv = findViewById<TextView>(R.id.protocol_center_content)
        initClickableSpan()
        initButtonBar(rootView)
    }

    /**
     * Initialize the page of the button bar.
     *
     * @param rootView rootView
     */
    fun initButtonBar(rootView: LinearLayout) {
        confirmButton = rootView.findViewById(R.id.base_okBtn)
        confirmButton!!.setOnClickListener(View.OnClickListener {
            val preferences = mContext.getSharedPreferences(AdsConstant.SP_NAME, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putInt(AdsConstant.SP_PROTOCOL_KEY, 1).commit()
            dismiss()
            if (mCallback != null) {
                mCallback!!.agree()
            }
        })
        cancelButton = rootView.findViewById(R.id.base_cancelBtn)
        cancelButton!!.setOnClickListener(View.OnClickListener {
            dismiss()
            if (mCallback != null) {
                mCallback!!.cancel()
            }
        })
    }

    private fun initClickableSpan() {
        // Add a text-based tapping event.
        protocolTv!!.movementMethod = ScrollingMovementMethod.getInstance()
        val privacyInfoText = mContext.getString(R.string.protocol_content_text)
        val spanPrivacyInfoText = SpannableStringBuilder(privacyInfoText)

        // Set the listener on the event for tapping some text.
        val personalizedAdsTouchHere: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(ACTION_OAID_SETTING)
            }
        }
        val privacyStart = mContext.resources.getInteger(R.integer.privacy_start)
        val privacyEnd = mContext.resources.getInteger(R.integer.privacy_end)
        val privacySpan = StyleSpan(Typeface.BOLD)
        spanPrivacyInfoText.setSpan(privacySpan, privacyStart, privacyEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val colorPersonalize = ForegroundColorSpan(Color.BLUE)
        val personalizedStart = mContext.resources.getInteger(R.integer.personalized_start)
        val personalizedEnd = mContext.resources.getInteger(R.integer.personalized_end)
        spanPrivacyInfoText.setSpan(personalizedAdsTouchHere, personalizedStart, personalizedEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanPrivacyInfoText.setSpan(colorPersonalize, personalizedStart, personalizedEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        protocolTv!!.text = spanPrivacyInfoText
        protocolTv!!.movementMethod = LinkMovementMethod.getInstance()
    }

    fun startActivity(action: String) {
        val enterNative = Intent(action)
        val pkgMng = mContext.packageManager
        if (pkgMng != null) {
            val list = pkgMng.queryIntentActivities(enterNative, 0)
            if (!list.isEmpty()) {
                enterNative.setPackage("com.huawei.hwid")
                mContext.startActivity(enterNative)
            } else {
                // A message is displayed, indicating that no function is available and asking users to install HMS Core of the latest version.
                addAlertView()
            }
        }
    }

    /**
     * Prompt dialog box, indicating that no function is available and asking users to install Huawei Mobile Services (APK) of the latest version.
     */
    fun addAlertView() {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(mContext.getString(R.string.alert_title))
        builder.setMessage(mContext.getString(R.string.alert_content))
        builder.setPositiveButton(mContext.getString(R.string.alert_confirm), null)
        builder.show()
    }

    companion object {
    private val ACTION_SIMPLE_PRIVACY = "com.huawei.hms.ppskit.ACTION.SIMPLE_PRIVACY"
    private val ACTION_OAID_SETTING = "com.huawei.hms.action.OAID_SETTING"
}

}