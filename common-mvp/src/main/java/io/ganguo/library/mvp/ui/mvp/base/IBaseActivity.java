/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.ganguo.library.mvp.ui.mvp.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.MenuItem;

import io.ganguo.library.mvp.R;
import io.ganguo.library.mvp.fragment_handle.FragmentHandleBackUtil;
import io.ganguo.library.mvp.ui.mvp.IPresenter;
import io.ganguo.library.mvp.ui.mvp.base.delegate.IActivity;
import io.ganguo.library.mvp.ui.mvp.integration.cache.Cache;
import io.ganguo.library.mvp.ui.mvp.integration.cache.CacheType;
import io.ganguo.library.mvp.util.CBaseUtils;
import io.ganguo.library.mvp.util.KeyboardUtils;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * ================================================
 * 因为 Java 只能单继承, 所以如果要用到需要继承特定 {@link Activity} 的三方库, 那你就需要自己自定义 {@link Activity}
 * 继承于这个特定的 {@link Activity}, 然后再按照 {@link IBaseActivity} 的格式, 将代码复制过去, 记住一定要实现{@link IActivity}
 * ================================================
 */
public abstract class IBaseActivity<P extends IPresenter> extends AppCompatActivity implements IActivity {
    protected final String TAG = this.getClass().getSimpleName();
    private Cache<String, Object> mCache;

    private Bundle lastSavedInstanceState;

    @Inject
    protected P mPresenter;

    protected boolean isRestoreState() {
        return lastSavedInstanceState != null;
    }

    protected @Nullable Bundle getLastSavedInstanceState(){
        return lastSavedInstanceState;
    }

    @NonNull
    @Override
    public synchronized Cache<String, Object> provideCache() {
        if (mCache == null) {
            mCache = CBaseUtils.obtainAppComponentFromContext(this).cacheFactory().build(CacheType.ACTIVITY_CACHE);
        }
        return mCache;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastSavedInstanceState = savedInstanceState;

        try {
            int layoutResID = getLayoutResourceId();
            //如果 getLayoutResourceId 返回0,框架则不会调用setContentView(),当然也不会 Bind ButterKnife
            if (layoutResID != 0) {
                setContentView(layoutResID);
            }
        } catch (Exception e) {
            if (e instanceof InflateException) throw e;
            e.printStackTrace();
        }

        initView();
        initListener();
        initData();
    }

    // 是否忽略系统字号设置
    protected boolean ignoreSystemFontScale() {
        return true;
    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        if (ignoreSystemFontScale()) {
            if (resources != null && resources.getConfiguration().fontScale != 1F) {
                DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                Configuration newConfig = resources.getConfiguration();
                newConfig.fontScale = 1F;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    Context configurationContext = createConfigurationContext(newConfig);
                    resources = configurationContext.getResources();
                    displayMetrics.scaledDensity = displayMetrics.density * newConfig.fontScale;
                } else {
                    resources.updateConfiguration(newConfig, displayMetrics);
                }
            }
        }

        return resources;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!FragmentHandleBackUtil.handleBackPress(this)) {
            KeyboardUtils.hideSoftInput(this);

            super.onBackPressed();
            overridePendingTransition(R.anim.cbase__slide_in_from_left, R.anim.cbase__slide_out_to_right);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //send event to current child fragments
        sendEventToChildFragments(requestCode, resultCode, data);
    }

    private void sendEventToChildFragments(int requestCode, int resultCode, Intent data) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment == null) {
                Log.w("IBaseActivity", "Activity result no fragment exists for index: 0x"
                        + Integer.toHexString(requestCode));
            } else {
                handleResult(fragment, requestCode, resultCode, data);
            }
        }
    }

    private void handleResult(Fragment fragment, int requestCode, int resultCode, Intent data) {
        fragment.onActivityResult(requestCode, resultCode, data);
        for (Fragment childFragment : fragment.getChildFragmentManager().getFragments()) {
            if (childFragment != null) {
                handleResult(childFragment, requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 这个 {@link Activity} 是否会使用 {@link Fragment}, 框架会根据这个属性判断是否注册 {@link androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks}
     * 如果返回 {@code false}, 那意味着这个 {@link Activity} 不需要绑定 {@link Fragment}, 那你再在这个 {@link Activity} 中绑定继承于 {@link IBaseFragment} 的 {@link Fragment} 将不起任何作用
     *
     * @return 返回 {@code true} (默认为 {@code true}), 则需要使用 {@link Fragment}
     */
    @Override
    public boolean useFragment() {
        return true;
    }
}
