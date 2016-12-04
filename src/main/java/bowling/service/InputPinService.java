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
 * Created by bassyMac on 2016/12/03.
 */
public class InputPinService {

    /**
     * フレーム毎のピン数を入力します
     *
     * @param frameNo
     * @return
     * @throws IOException
     */
    public Frame inputPin(int frameNo) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<Pin> pins = new ArrayList<Pin>();

        int throwCount = 1;
        int maxThrowCount = 2;
        if (frameNo == 10) {
            maxThrowCount ++;
        }

        try {
            System.out.println(frameNo + "フレーム");
            while (true) {
                System.out.println(throwCount + "投目のピン数を入力してください");
                String count = br.readLine();

                if (validateInputValue(count)) {
                    Pin pin = new Pin();
                    pin.setCount(Integer.parseInt(count));
                    pins.add(pin);
                    throwCount++;
                } else {
                    continue;
                }

                if (throwCount > maxThrowCount) {
                    break;
                }
            }
        } catch(IOException ie) {
            throw ie;
        }

        Frame frame = new Frame();
        frame.setFrame(frameNo);
        frame.setPins(pins);
        return frame;
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
}
