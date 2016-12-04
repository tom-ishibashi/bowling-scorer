package bowling.service;

import bowling.model.Frame;
import bowling.model.Pin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 入力のサービスクラス
 *
 * Created by bassyMac on 2016/12/03.
 */
public class InputPinService {

    public Frame inputPin(int frameNo) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<Pin> pins = new ArrayList<Pin>();

        int throwCount = 2;
        if (frameNo == 10) {
            throwCount ++;
        }

        try {
            System.out.println(frameNo + "フレーム");
            for(int i = 1; i <= throwCount; i++) {
                System.out.println(i + "投目のピン数を入力してください");
                String count = br.readLine();

                Pin pin = new Pin();
                pin.setCount(Integer.parseInt(count));
                pins.add(pin);
            }
        } catch(IOException ie) {
            throw ie;
        }

        Frame frame = new Frame();
        frame.setFrame(frameNo);
        frame.setPins(pins);
        return frame;
    }
}
