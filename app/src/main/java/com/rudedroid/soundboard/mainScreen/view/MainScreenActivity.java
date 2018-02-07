package com.rudedroid.soundboard.mainScreen.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rudedroid.soundboard.R;
import com.rudedroid.soundboard.base.view.BaseActivity;
import com.rudedroid.soundboard.mainScreen.dagger.DaggerMainScreenComponent;
import com.rudedroid.soundboard.mainScreen.dagger.MainScreenModule;
import com.rudedroid.soundboard.mainScreen.model.SoundModel;
import com.rudedroid.soundboard.mainScreen.presenter.MainScreenPresenter;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class MainScreenActivity extends BaseActivity implements MainScreenView {

  private static final String TAG = MainScreenActivity.class.getSimpleName();

  @BindView(R.id.soundsRecyclerView)
  RecyclerView soundsRecyclerView;
  @BindView(R.id.reproductionBarLayout)
  LinearLayout reproductionBarLayout;
  @BindView(R.id.playPauseButton)
  ImageView playPauseButton;
  @BindView(R.id.shuffleButton)
  ImageView shuffleButton;
  @BindView(R.id.playingSoundTextView)
  TextView playingSoundTextView;
  @BindView(R.id.loadingProgressBar)
  ProgressBar loadingProgressBar;
  @BindView(R.id.adView)
  AdView adView;

  @Inject
  MainScreenPresenter mainScreenPresenter;

  private MediaPlayer mp;
  private VerticalSoundListAdapter verticalAdapter;
  private SoundModel lastPlayedSound;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_screen);
    injectDependencies();
    setUpViews();
    registerItemClickObservable();
    mainScreenPresenter.viewCreated();
  }

  private void injectDependencies() {
    DaggerMainScreenComponent.builder()
        .applicationComponent(getApplicationComponent())
        .mainScreenModule(new MainScreenModule(this))
        .build()
        .injectMainScreenActivity(this);
  }

  private void setUpViews() {
    ButterKnife.bind(this);
    setUpRecyclerView();
    setUpAdView();
    shuffleButton.setOnClickListener(v1 -> playAudio(verticalAdapter.getRandomSound()));
    playPauseButton.setOnClickListener(v -> {
      if (lastPlayedSound == null) {
        return;
      }
      if (mp != null && mp.isPlaying()) {
        stopAudio(mp);
      } else {
        playAudio(lastPlayedSound);
      }
    });
  }

  private void setUpRecyclerView() {
    LayoutManager layoutManager = new LinearLayoutManager(this);
    verticalAdapter = new VerticalSoundListAdapter(this);
    soundsRecyclerView.setAdapter(verticalAdapter);
    soundsRecyclerView.setLayoutManager(layoutManager);
  }

  private void setUpAdView() {
    AdRequest adRequest = new AdRequest.Builder().build();
    adView.loadAd(adRequest);
    adView.setAdListener(new AdListener() {
      @Override
      public void onAdLoaded() {
        super.onAdLoaded();
      }
    });
  }

  @Override
  public void onPause() {
    super.onPause();
    if (mp != null) {
      stopAudio(mp);
    }
  }

  private void registerItemClickObservable() {
    verticalAdapter.getItemClickObservable()
        .subscribe(this::playAudio,
            throwable -> Log.e(TAG, "There was an error playing the audio"));
  }

  @Override
  public void showLoadingBar() {
    loadingProgressBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideLoadingBar() {
    loadingProgressBar.setVisibility(View.GONE);
  }

  @Override
  public void updateSoundList(List<SoundModel> soundModelList) {
    verticalAdapter.setSoundModelList(soundModelList);
    verticalAdapter.notifyDataSetChanged();
  }

  @Override
  public void showToastError(int errorStringResourceId, Throwable throwable) {
    Log.e(TAG, "Error downloading files", throwable);
    Toast.makeText(this, errorStringResourceId, Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.more_apps:
        Intent browserIntent =
            new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.more_apps_url)));
        startActivity(browserIntent);
        break;

      case R.id.rate_us:
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
            | Intent.FLAG_ACTIVITY_NEW_DOCUMENT
            | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
          startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
          startActivity(new Intent(Intent.ACTION_VIEW,
              Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
        break;

      case R.id.contact_us:
        String uriText = "mailto:rudedroiddevs@gmail.com";
        Uri uriMail = Uri.parse(uriText);
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(uriMail);
        startActivity(Intent.createChooser(sendIntent, "Send email"));
    }

    return super.onOptionsItemSelected(item);
  }

  private void stopAudio(MediaPlayer mplayer) {
    if (mplayer != null) {
      mplayer.release();
    }
    mp = null;
    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
  }

  private void playAudio(SoundModel soundModel) {
    lastPlayedSound = soundModel;
    playingSoundTextView.setText(soundModel.getTitle());
    reproductionBarLayout.setVisibility(View.VISIBLE);
    try {
      if (mp == null) {
        mp = new MediaPlayer();
        mp.setOnCompletionListener(this::stopAudio);
      }
      String mFileName = soundModel.getInternalPath();
      mp.stop();
      mp.reset();
      mp.setDataSource(mFileName);
      mp.prepare();
      mp.start();
      playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
