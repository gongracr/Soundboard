package com.rudedroid.soundboard.utils.lifecycle;

import com.trello.rxlifecycle.ActivityEvent;
import rx.Observable;

public interface LifeCycleHandler {

  <T> rx.Observable.Transformer<? super T, ? extends T> bindUntilEvent(ActivityEvent activityEvent);

  Observable<ActivityEvent> startOnEvent(ActivityEvent activityEvent);
}