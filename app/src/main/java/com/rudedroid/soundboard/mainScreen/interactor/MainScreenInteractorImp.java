package com.rudedroid.soundboard.mainScreen.interactor;

import android.content.Context;
import com.rudedroid.soundboard.R;
import com.rudedroid.soundboard.data.CharacterFlavor;
import com.rudedroid.soundboard.data.CharacterSound;
import com.rudedroid.soundboard.mainScreen.model.CharacterFlavorMapper;
import com.rudedroid.soundboard.utils.network.SoundboardDriveApi;
import com.rudedroid.soundboard.utils.storage.StorageHelper;
import java.io.File;
import java.util.List;
import rx.Observable;

import static com.rudedroid.soundboard.utils.Constants.CHARACTER_FLAVOR_JSON_FILE;

public class MainScreenInteractorImp implements MainScreenInteractor {

  private final Context context;
  private final SoundboardDriveApi soundboardDriveApi;
  private final StorageHelper storageHelper;
  private final CharacterFlavorMapper characterFlavorMapper;

  public MainScreenInteractorImp(Context context, SoundboardDriveApi soundboardDriveApi,
      StorageHelper storageHelper, CharacterFlavorMapper characterFlavorMapper) {
    this.context = context;
    this.soundboardDriveApi = soundboardDriveApi;
    this.storageHelper = storageHelper;
    this.characterFlavorMapper = characterFlavorMapper;
  }

  @Override
  public Observable<CharacterFlavor> getLocalCharacterFlavor() {
    File file = new File(storageHelper.getInternalPathForFileName(CHARACTER_FLAVOR_JSON_FILE));
    return Observable.just(file).map(characterFlavorMapper::mapFileToCharacterFlavor);
  }

  @Override
  public Observable<CharacterFlavor> getCharacterFlavorFromServer() {
    return soundboardDriveApi.downloadDriveFile(context.getString(R.string.characterFileId))
        .map(response -> storageHelper.storeHttpResponse(response,
            storageHelper.getInternalPathForFileName(CHARACTER_FLAVOR_JSON_FILE)))
        .map(characterFlavorMapper::mapFileToCharacterFlavor);
  }

  @Override
  public Observable<File> getCharacterSoundFilesFromServer(
      List<CharacterSound> characterSoundList) {
    return Observable.from(characterSoundList)
        .filter(characterSound -> !storageHelper.isFileStored(
            storageHelper.getInternalPathForFileName(characterSound.getAssetName())))
        .flatMap(characterSound -> soundboardDriveApi.downloadDriveFile(
            characterSound.getSoundDriveFileId())
            .map(response -> storageHelper.storeHttpResponse(response,
                storageHelper.getInternalPathForFileName(characterSound.getAssetName()))));
  }
}
