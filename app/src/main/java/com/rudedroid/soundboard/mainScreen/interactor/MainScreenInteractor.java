package com.rudedroid.soundboard.mainScreen.interactor;

import com.rudedroid.soundboard.data.CharacterFlavor;
import com.rudedroid.soundboard.data.CharacterSound;
import java.io.File;
import java.util.List;
import rx.Observable;

public interface MainScreenInteractor {

  Observable<CharacterFlavor> getLocalCharacterFlavor();

  Observable<CharacterFlavor> getCharacterFlavorFromServer();

  Observable<File> getCharacterSoundFilesFromServer(List<CharacterSound> characterSoundList);
}
