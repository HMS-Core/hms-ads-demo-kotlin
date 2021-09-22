package com.huawei.hms.ads.sdk

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.huawei.hms.ads.AppDownloadButton
import com.huawei.hms.ads.AppDownloadButtonStyle
import com.huawei.hms.ads.VideoOperator.VideoLifecycleListener
import com.huawei.hms.ads.nativead.MediaView
import com.huawei.hms.ads.nativead.NativeAd
import com.huawei.hms.ads.nativead.NativeView

class NativeViewFactory {

    companion object {

        private val TAG = NativeViewFactory::class.java.simpleName

        fun createMediumAdView(nativeAd: NativeAd, parentView: ViewGroup): View? {
            val inflater = LayoutInflater.from(parentView.context)
            val adRootView: View = inflater.inflate(R.layout.native_common_medium_template, null)
            val nativeView: NativeView = adRootView.findViewById(R.id.native_medium_view)
            nativeView.titleView = adRootView.findViewById(R.id.ad_title)
            nativeView.mediaView = adRootView.findViewById<View>(R.id.ad_media) as MediaView
            nativeView.adSourceView = adRootView.findViewById(R.id.ad_source)
            nativeView.callToActionView = adRootView.findViewById(R.id.ad_call_to_action)

            // Populate a native ad material view.
            (nativeView.titleView as TextView).text = nativeAd.title
            nativeView.mediaView.setMediaContent(nativeAd.mediaContent)
            if (null != nativeAd.adSource) {
                (nativeView.adSourceView as TextView).text = nativeAd.adSource
            }
            nativeView.adSourceView.visibility =
                if (null != nativeAd.adSource) View.VISIBLE else View.INVISIBLE
            if (null != nativeAd.callToAction) {
                (nativeView.callToActionView as Button).text = nativeAd.callToAction
            }
            nativeView.callToActionView.visibility =
                if (null != nativeAd.callToAction) View.VISIBLE else View.INVISIBLE

            // Obtain a video controller.
            val videoOperator = nativeAd.videoOperator

            // Check whether a native ad contains video materials.
            if (videoOperator.hasVideo()) {
                // Add a video lifecycle event listener.
                videoOperator.videoLifecycleListener = object : VideoLifecycleListener() {
                    override fun onVideoStart() {
                        Log.i(NativeViewFactory.TAG, "NativeAd video play start.")
                    }

                    override fun onVideoPlay() {
                        Log.i(NativeViewFactory.TAG, "NativeAd video playing.")
                    }

                    override fun onVideoEnd() {
                        Log.i(NativeViewFactory.TAG, "NativeAd video play end.")
                    }
                }
            }

            // Register a native ad object.
            nativeView.setNativeAd(nativeAd)
            return nativeView
        }


        fun createSmallImageAdView(nativeAd: NativeAd, parentView: ViewGroup): View? {
            val inflater = LayoutInflater.from(parentView.context)
            val adRootView: View = inflater.inflate(R.layout.native_small_image_template, null)
            val nativeView: NativeView = adRootView.findViewById(R.id.native_small_view)
            nativeView.titleView = adRootView.findViewById(R.id.ad_title)
            nativeView.mediaView = adRootView.findViewById<View>(R.id.ad_media) as MediaView
            nativeView.adSourceView = adRootView.findViewById(R.id.ad_source)
            nativeView.callToActionView = adRootView.findViewById(R.id.ad_call_to_action)

            // Populate a native ad material view.
            (nativeView.titleView as TextView).text = nativeAd.title
            nativeView.mediaView.setMediaContent(nativeAd.mediaContent)
            if (null != nativeAd.adSource) {
                (nativeView.adSourceView as TextView).text = nativeAd.adSource
            }
            nativeView.adSourceView.visibility =
                if (null != nativeAd.adSource) View.VISIBLE else View.INVISIBLE
            if (null != nativeAd.callToAction) {
                (nativeView.callToActionView as Button).text = nativeAd.callToAction
            }
            nativeView.callToActionView.visibility =
                if (null != nativeAd.callToAction) View.VISIBLE else View.INVISIBLE

            // Register a native ad object.
            nativeView.setNativeAd(nativeAd)
            return nativeView
        }

        fun createThreeImagesAdView(nativeAd: NativeAd, parentView: ViewGroup): View? {
            val inflater = LayoutInflater.from(parentView.context)
            val adRootView: View = inflater.inflate(R.layout.native_three_images_template, null)
            val nativeView: NativeView = adRootView.findViewById(R.id.native_three_images)
            nativeView.titleView = adRootView.findViewById(R.id.ad_title)
            nativeView.adSourceView = adRootView.findViewById(R.id.ad_source)
            nativeView.callToActionView = adRootView.findViewById(R.id.ad_call_to_action)
            val imageView1 = adRootView.findViewById<ImageView>(R.id.image_view_1)
            val imageView2 = adRootView.findViewById<ImageView>(R.id.image_view_2)
            val imageView3 = adRootView.findViewById<ImageView>(R.id.image_view_3)

            // Populate a native ad material view.
            (nativeView.titleView as TextView).text = nativeAd.title
            if (null != nativeAd.adSource) {
                (nativeView.adSourceView as TextView).text = nativeAd.adSource
            }
            nativeView.adSourceView.visibility =
                if (null != nativeAd.adSource) View.VISIBLE else View.INVISIBLE
            if (null != nativeAd.callToAction) {
                (nativeView.callToActionView as Button).text = nativeAd.callToAction
            }
            nativeView.callToActionView.visibility =
                if (null != nativeAd.callToAction) View.VISIBLE else View.INVISIBLE
            if (nativeAd.images != null && nativeAd.images.size >= 3) {
                imageView1.setImageDrawable(nativeAd.images[0].drawable)
                imageView2.setImageDrawable(nativeAd.images[1].drawable)
                imageView3.setImageDrawable(nativeAd.images[2].drawable)
            }

            // Register a native ad object.
            nativeView.setNativeAd(nativeAd)
            return nativeView
        }

        fun createAppDownloadButtonAdView(nativeAd: NativeAd, parentView: ViewGroup): View? {
            val inflater = LayoutInflater.from(parentView.context)
            val adRootView: View =
                inflater.inflate(R.layout.native_ad_with_app_download_btn_template, null)
            val nativeView: NativeView = adRootView.findViewById(R.id.native_app_download_button_view)
            nativeView.titleView = adRootView.findViewById(R.id.ad_title)
            nativeView.mediaView = adRootView.findViewById<View>(R.id.ad_media) as MediaView
            nativeView.adSourceView = adRootView.findViewById(R.id.ad_source)
            nativeView.callToActionView = adRootView.findViewById(R.id.ad_call_to_action)

            // Populate a native ad material view.
            (nativeView.titleView as TextView).text = nativeAd.title
            nativeView.mediaView.setMediaContent(nativeAd.mediaContent)
            if (null != nativeAd.adSource) {
                (nativeView.adSourceView as TextView).text = nativeAd.adSource
            }
            nativeView.adSourceView.visibility =
                if (null != nativeAd.adSource) View.VISIBLE else View.INVISIBLE
            if (null != nativeAd.callToAction) {
                (nativeView.callToActionView as Button).text = nativeAd.callToAction
            }

            // Register a native ad object.
            nativeView.setNativeAd(nativeAd)
            val appDownloadButton: AppDownloadButton = nativeView.findViewById(R.id.app_download_btn)
            appDownloadButton.setAppDownloadButtonStyle(NativeViewFactory.MyAppDownloadStyle(parentView.context))
            if (nativeView.register(appDownloadButton)) {
                appDownloadButton.visibility = View.VISIBLE
                appDownloadButton.refreshAppStatus()
                nativeView.callToActionView.visibility = View.GONE
            } else {
                appDownloadButton.visibility = View.GONE
                nativeView.callToActionView.visibility = View.VISIBLE
            }
            return nativeView
        }

        fun createImageOnlyAdView(nativeAd: NativeAd, parentView: ViewGroup): View? {
            val inflater = LayoutInflater.from(parentView.context)
            val adRootView: View = inflater.inflate(R.layout.native_image_only_template, null)
            val nativeView: NativeView = adRootView.findViewById(R.id.native_image_only_view)
            nativeView.mediaView = adRootView.findViewById<View>(R.id.ad_media) as MediaView
            nativeView.callToActionView = adRootView.findViewById(R.id.ad_call_to_action)
            nativeView.mediaView.setMediaContent(nativeAd.mediaContent)
            if (null != nativeAd.callToAction) {
                (nativeView.callToActionView as Button).text = nativeAd.callToAction
            }
            nativeView.callToActionView.visibility =
                if (null != nativeAd.callToAction) View.VISIBLE else View.INVISIBLE

            // Register a native ad object.
            nativeView.setNativeAd(nativeAd)
            return nativeView
        }

    }

    /**
     * Custom AppDownloadButton Style
     */
    private class MyAppDownloadStyle(context: Context) :
        AppDownloadButtonStyle(context) {
        init {
            normalStyle.textColor = context.resources.getColor(R.color.white)
            normalStyle.background = context.resources.getDrawable(R.drawable.native_button_rounded_corners_shape)
            processingStyle.textColor = context.resources.getColor(R.color.black)
        }
    }

}