package bowling.controller;

import bowling.model.Frame;
import bowling.service.CalculateService;
import bowling.service.InputPinService;

import java.io.IOException;

/**
 * ボウリングのスコアラー
 */
public class Scorer {

    private InputPinService inputPinService;
    private CalculateService calculateService;

    public Scorer() {
        inputPinService = new InputPinService();
        calculateService = new CalculateService();
    }

    public void execute() {
        try {

            for (int i = 1; i <= 10; i++) {

                while (true) {
                    Frame frame = inputPinService.inputPinCount(i);

                    int result = calculateService.calculateScore(frame);
                    if (result > 0) {
                        break;
                    }
                }

                if (i == 10) {
                    // TODO 10フレーム目の3投目用あとで実装
                }
            }

        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
