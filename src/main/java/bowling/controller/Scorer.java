package bowling.controller;

import bowling.service.InputPinService;

import java.io.IOException;

/**
 * スコアラークラス
 *
 * Created by bassyMac on 2016/12/03.
 */
public class Scorer {
    public Scorer() {
    }

    public void execute() {

        try {
            InputPinService input = new InputPinService();

            for (int i = 1; i <= 10; i++) {
                input.inputPin(i);
            }

        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
