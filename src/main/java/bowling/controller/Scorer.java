package bowling.controller;

import bowling.model.Frame;
import bowling.service.CalculateService;
import bowling.service.InputPinService;
import bowling.service.OutputScoreService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ボウリングのスコアラー
 */
public class Scorer {

    private InputPinService inputPinService;
    private CalculateService calculateService;
    private OutputScoreService outputScoreService;

    public Scorer() {
        inputPinService = new InputPinService();
        calculateService = new CalculateService();
        outputScoreService = new OutputScoreService();
    }

    public void execute() {
        try {

            List<Frame> frames = new ArrayList<>();

            // 1フレームから10フレームまでループ
            for (int i = 1; i <= 10; i++) {
                Frame frame = new Frame();
                frame.setFrameNo(i);
                System.out.println(i + "フレーム");

                System.out.println("1投目のピン数を入力してください");
                frame = inputPinService.inputPinCount(frame);
                calculateService.calculateScore(frames, frame);

                System.out.println("2投目のピン数を入力してください");
                frame = inputPinService.inputPinCount(frame);
                calculateService.calculateScore(frames, frame);

                // 10フレームの3投目
                if (i == 10 && (frame.isStrike() || frame.isSpare())) {
                    System.out.println("3投目のピン数を入力してください");
                    frame = inputPinService.inputPinCount(frame);
                    calculateService.calculateScore(frames, frame);
                }

                frames.add(frame);
                System.out.println("スコア = " + frame.getScore());
                outputScoreService.outputScore(frames);
            }

        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
