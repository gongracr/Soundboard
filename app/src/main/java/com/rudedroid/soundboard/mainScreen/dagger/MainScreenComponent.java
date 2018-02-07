package com.rudedroid.soundboard.mainScreen.dagger;

import com.rudedroid.soundboard.base.dagger.ApplicationComponent;
import com.rudedroid.soundboard.base.dagger.scopes.ActivityScope;
import com.rudedroid.soundboard.mainScreen.view.MainScreenActivity;
import dagger.Component;

@ActivityScope
@Component(dependencies = { ApplicationComponent.class }, modules = {
    MainScreenModule.class
})
public interface MainScreenComponent {

  void injectMainScreenActivity(MainScreenActivity mainScreenActivity);
}

