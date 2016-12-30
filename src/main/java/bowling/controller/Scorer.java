package bowling.controller;

import bowling.model.Frame;
import bowling.presentation.InputData;
import bowling.presentation.OutputData;
import bowling.service.ScoreCalculator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ボウリングのスコアラー
 */
public class Scorer {

    private InputData inputData;
    private ScoreCalculator scoreCalculator;
    private OutputData outputData;

    public Scorer() {
        this.inputData = new InputData();
        this.scoreCalculator = new ScoreCalculator();
        this.outputData = new OutputData();
    }

    /**
     * 実行します
     */
    public void run() {
        try {

            List<Frame> frames = new ArrayList<>();

            // 1フレームから10フレームまでループ
            for (int i = 1; i <= 10; i++) {
                Frame frame = new Frame();
                frame.setFrameNo(i);
                frames.add(frame);

                System.out.println(i + "フレーム");

                System.out.println("1投目のピン数を入力してください");
                inputData.input(frames);
                scoreCalculator.calculate(frames);

                System.out.println("2投目のピン数を入力してください");
                inputData.input(frames);
                scoreCalculator.calculate(frames);

                // 10フレームの3投目
                if (i == 10 && (frame.isStrike() || frame.isSpare())) {
                    System.out.println("3投目のピン数を入力してください");
                    inputData.input(frames);
                    scoreCalculator.calculate(frames);
                }

                outputData.outputScore(frames);
            }

        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
