package bowling.service;

import bowling.model.Frame;
import bowling.model.Pin;
import bowling.util.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * スコア計算のクラス
 *
 */
public class CalculateService {

    /**
     * スコアを計算します。
     *
     * @param frames
     * @return
     */
    public void calculateScore(List<Frame> frames) {
        // 最新フレームを取得
        Frame latestFrame = frames.get(frames.size() - 1);
        int latestFrameNo = latestFrame.getFrameNo();

        // ストライクの計算
        System.out.println("=====calculate strike start=====");
        int strikeIndex = 0;
        for(Frame frame: frames) {
            System.out.println("strikeIndex = " + strikeIndex);
            System.out.println("frameNo = " + frame.getFrameNo());
            // 1投目が10かつスコアが未計算のフレームの場合ストライクの計算を行う
            if (frame.getFirstPinCount() == 10 && frame.getScore() == 0) {
                int strikeFrameScore = frame.getFirstPinCount();

                if (frames.size() - 1 > strikeIndex + 1) {
                    // 次のフレームの1投目と2投目のピン数を取得する
                    Frame nextFrame = frames.get(strikeIndex + 1);
                    System.out.println("nextFrameNo = " + nextFrame.getFrameNo());

                    int nextFrameFirst = nextFrame.getFirstPinCount();
                    int nextFrameSecond = nextFrame.getSecondPinCount();

                    // 1投目が10未満の場合、1投目と2投目を合計する。
                    if (nextFrameFirst < 10) {
                        strikeFrameScore += nextFrameFirst + nextFrameSecond;

                        // 前フレームのスコアと合計する
                        if (strikeIndex -1 > 0) {
                            Frame prevFrame = frames.get(strikeIndex - 1);
                            System.out.println("prevFrameNo = " + prevFrame.getFrameNo());

                            strikeFrameScore += prevFrame.getScore();
                            frame.setScore(strikeFrameScore);
                        }

                    } else {
                        // 1投目がストライクの場合、かつその次のフレームが存在する場合、その次のフレームの1投目のピン数を取得する
                        if (frames.size() - 1 >= strikeIndex + 2) {
                            Frame afterNextFrame = frames.get(strikeIndex + 2);
                            System.out.println("afterNextFrameNo = " + afterNextFrame.getFrameNo());

                            int afterNextFrameFirst = afterNextFrame.getFirstPinCount();
                            strikeFrameScore += nextFrameFirst + afterNextFrameFirst;

                            // 前フレームのスコアと合計する
                            if (strikeIndex -1 >= 0) {
                                Frame prevFrame = frames.get(strikeIndex - 1);
                                System.out.println("prevFrameNo = " + prevFrame.getFrameNo());

                                strikeFrameScore += prevFrame.getScore();
                            }
                            frame.setScore(strikeFrameScore);
                        }
                    }
                }
            }
            strikeIndex++;
        }
        System.out.println("=====calculate strike end=====");


        // ストライクではない場合、1つ前のフレームのスコアに合計する
        if (latestFrame.getFirstPinCount() < 10) {
            int score = sumPinCount(latestFrame.getPins());
            int prevScore = 0;

            if (0 <= frames.size() - 2) {
                Frame prevFrame = frames.get(frames.size() - 2);
                prevScore = prevFrame.getScore();
            }

            // 最新フレームのスコア
            int result = prevScore + score;
            latestFrame.setScore(result);
        }
    }

    /**
     * 最終フレームのスコアを計算します
     *
     * @param frames
     */
    public void calculateLastFrame(List<Frame> frames) {

        Frame frame = frames.get(frames.size() - 1);
        List<Pin> pins = frame.getPins();
        frame.setScore(frame.getScore() + pins.get(2).getCount());
    }

    /**
     * ピン数のチェック
     * @param pins
     * @return
     */
    public boolean validateSumCounts(List<Pin> pins) {

        return Validator
                .isValidSumValues(pins.get(0).getCount(), pins.get(1).getCount());
    }

    /**
     * 2投を合計します。
     *
     * @param pins
     * @return
     */
    private int sumPinCount(List<Pin> pins) {
        int count = 0;
        count += pins.get(0).getCount();
        count += pins.get(1).getCount();
        return count;
    }


    private void calculateStrike(Frame strikeFrame, Frame nextFrame, Frame afterNextFrame) {

    }
}
