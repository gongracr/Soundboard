package com.rudedroid.soundboard.utils.network;

import okhttp3.Response;
import rx.Observable;

public interface SoundboardDriveApi {

  Observable<Response> downloadDriveFile(String driveFileId);
}
