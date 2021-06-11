package io.ganguo.library.mvp.arouter;

import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * Created by Mikes at 2019/4/28 4:05 PM
 */
public class BundleHelper {
    private Bundle bundle;

    public BundleHelper() {
        bundle = new Bundle();
    }

    public BundleHelper putString(@Nullable String key, @Nullable String value) {
        bundle.putString(key, value);
        return this;
    }

    public BundleHelper putBoolean(@Nullable String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleHelper putInt(@Nullable String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleHelper putLong(@Nullable String key, long value) {
        bundle.putLong(key, value);
        return this;
    }

    public BundleHelper putParcelable(@Nullable String key, @Nullable Parcelable value) {
        bundle.putParcelable(key, value);
        return this;
    }

    public BundleHelper putParcelableArrayList(@Nullable String key, @Nullable ArrayList<Parcelable> value) {
        bundle.putParcelableArrayList(key, value);
        return this;
    }

    public BundleHelper putSerializable(@Nullable String key, @Nullable Serializable value) {
        bundle.putSerializable(key, value);
        return this;
    }


    public BundleHelper putStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        bundle.putStringArrayList(key, value);
        return this;
    }

    public BundleHelper putBundle(@Nullable String key, Bundle value) {
        bundle.putBundle(key, value);
        return this;
    }

    public Bundle build() {
        return bundle;
    }
}
