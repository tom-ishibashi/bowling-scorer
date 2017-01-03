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

            // 1フレームから10フレームまでループ
            for (int i = 1; i <= 10; i++) {
                List<Frame> frames;

                Frame currentFrame = new Frame();
                currentFrame.setFrameNo(i);

                System.out.println(i + "フレーム");
                System.out.println("1投目のピン数を入力(0~10, G, F, x, X)してください");
                inputData.input(currentFrame);
                frames = scoreCalculator.calculate(currentFrame);

                // 1~9フレームの場合
                if (i < 10) {
                    if (!currentFrame.isStrike()) {
                        System.out.println("2投目のピン数を入力(0~10, /, F, -)してください");
                        inputData.input(currentFrame);
                        frames = scoreCalculator.calculate(currentFrame);
                    }

                // 10フレームの場合
                } else {

                    System.out.println("2投目のピン数を入力(0~10, x, X, /, F, -)してください");
                    inputData.input(currentFrame);
                    frames = scoreCalculator.calculate(currentFrame);

                    if (currentFrame.isStrike() || currentFrame.isSpare()) {
                        System.out.println("3投目のピン数を入力(0~10, x, X, /, F, -)してください");
                        inputData.input(currentFrame);
                        frames = scoreCalculator.calculate(currentFrame);
                    }
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
