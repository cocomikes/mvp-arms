package io.ganguo.library.mvp.util;

import androidx.core.content.FileProvider;

/**
 * Created by mikes on 2017/4/26.
 */
public final class FileProvider4UtilCode extends FileProvider {

    @Override
    public boolean onCreate() {
        AppUtils.init(getContext());
        return true;
    }
}
