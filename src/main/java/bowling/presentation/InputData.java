package bowling.presentation;

import bowling.model.Frame;
import bowling.model.Pin;
import bowling.util.Validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 入力のサービスクラス
 *
 */
public class InputData {

    private BufferedReader br;

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
            String count = br.readLine();

            pin = new Pin();
            if (validateInputValue(count)) {
                pin.setCount(Integer.parseInt(count));
                frame.getPins().add(pin);

                // 10フレーム目で1投目がストライク、または2投目でスペアの場合、合計値チェックをスキップ
                if(frame.getFrameNo() == 10 &&
                        (frame.isStrike() ||
                                (frame.getThrownCount() == 2 && frame.isSpare()))) {
                    break;
                }

                if (validateSumValues(frame.getPins())) {
                    break;
                } else {
                    frame.getPins().remove(frame.getThrownCount() - 1);
                }
            }
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
