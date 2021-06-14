/*
 * Copyright © 2019 iFund. All rights reserved.
 */

package io.ganguo.library.mvp.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import java.text.NumberFormat;

/**
 * Created by mikes on 2017/12/26.
 */

public class MinValueInputFilter implements InputFilter {
    public interface CallBack{
        void breakMinValueInput();
    }

    private CallBack callBack;

    public void setCallBack(CallBack callBack){
        this.callBack = callBack;
    }

    private int minValue;
    private NumberFormat numberFormatter;
    private boolean withFormatter = false;

    public MinValueInputFilter(int minValue, boolean withFormatter) {
        this.minValue = minValue;
        this.withFormatter  = withFormatter;
        numberFormatter = NumberFormat.getNumberInstance();
        numberFormatter.setMaximumFractionDigits(0);
    }

    /**
     * @param source 新输入的字符串
     * @param start  新输入的字符串起始下标，一般为0
     * @param end    新输入的字符串终点下标，一般为source.length - 1
     * @param dest   输入之前文本框内容
     * @param dstart 原内容起始坐标，一般为0
     * @param dend   原内容终点坐标，一般为dest.length - 1
     * @return
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String sourceText = source.toString();
        String destText = dest.toString();

        //delete键
        if (TextUtils.isEmpty(sourceText)) {
            return "";
        }

        try {
            double sumText;
            if(withFormatter){
                sumText = numberFormatter.parse(destText + sourceText).doubleValue();
            } else{
                sumText = Double.parseDouble(destText + sourceText);
            }

            if (sumText < minValue) {
                if(callBack != null){
                    callBack.breakMinValueInput();
                }
                return dest.subSequence(dstart, dend);
            }
            return dest.subSequence(dstart, dend) + sourceText;
        } catch (Exception e) {
            Log.e("MinValueInputFilter", "", e);
        }

        return "";
    }
}
