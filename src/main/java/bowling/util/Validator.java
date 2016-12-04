package bowling.util;

/**
 * バリデータークラス
 *
 * Created by bassyMac on 2016/12/04.
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
}
