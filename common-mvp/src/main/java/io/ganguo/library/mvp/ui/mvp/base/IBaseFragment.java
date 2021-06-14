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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.ganguo.library.mvp.fragment_handle.FragmentBackHandleInterface;
import io.ganguo.library.mvp.fragment_handle.FragmentHandleBackUtil;
import io.ganguo.library.mvp.ui.mvp.IPresenter;
import io.ganguo.library.mvp.ui.mvp.base.delegate.IFragment;
import io.ganguo.library.mvp.ui.mvp.integration.cache.Cache;
import io.ganguo.library.mvp.ui.mvp.integration.cache.CacheType;
import io.ganguo.library.mvp.util.CBaseUtils;

/**
 * ================================================
 * 因为 Java 只能单继承, 所以如果要用到需要继承特定 @{@link Fragment} 的三方库, 那你就需要自己自定义 @{@link Fragment}
 * 继承于这个特定的 @{@link Fragment}, 然后再按照 {@link IBaseFragment} 的格式, 将代码复制过去, 记住一定要实现{@link IFragment}
 * ================================================
 */
public abstract class IBaseFragment<P extends IPresenter> extends Fragment implements IFragment, FragmentBackHandleInterface {
    protected final String TAG = this.getClass().getSimpleName();
    private Cache<String, Object> mCache;
    protected Context mHostContext;

    protected View rootView;

    private boolean isPrepared = false;
    private boolean isVisible = false;

    // 数据是否加载过
    private boolean isDataLoaded;

    @Inject
    protected P mPresenter;

    @NonNull
    @Override
    public synchronized Cache<String, Object> provideCache() {
        if (mCache == null) {
            mCache = CBaseUtils.obtainAppComponentFromContext(getActivity()).cacheFactory().build(CacheType.FRAGMENT_CACHE);
        }
        return mCache;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHostContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPrepared = false;
        isVisible = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isDataLoaded = false;

        rootView = inflater.inflate(getLayoutResourceId(), container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();

        if (!isPrepared && getUserVisibleHint()) {
            onVisibilityChange(true);
            isVisible = true;
        }
    }

    /**
     * see  FragmentDelegateImpl.onActivityCreated
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDataLoaded = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHostContext = null;
    }

    /**
     * Warn:this method is invoked before onCreateView
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (rootView == null) {
            return;
        }
        isPrepared = true;
        if (isVisibleToUser) {
            onVisibilityChange(true);
            isVisible = true;
            return;
        }
        if (isVisible) {
            onVisibilityChange(false);
            isVisible = false;
        }
    }

    private void onVisibilityChange(boolean isVisible){
        onFragmentVisibleChange(isVisible);

        if(isVisible && !isDataLoaded){
            isDataLoaded = true;
            onlyInvokedOnceWhenVisible();
        }
    }

    public void onFragmentVisibleChange(boolean isVisible){
    }

    /**
     * 仅在可见时被调用一次
     */
    public void onlyInvokedOnceWhenVisible(){
    }

    protected @Nullable View getRootView(){
        return rootView;
    }

    @Override
    public boolean onBackPressed() {
        return FragmentHandleBackUtil.handleBackPress(this);
    }
}
