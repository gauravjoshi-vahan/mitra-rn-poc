package com.vahan.mitra_playstore.view

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.facebook.react.*
import com.facebook.react.ReactPackage
import com.facebook.react.common.LifecycleState
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.shell.MainReactPackage
import com.facebook.soloader.SoLoader
import com.microsoft.codepush.react.CodePush
import com.microsoft.codepush.react.CodePush.getJSBundleFile
import com.vahan.mitra_playstore.BuildConfig
import java.util.*


class ReactActivity : Activity(), DefaultHardwareBackBtnHandler, ReactApplication {
    private var mReactRootView: ReactRootView? = null
    private var mReactInstanceManager: ReactInstanceManager? = null
    private var mReactNativeHost: ReactNativeHost? = null
    private var application = Application()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SoLoader.init(this, false)
        mReactRootView = ReactRootView(this)
        if (getApplication() !== null && applicationContext !== null && getApplication().resources !== null) {
            val packages: List<ReactPackage> = PackageList(getApplication()).packages
            mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setCurrentActivity(this)
                .setBundleAssetName("index.android.bundle")
                .setJSMainModulePath("index")
                .addPackages(packages)
//                .addPackage(
//                    CodePush(
//                        BuildConfig.CODEPUSH_KEY,
//                        applicationContext, BuildConfig.DEBUG
//                    )
//                )
                .setJSBundleFile(getJSBundleFile())
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build()
        }
        // The string here (e.g. "IntegratedApp") has to match
        // the string in AppRegistry.registerComponent() in index.js
        mReactRootView!!.startReactApplication(mReactInstanceManager, "mitra-rn-poc", null)
        setContentView(mReactRootView)
    }

    override fun invokeDefaultOnBackPressed() {
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        mReactInstanceManager?.onHostPause(this)
    }

    override fun onResume() {
        super.onResume()
        mReactInstanceManager?.onHostResume(this, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mReactInstanceManager?.onHostDestroy(this)
        mReactRootView?.unmountReactApplication()
    }

    override fun onBackPressed() {
        mReactInstanceManager?.onBackPressed()
        super.onBackPressed()
    }

    override fun getReactNativeHost(): ReactNativeHost {
        TODO("Not yet implemented")
    }
}
