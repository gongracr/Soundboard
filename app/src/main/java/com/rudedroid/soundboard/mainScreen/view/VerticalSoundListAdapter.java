package com.rudedroid.soundboard.mainScreen.view;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.rudedroid.soundboard.R;
import com.rudedroid.soundboard.mainScreen.model.SoundModel;
import com.rudedroid.soundboard.utils.storage.StorageHelper;
import com.rudedroid.soundboard.utils.storage.StorageHelperImp;
import com.tbruyelle.rxpermissions.RxPermissions;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import rx.Observable;
import rx.subjects.PublishSubject;

import static android.media.RingtoneManager.TYPE_NOTIFICATION;
import static android.media.RingtoneManager.TYPE_RINGTONE;

public class VerticalSoundListAdapter
    extends RecyclerView.Adapter<VerticalSoundListAdapter.SoundItemViewHolder> {

  private final Context context;
  private final RxPermissions rxPermissions;
  private final StorageHelper storageHelper;

  private List<SoundModel> soundModelList = new ArrayList<>();
  private PublishSubject<SoundModel> itemClickSubject = PublishSubject.create();

  public VerticalSoundListAdapter(Activity context) {
    this.context = context;
    rxPermissions = new RxPermissions(context);
    storageHelper = new StorageHelperImp(context);
  }

  public void setSoundModelList(List<SoundModel> soundModelList) {
    this.soundModelList = soundModelList;
  }

  public Observable<SoundModel> getItemClickObservable() {
    return itemClickSubject.asObservable();
  }

  @Override
  public SoundItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sound, parent, false);
    return new SoundItemViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(final SoundItemViewHolder holder, final int position) {
    SoundModel soundModel = soundModelList.get(position);
    boolean isAudioDownloaded = soundModel.isStored();
    holder.titleTextView.setText(soundModelList.get(position).getTitle());
    holder.enableSoundItem(isAudioDownloaded);
    holder.actionButtonImageView.setOnClickListener(v -> {
      PopupMenu popup = new PopupMenu(context, holder.actionButtonImageView);
      popup.inflate(R.menu.menu_more_options_sound_item);
      popup.setOnMenuItemClickListener(item -> {
        switch (item.getItemId()) {
          case R.id.share_to_opt:
            shareAudioFile(soundModel);
            break;
          case R.id.set_as_ringtone:
            setAudioAs(soundModel, TYPE_RINGTONE);
            break;
          case R.id.set_as_notification:
            setAudioAs(soundModel, TYPE_NOTIFICATION);
            break;
        }
        return false;
      });
      popup.show();
    });
    if (isAudioDownloaded) {
      holder.itemContainer.setOnClickListener(v -> itemClickSubject.onNext(soundModel));
    }
  }

  private void setAudioAs(SoundModel soundModel, int type) {
    rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(granted -> {
      if (granted) {
        boolean hasWritePermissions = checkSystemWritePermission();
        if (hasWritePermissions) {
          File audioFile = storageHelper.getFileFromExternalStorageForSoundModel(soundModel);
          String audioPath = audioFile.getAbsolutePath();
          ContentValues values = new ContentValues();
          values.put(MediaStore.MediaColumns.DATA, audioPath);
          values.put(MediaStore.MediaColumns.TITLE, soundModel.getTitle());
          values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
          values.put(MediaStore.Audio.Media.ARTIST, "");
          values.put(MediaStore.Audio.Media.IS_RINGTONE, type == TYPE_RINGTONE);
          values.put(MediaStore.Audio.Media.IS_NOTIFICATION, type == TYPE_NOTIFICATION);
          values.put(MediaStore.Audio.Media.IS_ALARM, false);
          values.put(MediaStore.Audio.Media.IS_MUSIC, false);
          Uri uri = MediaStore.Audio.Media.getContentUriForPath(audioPath);
          context.getContentResolver()
              .delete(uri, MediaStore.MediaColumns.DATA + "=\"" + audioPath + "\"", null);
          Uri newUri = context.getContentResolver().insert(uri, values);
          RingtoneManager.setActualDefaultRingtoneUri(context, type, newUri);
          String toastMsg =
              type == TYPE_NOTIFICATION ? context.getString(R.string.tone_set_as_notification)
                  : context.getString(R.string.tone_set_as_ringtone);
          Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show();
        } else {
          Toast.makeText(context, context.getString(R.string.need_allow_writing_permissions),
              Toast.LENGTH_LONG).show();
        }
      } else {
        String msg =
            type == TYPE_RINGTONE ? context.getString(R.string.accept_writing_permissions_ringtone)
                : context.getString(R.string.accept_writing_permissions_notification);
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
      }
    }, throwable -> Toast.makeText(context, context.getString(R.string.error_storing_files),
        Toast.LENGTH_LONG).show());
  }

  public SoundModel getRandomSound() {
    Random r = new Random();
    return soundModelList.get(r.nextInt(soundModelList.size()));
  }

  private void shareAudioFile(SoundModel soundModel) {
    String sharePath = soundModel.getInternalPath();
    Uri uri = Uri.parse("content://com.rudedroid.soundboard/" + sharePath);
    Intent share = new Intent(Intent.ACTION_SEND);
    share.setType("audio/*");
    share.putExtra(Intent.EXTRA_STREAM, uri);
    context.startActivity(Intent.createChooser(share, context.getString(R.string.share_sound)));
  }

  @Override
  public int getItemCount() {
    return soundModelList.size();
  }

  private boolean checkSystemWritePermission() {
    boolean retVal = true;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      retVal = Settings.System.canWrite(context);
      if (!retVal) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
      }
    }
    return retVal;
  }

  class SoundItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.actionButtonImageView)
    ImageView actionButtonImageView;

    View itemContainer;

    SoundItemViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemContainer = itemView;
    }

    private void enableSoundItem(boolean enable) {
      actionButtonImageView.setVisibility(enable ? View.VISIBLE : View.GONE);
      if (enable) {
        titleTextView.setTextColor(
            ContextCompat.getColor(context, R.color.soundlist_text_color_enabled));
      } else {
        titleTextView.setTextColor(
            ContextCompat.getColor(context, R.color.soundlist_text_color_disabled));
      }
    }
  }
}
