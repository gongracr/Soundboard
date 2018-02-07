package com.rudedroid.soundboard.utils.network;

import com.rudedroid.soundboard.utils.Constants;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.exceptions.Exceptions;

public class SoundboardDriveApiImp implements SoundboardDriveApi {

  private OkHttpClient client = new OkHttpClient();

  @Override
  public Observable<Response> downloadDriveFile(String driveFileId) {
    return Observable.just(getUrlForDriveId(driveFileId)).map(url -> {
      try {
        return executeRequestForUrl(url);
      } catch (IOException e) {
        throw Exceptions.propagate(e);
      }
    });
  }

  private String getUrlForDriveId(String driveFileId) {
    return String.format("%s%s", Constants.DRIVE_BASE_URL, driveFileId);
  }

  private Response executeRequestForUrl(String url) throws IOException {
    Request request = new Request.Builder().url(url).build();
    return client.newCall(request).execute();
  }
}
