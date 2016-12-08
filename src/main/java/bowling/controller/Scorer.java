package bowling.controller;

import bowling.model.Frame;
import bowling.service.CalculateService;
import bowling.service.InputPinService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

            List<Frame> frames = new ArrayList<>();

            // 1フレームから10フレームまでループ
            for (int i = 1; i <= 10; i++) {
                Frame frame;

                while (true) {
                    frame = inputPinService.inputPinCount(i);

                    // 2投の合計値をチェック
                    if (!calculateService.validateSumCounts(frame.getPins())) {
                        continue;
                    }

                    frames.add(frame);
                    calculateService.calculateScore(frames);
                    if (frame.getScore() > 0) {
                        break;
                    } else {
                        frames.remove(frames.size() - 1);
                    }
                }

                // 10フレームの3投目
                if (i == 10) {
                    inputPinService.inputPinCount(frame);
                    calculateService.calculateLastFrame(frames);
                }
                System.out.println("スコア = " + frames.get(frames.size() - 1).getScore());
            }

        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
