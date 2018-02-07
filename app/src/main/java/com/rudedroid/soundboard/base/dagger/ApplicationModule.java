package com.rudedroid.soundboard.base.dagger;

import android.app.Application;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rudedroid.soundboard.base.dagger.scopes.ApplicationScope;
import com.rudedroid.soundboard.utils.lifecycle.LifeCycleHandler;
import com.rudedroid.soundboard.utils.lifecycle.LifecycleHandlerImp;
import com.rudedroid.soundboard.utils.network.SoundboardDriveApi;
import com.rudedroid.soundboard.utils.network.SoundboardDriveApiImp;
import com.rudedroid.soundboard.utils.storage.StorageHelper;
import com.rudedroid.soundboard.utils.storage.StorageHelperImp;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

  private final Application application;
  private final LifeCycleHandler lifeCycleHandler;

  public ApplicationModule(Application application) {
    this.application = application;
    lifeCycleHandler = new LifecycleHandlerImp(application);
  }

  @Provides
  Application providesApplication() {
    return application;
  }

  @ApplicationScope
  @Provides
  LifeCycleHandler providesLifeCycleHandler() {
    return lifeCycleHandler;
  }

  @ApplicationScope
  @Provides
  Gson providesGson() {
    return new GsonBuilder().create();
  }

  @ApplicationScope
  @Provides
  SoundboardDriveApi providesSoundboardDriveApi() {
    return new SoundboardDriveApiImp();
  }

  @ApplicationScope
  @Provides
  StorageHelper providesStorageHelper() {
    return new StorageHelperImp(application);
  }
}