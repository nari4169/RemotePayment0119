package com.billcoreatech.remotepayment0119.viewModel

import android.app.Application
import com.billcoreatech.remotepayment0119.BuildConfig
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.kakao.sdk.common.KakaoSdk

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, BuildConfig.KAKAO_KEY)
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

    }
}