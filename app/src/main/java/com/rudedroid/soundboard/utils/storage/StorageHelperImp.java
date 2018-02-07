package com.rudedroid.soundboard.utils.storage;

import android.content.Context;
import android.os.Environment;
import com.rudedroid.soundboard.R;
import com.rudedroid.soundboard.mainScreen.model.SoundModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import rx.exceptions.Exceptions;

public class StorageHelperImp implements StorageHelper {

  private final Context context;

  public StorageHelperImp(Context context) {
    this.context = context;
  }

  @Override
  public String getInternalPathForFileName(String fileName) {
    return getInternalStorageDirectoryName() + File.separator + fileName;
  }

  @Override
  public String getExternalPathForFileName(String fileName) {
    return getExternalStorageDirectoryName() + File.separator + fileName;
  }

  @Override
  public boolean isFileStored(String filePath) {
    File file = new File(filePath);
    return file.exists();
  }

  @Override
  public File storeHttpResponse(Response response, String filePath) {
    try {
      File downloadedFile = new File(filePath);
      BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
      sink.writeAll(response.body().source());
      sink.close();
      return downloadedFile;
    } catch (IOException exception) {
      throw Exceptions.propagate(exception);
    }
  }

  @Override
  public File getFileFromExternalStorageForSoundModel(SoundModel soundModel) {
    String externalPath = soundModel.getExternalPath();
    File externalFile = new File(externalPath);
    if (isFileStored(externalPath)) {
      return externalFile;
    }
    File internalFile = new File(soundModel.getInternalPath());
    try {
      return exportFile(internalFile, externalFile);
    } catch (IOException exception) {
      throw Exceptions.propagate(exception);
    }
  }

  private File exportFile(File internalFile, File externalFile) throws IOException {
    File externalDirectory = new File(getExternalStorageDirectoryName());
    if (!externalDirectory.exists()) {
      if (!externalDirectory.mkdir()) {
        return null;
      }
    }
    FileChannel inChannel = new FileInputStream(internalFile).getChannel();
    FileChannel outChannel = new FileOutputStream(externalFile).getChannel();
    try {
      inChannel.transferTo(0, inChannel.size(), outChannel);
    } finally {
      inChannel.close();
      outChannel.close();
    }
    return externalFile;
  }

  private String getInternalStorageDirectoryName() {
    return context.getFilesDir().getPath();
  }

  private String getExternalStorageDirectoryName() {
    return Environment.getExternalStorageDirectory().getPath() + File.separator + context.getString(
        R.string.app_name) + File.separator;
  }
}
