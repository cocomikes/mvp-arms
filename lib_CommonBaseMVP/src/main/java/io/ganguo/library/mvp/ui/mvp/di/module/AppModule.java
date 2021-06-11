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
package io.ganguo.library.mvp.ui.mvp.di.module;

import android.app.Application;

import io.ganguo.library.mvp.ui.mvp.integration.ActivityLifecycle;
import io.ganguo.library.mvp.ui.mvp.integration.FragmentLifecycle;
import io.ganguo.library.mvp.ui.mvp.integration.IRepositoryManager;
import io.ganguo.library.mvp.ui.mvp.integration.RepositoryManager;
import io.ganguo.library.mvp.ui.mvp.integration.cache.Cache;
import io.ganguo.library.mvp.ui.mvp.integration.cache.CacheType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import androidx.fragment.app.FragmentManager;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * ================================================
 * 提供一些框架必须的实例的 {@link Module}
 * ================================================
 */
@Module
public abstract class AppModule {
    @Binds
    abstract IRepositoryManager bindRepositoryManager(RepositoryManager repositoryManager);

    @Singleton
    @Provides
    static Cache<String, Object> provideExtras(Cache.Factory cacheFactory) {
        return cacheFactory.build(CacheType.EXTRAS);
    }

    @Binds
    @Named("ActivityLifecycle")
    abstract Application.ActivityLifecycleCallbacks bindActivityLifecycle(ActivityLifecycle activityLifecycle);

    @Binds
    abstract FragmentManager.FragmentLifecycleCallbacks bindFragmentLifecycle(FragmentLifecycle fragmentLifecycle);

    @Singleton
    @Provides
    static List<FragmentManager.FragmentLifecycleCallbacks> provideFragmentLifecycles() {
        return new ArrayList<>();
    }

}
