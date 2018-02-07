package com.rudedroid.soundboard.mainScreen.dagger;

import android.app.Application;
import com.google.gson.Gson;
import com.rudedroid.soundboard.base.dagger.scopes.ActivityScope;
import com.rudedroid.soundboard.mainScreen.interactor.MainScreenInteractor;
import com.rudedroid.soundboard.mainScreen.interactor.MainScreenInteractorImp;
import com.rudedroid.soundboard.mainScreen.model.CharacterFlavorMapper;
import com.rudedroid.soundboard.mainScreen.presenter.MainScreenPresenter;
import com.rudedroid.soundboard.mainScreen.presenter.MainScreenPresenterImp;
import com.rudedroid.soundboard.mainScreen.view.MainScreenView;
import com.rudedroid.soundboard.utils.lifecycle.LifeCycleHandler;
import com.rudedroid.soundboard.utils.network.SoundboardDriveApi;
import com.rudedroid.soundboard.utils.storage.StorageHelper;
import dagger.Module;
import dagger.Provides;

@Module
public class MainScreenModule {

  private final MainScreenView mainScreenView;

  public MainScreenModule(MainScreenView mainScreenView) {
    this.mainScreenView = mainScreenView;
  }

  @ActivityScope
  @Provides
  public MainScreenPresenter providesMainScreenPresenter(LifeCycleHandler lifeCycleHandler,
      MainScreenInteractor mainScreenInteractor, CharacterFlavorMapper characterFlavorMapper) {
    return new MainScreenPresenterImp(mainScreenView, lifeCycleHandler, mainScreenInteractor,
        characterFlavorMapper);
  }

  @ActivityScope
  @Provides
  public MainScreenInteractor providesMainScreenInteractor(Application context,
      SoundboardDriveApi soundboardDriveApi, StorageHelper storageHelper,
      CharacterFlavorMapper characterFlavorMapper) {
    return new MainScreenInteractorImp(context, soundboardDriveApi, storageHelper,
        characterFlavorMapper);
  }

  @ActivityScope
  @Provides
  public CharacterFlavorMapper providesCharacterFlavorMapper(Gson gson,
      StorageHelper storageHelper) {
    return new CharacterFlavorMapper(gson, storageHelper);
  }
}