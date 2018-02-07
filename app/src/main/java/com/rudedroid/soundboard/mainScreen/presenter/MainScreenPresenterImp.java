package com.rudedroid.soundboard.mainScreen.presenter;

import com.rudedroid.soundboard.R;
import com.rudedroid.soundboard.data.CharacterFlavor;
import com.rudedroid.soundboard.mainScreen.interactor.MainScreenInteractor;
import com.rudedroid.soundboard.mainScreen.model.CharacterFlavorMapper;
import com.rudedroid.soundboard.mainScreen.view.MainScreenView;
import com.rudedroid.soundboard.utils.lifecycle.LifeCycleHandler;
import com.trello.rxlifecycle.ActivityEvent;
import java.io.File;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class MainScreenPresenterImp implements MainScreenPresenter {

  private final MainScreenView mainScreenView;
  private final LifeCycleHandler lifeCycleHandler;
  private final MainScreenInteractor mainScreenInteractor;
  private final CharacterFlavorMapper characterFlavorMapper;

  public MainScreenPresenterImp(MainScreenView mainScreenView, LifeCycleHandler lifeCycleHandler,
      MainScreenInteractor mainScreenInteractor, CharacterFlavorMapper characterFlavorMapper) {
    this.mainScreenView = mainScreenView;
    this.lifeCycleHandler = lifeCycleHandler;
    this.mainScreenInteractor = mainScreenInteractor;
    this.characterFlavorMapper = characterFlavorMapper;
  }

  @Override
  public void viewCreated() {
    getSoundModelList();
  }

  private void getSoundModelList() {
    mainScreenInteractor.getCharacterFlavorFromServer()
        .compose(lifeCycleHandler.<CharacterFlavor>bindUntilEvent(ActivityEvent.DESTROY))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(this::getLocalCharacterFlavor)
        .doOnNext(this::downloadCharacterFlavorSounds)
        .map(characterFlavorMapper::mapCharacterFlavorToSoundModelList)
        .subscribe(soundModelList -> {
          mainScreenView.hideLoadingBar();
          mainScreenView.updateSoundList(soundModelList);
        }, throwable -> mainScreenView.showToastError(R.string.error_downloading_audio_files,
            throwable));
  }

  private void getLocalCharacterFlavor() {
    mainScreenInteractor.getLocalCharacterFlavor()
        .map(characterFlavorMapper::mapCharacterFlavorToSoundModelList)
        .subscribe(mainScreenView::updateSoundList, throwable -> mainScreenView.showLoadingBar());
  }

  private void downloadCharacterFlavorSounds(CharacterFlavor characterFlavor) {
    mainScreenInteractor.getCharacterSoundFilesFromServer(characterFlavor.getCharacterSounds())
        .compose(lifeCycleHandler.<File>bindUntilEvent(ActivityEvent.DESTROY))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe((Action0) () -> {
          //todo Show loading for the item
        })
        .subscribe(file -> {
          //todo Hide loading for the item
          mainScreenView.updateSoundList(
              characterFlavorMapper.mapCharacterFlavorToSoundModelList(characterFlavor));
        }, throwable -> mainScreenView.showToastError(R.string.error_downloading_audio_files,
            throwable));
  }
}
