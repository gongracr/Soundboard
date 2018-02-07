package com.rudedroid.soundboard.base.dagger;

import android.app.Application;
import com.google.gson.Gson;
import com.rudedroid.soundboard.base.SoundboardApplication;
import com.rudedroid.soundboard.base.dagger.scopes.ApplicationScope;
import com.rudedroid.soundboard.utils.lifecycle.LifeCycleHandler;
import com.rudedroid.soundboard.utils.network.SoundboardDriveApi;
import com.rudedroid.soundboard.utils.storage.StorageHelper;
import dagger.Component;

@ApplicationScope
@Component(modules = {
    ApplicationModule.class
})
public interface ApplicationComponent {

  void injectSoundboardApplication(SoundboardApplication soundboardApplication);

  Application getApplication();

  LifeCycleHandler getLifeCycleHandler();

  Gson getGson();

  SoundboardDriveApi getSoundboardDriveApi();

  StorageHelper getStorageHelper();
}

