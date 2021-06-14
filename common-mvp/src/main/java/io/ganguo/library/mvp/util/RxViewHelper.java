package io.ganguo.library.mvp.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Created by Mikes at 2019/4/12
 */
public class RxViewHelper {
    public static Observable<View> createButtonClickObservable(final View button) {
        Observable<View> buttonClickObservable =
                Observable.create(emitter -> {
                    button.setOnClickListener(emitter::onNext);

                    emitter.setCancellable(() -> button.setOnClickListener(null));
                });

        return buttonClickObservable
                .throttleFirst(1, TimeUnit.SECONDS);
    }

    public static Observable<String> createIMESearchClickObservable(final EditText queryEditText) {
        Observable<String> imeSearchClickObservable = Observable.create(emitter -> {
            queryEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    emitter.onNext(queryEditText.getText().toString());
                }
                return false;
            });

            emitter.setCancellable(() -> queryEditText.setOnEditorActionListener(null));
        });

        return imeSearchClickObservable
                .filter(keyword -> keyword.trim().length() > 0)
                .debounce(1000, TimeUnit.MILLISECONDS);
    }

    public static Observable<String> createTextChangeObservable(final EditText queryEditText) {
        Observable<String> textChangeObservable = Observable.create(emitter -> {
            final TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    emitter.onNext(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            queryEditText.addTextChangedListener(watcher);

            emitter.setCancellable(() -> queryEditText.removeTextChangedListener(watcher));
        });

        return textChangeObservable
                .filter(keyword -> keyword.trim().length() > 0)
                .debounce(1000, TimeUnit.MILLISECONDS);
    }

}
