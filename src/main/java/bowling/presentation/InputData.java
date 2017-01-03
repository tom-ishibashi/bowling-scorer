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
 * 入力のサービスクラス
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
     * @param frames
     * @return
     * @throws IOException
     */
    public void input(List<Frame> frames) throws IOException {

        // 最新フレームを取得
        Frame frame = frames.get(frames.size() - 1);
        Pin pin;

        while (true) {
            message.clear();
            String value = br.readLine();
            pin = new Pin();

            if (!validateCharType(value)) {
                message.forEach(System.out::println);
                continue;
            }

            if (isCharFail(value)) {
                if (validateCharTypeFail(value, frame)) {
                    int failCode = 0;
                    switch (Fail.getFail(value)) {
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
                        break;
                    }
                }
            } else if (isCharSuccess(value)) {
                if (validateCharTypeSuccess(value, frame)) {

                    int count = 0;
                    switch (Success.getSuccess(value)) {
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
                    break;
                }
            } else if (isNumber(value)) {
                if (validateInputValue(value)) {
                    pin.setCount(Integer.parseInt(value));
                    frame.getPins().add(pin);

                    // 10フレーム目の場合、1投目がストライク、または2投目でスペアの場合、合計値チェックをスキップ
                    if (frame.getFrameNo() == 10 &&
                            (frame.isStrike() || frame.isSpare())) {
                        break;
                    }

                    // 合計値チェック
                    if (validateSumValues(frame.getPins())) {
                        break;
                    } else {
                        frame.getPins().remove(frame.getThrownCount() - 1);
                    }
                }
            } else {
                message.add("入力できない文字が含まれています。");
            }
            message.forEach(System.out::println);
        }
    }

    /**
     * 入力値のバリデーションを行います
     *
     * @param val
     * @return
     */
    private boolean validateInputValue(String val) {

        if (!Validator.isNumber(val)) {
            message.add("数値は0~10のいずれかを入力してください。");
            return false;
        }

        if (!Validator.isAvailableRange(Integer.parseInt(val))) {
            message.add("数値は0~10の範囲で入力してください。");
            return false;
        }
        return true;
    }

    /**
     * 2投の合計値のバリデーションを行います
     * @param pins
     * @return
     */
    private boolean validateSumValues(List<Pin> pins) {

        if(pins.size() == 2) {
            if (!Validator.isValidSumValues(pins.get(0).getCount(), pins.get(1).getCount())) {
                message.add("2投の合計は10以内になるように入力してください。");
                return false;
            }
        }
        return true;
    }


    /**
     * 入力値のバリデーションを行います。
     *
     * @param val
     * @return
     */
    private boolean validateCharTypeSuccess(String val, Frame frame) {

        if (frame.getPins().size() == 0) {
            if (!Validator.isValidStrikeCharType(val)) {
                message.add("0~10, G, F, X, xのいずれかを入力してください。");
                return false;
            }
        } else {

            if (frame.getFrameNo() < 10) {
                if (!Validator.isValidSpareCharType(val)) {
                    message.add("0~10, -, F, /のいずれかを入力してください。");
                    return false;
                }

            } else {
                if (!Validator.isValidStrikeCharType(val) && !Validator.isValidSpareCharType(val)) {
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
     * @param val
     * @return
     */
    private boolean validateCharTypeFail(String val, Frame frame) {

        if (frame.getPins().size() == 0) {
            if (!Validator.isValidFailedFirstThrowCharType(val)) {
                message.add("0~10, G, F, X, xのいずれかを入力してください。");
                return false;
            }
        } else {
            if (!Validator.isValidFailedSecondThrowCharType(val)) {
                message.add("0~10, -, F, /のいずれかを入力してください。");
                return false;
            }
        }
        return true;
    }

    /**
     * 文字種チェックを行います。
     *
     * @param val
     * @return
     */
    private boolean validateCharType(String val) {
        if (!Validator.isValidCharType(val)) {
            message.add("文字種が不正です。");
            return false;
        }
        return true;
    }

    /**
     * 数字のみと一致するか判定します
     *
     * @param val
     * @return
     */
    private boolean isNumber(String val) {
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(val);
        return m.matches();
    }

    /**
     * 文字のみと一致するか判定します
     *
     * @param val
     * @return
     */
    private boolean isCharFail(String val) {
        Pattern p = Pattern.compile("[GF-]");
        Matcher m = p.matcher(val);
        return m.matches();
    }

    /**
     * 文字のみと一致するか判定します
     *
     * @param val
     * @return
     */
    private boolean isCharSuccess(String val) {
        Pattern p = Pattern.compile("[xX/]");
        Matcher m = p.matcher(val);
        return m.matches();
    }
}
