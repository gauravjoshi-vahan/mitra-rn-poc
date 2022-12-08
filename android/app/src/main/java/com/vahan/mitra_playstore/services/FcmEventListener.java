package com.vahan.mitra_playstore.services;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.moengage.firebase.MoEFireBaseHelper;
import com.moengage.firebase.listener.FirebaseEventListener;
import com.vahan.mitra_playstore.view.BaseApplication;


import org.jetbrains.annotations.NotNull;

public class FcmEventListener extends FirebaseEventListener {
  @Override public void onNonMoEngageMessageReceived(@NotNull RemoteMessage remoteMessage) {

    // payload received, add your processing logic here
    if (remoteMessage.getData().size() > 0){
      Log.d("MoeENgage", "onNonMoEngageMessageReceived: "+remoteMessage);

    }
  }

  @Override public void onTokenAvailable(@NotNull String token) {
    // push token received, add your processing logic here
    MoEFireBaseHelper.getInstance().passPushToken(BaseApplication.getContext(),token);
  }
}