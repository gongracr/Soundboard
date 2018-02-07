package com.rudedroid.soundboard.mainScreen.view;

import com.rudedroid.soundboard.mainScreen.model.SoundModel;
import java.util.List;

public interface MainScreenView {

  void showLoadingBar();

  void hideLoadingBar();

  void updateSoundList(List<SoundModel> soundModelList);

  void showToastError(int errorStringResourceId, Throwable throwable);
}
