package io.ganguo.library.mvp.http.upload;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * author : Mikes
 * date : 2019/3/19 9:58
 * description :
 */
public abstract class FileUploadObserver<Result> implements Observer<Result> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(Result result) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }

    public void onProgressChange(long bytesWritten, long contentLength) {
        onProgress((int) (bytesWritten * 100 / contentLength));
    }

    public abstract void onProgress(int progress);
}
