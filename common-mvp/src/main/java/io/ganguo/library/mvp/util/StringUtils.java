package io.ganguo.library.mvp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;

/**
 * Created by Wilson on 14-8-21.
 */
public class StringUtils {
    private final static Pattern emailer = Pattern
            .compile("[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?");
    private final static Pattern invalider =Pattern.compile("[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘； ：”“’。，、？]");

    private final static Pattern hongkongPhone = Pattern.compile("^\\+852\\d{8}$");
    private final static Pattern mainlandPhone = Pattern.compile("^\\+86((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$");

    private final static Pattern numberPattern = Pattern.compile("^[0-9]*$");

    public static String getNonNullString(CharSequence str) {
        if (isEmpty(str)) {
            return "";
        }
        return str.toString();
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.toString().trim().length() == 0;
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(CharSequence... str) {
        for (CharSequence s : str) {
            if (isEmpty(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(CharSequence... str) {
        for (CharSequence s : str) {
            if (isEmpty(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否含有非法字符
     *
     * @param text
     * @return
     */
    public static boolean isTextValid(String text) {
        if (text == null || text.trim().length() == 0)
            return false;
        return !invalider.matcher(text).matches();
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 判断是不是一个固定电话
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isMobileNumberValid(String phoneNumber) {
        boolean isValid = false;

        String expression = "0\\d{2,3}-\\d{5,9}|0\\d{2,3}-\\d{5,9}";
        CharSequence inputStr = phoneNumber;

        Pattern pattern = Pattern.compile(expression);

        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;

    }

    /**
     * 判断是不是一个手机号码
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        return isMainlandPhoneNumberValid(phoneNumber) || isHongkongPhoneNumberValid(phoneNumber);
    }

    public static boolean isMainlandPhoneNumberValid(String phoneNumber) {
        return mainlandPhone.matcher(phoneNumber).matches();
    }

    public static boolean isHongkongPhoneNumberValid(String phoneNumber) {
        return hongkongPhone.matcher(phoneNumber).matches();
    }

    /**
     * 判断是不是纯数字
     */
    public static boolean isOnlyNumber(String number){
        return numberPattern.matcher(number).matches();
    }

    /**
     * 判断是不是一样的
     *
     * @param src
     * @param target
     * @return
     */
    public static boolean equals(String src, String target) {
        if (isEmpty(src) || isEmpty(target))
            return false;
        return src.equals(target);
    }
}
