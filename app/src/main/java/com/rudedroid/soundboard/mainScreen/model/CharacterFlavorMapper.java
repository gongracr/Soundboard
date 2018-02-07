package com.rudedroid.soundboard.mainScreen.model;

import com.google.gson.Gson;
import com.rudedroid.soundboard.data.CharacterFlavor;
import com.rudedroid.soundboard.data.CharacterSound;
import com.rudedroid.soundboard.utils.storage.StorageHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import rx.exceptions.Exceptions;

public class CharacterFlavorMapper {

  private final Gson gson;
  private final StorageHelper storageHelper;

  public CharacterFlavorMapper(Gson gson, StorageHelper storageHelper) {
    this.gson = gson;
    this.storageHelper = storageHelper;
  }

  public CharacterFlavor mapFileToCharacterFlavor(File file) {
    try {
      FileInputStream is = new FileInputStream(file);
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      String json = new String(buffer, "UTF-8");
      return gson.fromJson(json, CharacterFlavor.class);
    } catch (IOException exception) {
      throw Exceptions.propagate(exception);
    }
  }

  public List<SoundModel> mapCharacterFlavorToSoundModelList(CharacterFlavor characterFlavor) {
    List<SoundModel> soundModelList = new ArrayList<>();
    if (characterFlavor != null) {
      for (CharacterSound characterSound : characterFlavor.getCharacterSounds()) {
        String assetName = characterSound.getAssetName();
        String internalPath = storageHelper.getInternalPathForFileName(assetName);
        SoundModel soundModel = new SoundModel();
        soundModel.setTitle(characterSound.getSoundTitle());
        soundModel.setInternalPath(internalPath);
        soundModel.setExternalPath(storageHelper.getExternalPathForFileName(assetName));
        soundModel.setStored(storageHelper.isFileStored(internalPath));
        soundModelList.add(soundModel);
      }
    }
    return soundModelList;
  }
}
