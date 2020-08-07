package com.mylibrary.xmpp;

import android.os.Binder;

import java.lang.ref.WeakReference;

/**
 * Created by Prem Kumar and Anitha on 12/14/2016.
 */

public class LocalBinder<S>  extends Binder {
    private final WeakReference<S> mService;

    public LocalBinder(final S service) {
        mService = new WeakReference<S>(service);
    }

    public S getService() {
        return mService.get();
    }

}
