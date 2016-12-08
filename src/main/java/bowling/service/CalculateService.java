package bowling.service;

import bowling.model.Frame;
import bowling.model.Pin;
import bowling.util.Validator;

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

        // 最新のフレームを取得し、1つ前のフレームのスコアに合計する
        Frame frame = frames.get(frames.size() - 1);

        int score = sumPinCount(frame.getPins());
        int prevScore = 0;

        if (0 <= frames.size() - 2) {
            Frame prevFrame = frames.get(frames.size() - 2);
            prevScore = prevFrame.getScore();
        }

        // 最新フレームのスコア
        int result = prevScore + score;
        frame.setScore(result);
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

    private int sumPinCount(List<Pin> pins) {
        int count = 0;
        count += pins.get(0).getCount();
        count += pins.get(1).getCount();
        return count;
    }
}
