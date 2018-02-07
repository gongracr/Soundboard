package com.rudedroid.soundboard.base;

import android.app.Application;
import com.rudedroid.soundboard.base.dagger.ApplicationComponent;
import com.rudedroid.soundboard.base.dagger.ApplicationModule;
import com.rudedroid.soundboard.base.dagger.DaggerApplicationComponent;

public class SoundboardApplication extends Application {

  private ApplicationComponent applicationComponent;

  @Override
  public void onCreate() {
    super.onCreate();
    applicationComponent = prepareApplicationComponent();
  }

  private ApplicationComponent prepareApplicationComponent() {
    ApplicationComponent applicationComponent =
        DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
    applicationComponent.injectSoundboardApplication(this);
    return applicationComponent;
  }

  public ApplicationComponent getApplicationComponent() {
    return applicationComponent;
  }
}
