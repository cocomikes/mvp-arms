package io.ganguo.library.mvp.fragment_handle;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import androidx.fragment.app.Fragment;

import java.util.Random;

/**
 * Created by Mikes at 2019-08-20 08:41
 */
public class RouterFragment extends Fragment {
    private SparseArray<ActivityResultHelper.Callback> mCallbacks = new SparseArray<>();
    private Random mCodeGenerator = new Random();

    public RouterFragment() {
        // Required empty public constructor
    }

    static RouterFragment newInstance() {
        return new RouterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    void startActivityForResult(Intent intent, ActivityResultHelper.Callback callback) {
        int requestCode = makeRequestCode();
        mCallbacks.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    private int makeRequestCode() {
        int requestCode;
        int tryCount = 0;
        do {
            requestCode = mCodeGenerator.nextInt(0x0000FFFF);
            tryCount++;
        } while (mCallbacks.indexOfKey(requestCode) >= 0 && tryCount < 10);
        return requestCode;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultHelper.Callback callback = mCallbacks.get(requestCode);
        mCallbacks.remove(requestCode);
        if (callback != null) {
            callback.onActivityResult(resultCode, data);
        }
    }

}
