package com.rudedroid.soundboard.base.view;

import android.support.v7.app.AppCompatActivity;
import com.rudedroid.soundboard.base.SoundboardApplication;
import com.rudedroid.soundboard.base.dagger.ApplicationComponent;

public abstract class BaseActivity extends AppCompatActivity {

  protected ApplicationComponent getApplicationComponent() {
    return ((SoundboardApplication) getApplication()).getApplicationComponent();
  }
}
