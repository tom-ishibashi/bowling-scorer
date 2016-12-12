package bowling.service;

import bowling.model.Frame;
import bowling.model.Pin;
import bowling.util.Validator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 入力のサービスクラス
 *
 */
public class InputPinService {

    private BufferedReader br;

    public InputPinService() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * ピン数を入力し、パラメータのフレームに格納します
     *
     * @param frame
     * @return
     * @throws IOException
     */
    public Frame inputPinCount(Frame frame) throws IOException {

        Pin pin;
        while (true) {
            String count = br.readLine();

            pin = new Pin();
            if (validateInputValue(count)) {
                pin.setCount(Integer.parseInt(count));
                frame.getPins().add(pin);

                // 10フレーム目で1投目がストライク、または2投目でスペアの場合、合計値チェックをスキップ
                if(frame.getFrameNo() == 10 &&
                        (frame.isStrike() ||
                        (frame.getPins().size() == 2 && frame.isSpare()))) {
                    break;
                }

                if (validateSumValues(frame.getPins())) {
                    break;
                } else {
                    frame.getPins().remove(frame.getPins().size() - 1);
                }
            }
        }
        return frame;
    }

    /**
     * 入力値を元にPinオブジェクトを生成します。
     *
     * @param count
     * @return
     */
    private Pin validateAndCreatePin(String count) {
        Pin pin = new Pin();
        if (validateInputValue(count)) {
            pin.setCount(Integer.parseInt(count));
        } else {
            pin = null;
        }
        return pin;
    }

    /**
     * 入力値のバリデーションを行います
     *
     * @param val
     * @return
     */
    private boolean validateInputValue(String val) {

        if (!Validator.isNumber(val)) {
            return false;
        }

        if (!Validator.isAvailableRange(Integer.parseInt(val))) {
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
            return Validator.isValidSumValues(pins.get(0).getCount(), pins.get(1).getCount());
        }
        return true;
    }
}
