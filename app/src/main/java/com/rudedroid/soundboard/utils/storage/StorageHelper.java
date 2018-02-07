package com.rudedroid.soundboard.utils.storage;

import com.rudedroid.soundboard.mainScreen.model.SoundModel;
import java.io.File;
import okhttp3.Response;

public interface StorageHelper {

  String getInternalPathForFileName(String fileName);

  String getExternalPathForFileName(String fileName);

  boolean isFileStored(String filePath);

  File storeHttpResponse(Response response, String filePath);

  File getFileFromExternalStorageForSoundModel(SoundModel soundModel);
}
