package com.rudedroid.soundboard.utils.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.RxLifecycle;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class LifecycleHandlerImp implements LifeCycleHandler {

  private final Map<Activity, BehaviorSubject<ActivityEvent>> subjects = new HashMap<>();
  private BehaviorSubject<ActivityEvent> activeSubject;

  public LifecycleHandlerImp(Application application) {
    application.registerActivityLifecycleCallbacks(lifecycleCallbacks);
  }

  public <T> rx.Observable.Transformer<? super T, ? extends T> bindUntilEvent(
      ActivityEvent activityEvent) {
    return RxLifecycle.bindUntilActivityEvent(activeSubject, activityEvent);
  }

  @Override
  public Observable<ActivityEvent> startOnEvent(final ActivityEvent givenActivityEvent) {
    return activeSubject.filter(activityEvent -> activityEvent == givenActivityEvent);
  }

  private final Application.ActivityLifecycleCallbacks lifecycleCallbacks =
      new Application.ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
          activeSubject = BehaviorSubject.create();
          subjects.put(activity, activeSubject);
          activeSubject.onNext(ActivityEvent.CREATE);
        }

        @Override
        public void onActivityStarted(Activity activity) {
          activeSubject = subjects.get(activity);
          subjects.get(activity).onNext(ActivityEvent.START);
        }

        @Override
        public void onActivityResumed(Activity activity) {
          activeSubject = subjects.get(activity);
          subjects.get(activity).onNext(ActivityEvent.RESUME);
        }

        @Override
        public void onActivityPaused(Activity activity) {
          subjects.get(activity).onNext(ActivityEvent.PAUSE);
        }

        @Override
        public void onActivityStopped(Activity activity) {
          subjects.get(activity).onNext(ActivityEvent.STOP);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
          //default empty body
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
          subjects.get(activity).onNext(ActivityEvent.DESTROY);
          subjects.remove(activity);
        }
      };
}
