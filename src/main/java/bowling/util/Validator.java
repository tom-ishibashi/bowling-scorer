package bowling.util;

import bowling.Entity.Fail;
import bowling.Entity.Success;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * バリデータークラス
 *
 */
public class Validator {

    /**
     * 数値かどうかチェック
     *
     * @param value
     * @return
     */
   public static boolean isNumber(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
   }

    /**
     * 利用可能な数値の範囲かチェック
     *
     * @param value
     * @return
     */
   public static boolean isAvailableRange(int value) {
       if (0 <= value && value <= 10) {
            return true;
       }
       return false;
   }

    /**
     * パラメータの合計値が有効かをチェック
     *
     * @param value1
     * @param value2
     * @return
     */
   public static boolean isValidSumValues(int value1, int value2) {
        if (value1 + value2 > 10) {
            return false;
        }
       return true;
   }

    /**
     * パラメータが有効な文字列かチェック
     *
     * @param value
     * @return
     */
   public static boolean isValidFailedFirstThrowCharType(String value) {
       if (!Fail.GUTTER.getMark().equals(value) &&
               !Fail.FOUL.getMark().equals(value)) {
           return false;
       }
       return true;
   }

    /**
     * パラメータが有効な文字列かチェック
     *
     * @param value
     * @return
     */
    public static boolean isValidFailedSecondThrowCharType(String value) {
        if (!Fail.GUTTER_HYPHEN.getMark().equals(value) &&
                !Fail.FOUL.getMark().equals(value)) {
            return false;
        }
        return true;
    }

    /**
     * パラメータが有効な文字列かチェック
     *
     * @param value
     * @return
     */
    public static boolean isValidSpareCharType(String value) {
        if (!Success.SPARE.getMark().equals(value)) {
            return false;
        }
        return true;
    }

    /**
     * パラメータが有効な文字列かチェック
     *
     * @param value
     * @return
     */
    public static boolean isValidStrikeCharType(String value) {
        if (!Success.STRIKE.getMark().equals(value) &&
                !Success.STRIKE_LOWER.getMark().equals(value)) {
            return false;
        }
        return true;
    }

    /**
     * 使用可能文字種のチェック
     *
     * <p>
     *     1文字でも使用不可能な文字が含まれている場合falseを返す。
     * </p>
     *
     *
     * @param value
     * @return
     */
    public static boolean isValidCharType(String value) {

        Pattern p = Pattern.compile("(\\d*|\\p{Upper}*|\\p{Lower}|[/-])");
        Matcher m = p.matcher(value);
        return m.matches();
    }
}
