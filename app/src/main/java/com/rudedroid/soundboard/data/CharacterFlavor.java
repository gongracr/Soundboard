package com.rudedroid.soundboard.data;

import java.util.List;

public class CharacterFlavor {

  private String characterVersion;
  private String characterName;
  private String driveFileId;
  private List<CharacterSound> characterSounds;

  public String getCharacterVersion() {
    return characterVersion;
  }

  public void setCharacterVersion(String characterVersion) {
    this.characterVersion = characterVersion;
  }

  public String getCharacterName() {
    return characterName;
  }

  public void setCharacterName(String characterName) {
    this.characterName = characterName;
  }

  public String getDriveFileId() {
    return driveFileId;
  }

  public void setDriveFileId(String driveFileId) {
    this.driveFileId = driveFileId;
  }

  public List<CharacterSound> getCharacterSounds() {
    return characterSounds;
  }

  public void setCharacterSounds(List<CharacterSound> characterSounds) {
    this.characterSounds = characterSounds;
  }
}
