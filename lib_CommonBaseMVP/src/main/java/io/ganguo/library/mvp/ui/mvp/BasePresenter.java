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
package io.ganguo.library.mvp.ui.mvp;

import android.app.Service;
import android.view.View;

import io.ganguo.library.mvp.util.Preconditions;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import io.reactivex.functions.Action;

/**
 * ================================================
 * 基类 Presenter
 * ================================================
 */
public class BasePresenter<M extends IModel, V extends IView> implements IPresenter {
    protected final String TAG = this.getClass().getSimpleName();
    protected M mModel;
    protected V mRootView;

    protected LifecycleOwner lifecycleOwner;

    /**
     * 如果当前页面同时需要 Model 层和 View 层,则使用此构造函数(默认)
     *
     * @param model
     * @param rootView
     */
    public BasePresenter(M model, V rootView) {
        Preconditions.checkNotNull(model, "%s cannot be null", IModel.class.getName());
        Preconditions.checkNotNull(rootView, "%s cannot be null", IView.class.getName());
        this.mModel = model;
        this.mRootView = rootView;
        doInitialWork();
    }

    /**
     * 如果当前页面不需要操作数据,只需要 View 层,则使用此构造函数
     *
     * @param rootView
     */
    public BasePresenter(V rootView) {
        Preconditions.checkNotNull(rootView, "%s cannot be null", IView.class.getName());
        this.mRootView = rootView;
        doInitialWork();
    }

    public BasePresenter() {
        doInitialWork();
    }

    @Override
    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public void doInitialWork() {
        //将 LifecycleObserver 注册给 LifecycleOwner 后 @OnLifecycleEvent 才可以正常使用
        if (mRootView instanceof LifecycleOwner) {
            setLifecycleOwner((LifecycleOwner) mRootView);
            ((LifecycleOwner) mRootView).getLifecycle().addObserver(this);
        }
    }

    @Override
    public void onLifecycleChanged(LifecycleOwner owner, Lifecycle.Event event) {

    }

    /**
     * 只有当 {@code mRootView} 不为 null, 并且 {@code mRootView} 实现了 {@link LifecycleOwner} 时, 此方法才会被调用
     * 所以当您想在 {@link Service} 以及一些自定义 {@link View} 或自定义类中使用 {@code Presenter} 时
     * 您也将不能继续使用 {@link OnLifecycleEvent} 绑定生命周期
     *
     * @param owner
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(LifecycleOwner owner) {
        if (mModel != null){
            mModel.onDestroy();
            this.mModel = null;
        }
        this.mRootView = null;

        /**
         * 注意, 如果在这里调用了 {@link #onDestroy()} 方法, 会出现某些地方引用 {@code mModel} 或 {@code mRootView} 为 null 的情况
         * 比如在 {@link RxLifecycle} 终止 {@link Observable} 时, 在 {@link io.reactivex.Observable#doFinally(Action)} 中却引用了 {@code mRootView} 做一些释放资源的操作, 此时会空指针
         * 或者如果你声明了多个 @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY) 时在其他 @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
         * 中引用了 {@code mModel} 或 {@code mRootView} 也可能会出现此情况
         */
        owner.getLifecycle().removeObserver(this);
    }
}
