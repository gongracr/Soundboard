package com.rudedroid.soundboard.mainScreen.model;

public class SoundModel {

  private String title;
  private String internalPath;
  private String externalPath;
  private boolean isStored;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getInternalPath() {
    return internalPath;
  }

  public void setInternalPath(String internalPath) {
    this.internalPath = internalPath;
  }

  public String getExternalPath() {
    return externalPath;
  }

  public void setExternalPath(String externalPath) {
    this.externalPath = externalPath;
  }

  public boolean isStored() {
    return isStored;
  }

  public void setStored(boolean stored) {
    isStored = stored;
  }
}
