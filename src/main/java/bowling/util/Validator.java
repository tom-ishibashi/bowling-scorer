package bowling.util;

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
            System.out.println("0~10の数値を入力してください。");
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
       System.out.println("0~10の数値を入力してください。");
       return false;
   }

    /**
     * パラメータの合計値が有効かをチェックします
     *
     * @param value1
     * @param value2
     * @return
     */
   public static boolean isValidSumValues(int value1, int value2) {
        if (value1 + value2 > 10) {
            System.out.println("2投の合計は10以内になるように入力してください。");
            return false;
        }
       return true;
   }
}
