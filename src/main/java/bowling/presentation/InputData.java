package bowling.presentation;

import bowling.Entity.Fail;
import bowling.Entity.Success;
import bowling.model.Frame;
import bowling.model.Pin;
import bowling.util.Validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * データ入力を行うクラス
 *
 */
public class InputData {

    private BufferedReader br;

    private List<String> message = new ArrayList<>();

    public InputData() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * ピン数を入力し、最新のフレームに格納します
     *
     * @param frame フレーム
     * @throws IOException IO例外
     */
    public void input(Frame frame) throws IOException {

        while (true) {
            message.clear();
            String value = br.readLine();

            // 入力文字種チェック
            if (!validateCharType(value)) {
                message.forEach(System.out::println);
                continue;
            }

            // 入力された文字種に応じて処理
            if (isCharFail(value)) {
                if (setAndValidFail(frame, value)) {
                    break;
                }
            } else if (isCharSuccess(value)) {
                if (setAndValidSuccess(frame, value)) {
                    break;
                }
            } else if (isNumber(value)) {
                if (setAndValidNumber(frame, value)) {
                    break;
                }
            } else {
                message.add("入力できない文字が含まれています。");
            }
            message.forEach(System.out::println);
        }
    }

    /**
     * 数値入力の場合にバリデーションとフレームへの設定を行う
     *
     * @param frame フレーム
     * @param input 入力値
     * @return バリデーションに問題なければtrue
     */
    private boolean setAndValidNumber(Frame frame, String input) {

        if (validateInputValue(input)) {
            Pin pin = new Pin();
            pin.setCount(Integer.parseInt(input));
            frame.getPins().add(pin);

            // 合計値チェック
            if (validateSumValues(frame)) {
                return true;
            } else {
                frame.getPins().remove(frame.getThrownCount() - 1);
            }
        }
        return false;
    }

    /**
     * ストライクおよびスペアの入力の場合にバリデーションとフレームへの設定を行う
     *
     * @param frame フレーム
     * @param input 入力値
     * @return バリデーションに問題なければtrue
     */
    private boolean setAndValidSuccess(Frame frame, String input) {

        if (validateCharTypeSuccess(input, frame)) {
            Pin pin = new Pin();
            int count = 0;
            switch (Success.getSuccess(input)) {
                case STRIKE:
                case STRIKE_LOWER:
                    count = 10;
                    break;
                case SPARE:
                    if (frame.getFrameNo() == 10 && frame.getThrownCount() == 2) {
                        count = 10 - frame.getSecondPinCount();
                    } else {
                        count = 10 - frame.getFirstPinCount();
                    }
                    break;
            }

            pin.setCount(count);
            frame.getPins().add(pin);

            // 合計値チェック
            if (validateSumValues(frame)) {
                return true;
            } else {
                frame.getPins().remove(frame.getThrownCount() - 1);
                return false;
            }
        }
        return false;
    }

    /**
     * 投球失敗の入力の場合にバリデーションとフレームへの設定を行う
     *
     * @param frame フレーム
     * @param input 入力値
     * @return バリデーションに問題なければtrue
     */
    private boolean setAndValidFail(Frame frame, String input) {

        if (validateCharTypeFail(input, frame)) {

            Pin pin = new Pin();
            int failCode = 0;
            switch (Fail.getFail(input)) {
                case GUTTER:
                    failCode = Fail.GUTTER.getCode();
                    break;
                case GUTTER_HYPHEN:
                    failCode = Fail.GUTTER_HYPHEN.getCode();
                    break;
                case FOUL:
                    failCode = Fail.FOUL.getCode();
                    break;
            }

            if (0 < failCode) {
                pin.setCount(0);
                pin.setFailCode(failCode);
                frame.getPins().add(pin);
                return true;
            }
        }
        return false;
    }

    /**
     * 入力値のバリデーションを行います
     *
     * @param value 入力値
     * @return チェックOKの場合true
     */
    private boolean validateInputValue(String value) {

        if (!Validator.isNumber(value)) {
            message.add("数値は0~10のいずれかを入力してください。");
            return false;
        }

        if (!Validator.isAvailableRange(Integer.parseInt(value))) {
            message.add("数値は0~10の範囲で入力してください。");
            return false;
        }
        return true;
    }

    /**
     * 2投の合計値のバリデーションを行います
     * @param frame フレーム
     * @return チェックOKの場合true
     */
    private boolean validateSumValues(Frame frame) {

        if (frame.getFrameNo() < 10) {
            if (frame.getPins().size() == 2) {
                if (!Validator.isValidSumValues(frame.getPins().get(0).getCount(), frame.getPins().get(1).getCount())) {
                    message.add("2投の合計は10以内になるように入力してください。");
                    return false;
                }
            }
        } else {
            if (frame.getPins().size() > 1) {

                // 1投目がストライクではない場合に、1投目と2投目の合計チェック
                if (!frame.isStrike()) {
                    if (!Validator.isValidSumValues(frame.getPins().get(0).getCount(), frame.getPins().get(1).getCount())) {
                        message.add("2投の合計は10以内になるように入力してください。");
                        return false;
                    }

                } else {

                    // 1投目ストライクかつ、3投投げたかつ、2投目が10以下の場合に、2投目と3投目の合計値チェック
                    if (frame.getThrownCount() == 3 && frame.getSecondPinCount() < 10) {
                        if (!Validator.isValidSumValues(frame.getPins().get(1).getCount(), frame.getPins().get(2).getCount())) {
                            message.add("2投の合計は10以内になるように入力してください。");
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }


    /**
     * 入力値のバリデーションを行います。
     *
     * @param value 入力値
     * @param frame フレーム
     * @return チェックOKの場合true
     */
    private boolean validateCharTypeSuccess(String value, Frame frame) {

        // 1投目の場合
        if (frame.getPins().size() == 0) {
            if (!Validator.isValidStrikeCharType(value)) {
                message.add("0~10, G, F, X, xのいずれかを入力してください。");
                return false;
            }

        // 2投目の場合
        } else if (frame.getPins().size() == 1) {

            if (frame.getFrameNo() < 10) {
                if (!Validator.isValidSpareCharType(value)) {
                    message.add("0~10, -, F, /のいずれかを入力してください。");
                    return false;
                }

            } else {

                if (!Validator.isValidStrikeCharType(value) && !Validator.isValidSpareCharType(value)) {
                    message.add("0~10, X, x, /, -, Fのいずれかを入力してください。");
                    return false;
                }
            }

        // 3投目の場合
        } else {

            // 2投目がストライクの場合
            if (frame.isStrike() && frame.getSecondPinCount() == 10) {
                if (!Validator.isValidStrikeCharType(value)) {
                    message.add("0~10, X, x, -, Fのいずれかを入力してください。");
                    return false;
                }

            // 2投目がストライク以外の場合
            } else {

                if (!Validator.isValidSpareCharType(value)) {
                    message.add("0~10, X, x, /, -, Fのいずれかを入力してください。");
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 入力値のバリデーションを行います。
     *
     * @param value 入力値
     * @param frame フレーム
     * @return チェックOKの場合true
     */
    private boolean validateCharTypeFail(String value, Frame frame) {

        if (frame.getPins().size() == 0) {
            if (!Validator.isValidFailedFirstThrowCharType(value)) {
                message.add("0~10, G, F, X, xのいずれかを入力してください。");
                return false;
            }
        } else {
            if (!Validator.isValidFailedSecondThrowCharType(value)) {
                message.add("0~10, -, F, /のいずれかを入力してください。");
                return false;
            }
        }
        return true;
    }

    /**
     * 文字種チェックを行います。
     *
     * @param value 入力値
     * @return チェックOKの場合true
     */
    private boolean validateCharType(String value) {
        if (!Validator.isValidCharType(value)) {
            message.add("文字種が不正です。");
            return false;
        }
        return true;
    }

    /**
     * 数字のみと一致するか判定します
     *
     * @param value 入力値
     * @return 一致する場合true
     */
    private boolean isNumber(String value) {
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(value);
        return m.matches();
    }

    /**
     * 文字のみと一致するか判定します
     *
     * @param value 入力値
     * @return 一致する場合true
     */
    private boolean isCharFail(String value) {
        Pattern p = Pattern.compile("[GF-]");
        Matcher m = p.matcher(value);
        return m.matches();
    }

    /**
     * 文字のみと一致するか判定します
     *
     * @param value 入力値
     * @return 一致する場合true
     */
    private boolean isCharSuccess(String value) {
        Pattern p = Pattern.compile("[xX/]");
        Matcher m = p.matcher(value);
        return m.matches();
    }
}
