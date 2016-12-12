package bowling.service;

import bowling.model.Frame;
import bowling.model.Pin;

import java.util.List;

/**
 * スコア計算のクラス
 */
public class CalculateService {

    private static final int STRIKE = 10;
    private static final int SPARE = 10;
    private static final int FIRST_THROW = 1;
    private static final int SECOND_THROW = 2;
    private static final int THIRD_THROW = 3;

    /**
     * スコアを計算します
     *
     * @param frames       前フレームまでのフレームリスト
     * @param currentFrame 現在投球中のフレーム
     */
    public void calculateScore(List<Frame> frames, Frame currentFrame) {

        // 前フレームのindex取得
        int lastFrameIndex = frames.size() - 1;

        // ストライクの計算
        calculateStrike(frames, currentFrame);

        // スペアの計算
        calculateSpare(frames, currentFrame);

        // 2投目を入力してない場合はスキップする
        if (currentFrame.getPins().size() != 2) {
            return;
        }

        // ストライクではないかつスペアではない場合、または10フレームの3投目ではない場合1つ前のフレームのスコアに合計する
        if (!currentFrame.isStrike() &&
                !currentFrame.isSpare() &&
                (currentFrame.getFrameNo() == 10 && currentFrame.getPins().size() != 3)) {

            int score = sumPinCount(currentFrame.getPins());

            // 1つ前のフレームが無い場合最新スコアは0とする
            int lastScore = 0;
            if (0 <= lastFrameIndex) {
                Frame lastFrame = frames.get(lastFrameIndex);
                lastScore = lastFrame.getScore();
            }

            // 現在のフレームのスコア
            currentFrame.setScore(lastScore + score);
        }
    }

    /**
     * ストライクの計算を行う
     *
     * <p>
     * 入力中のフレームを基準に過去フレームのストライクの計算を行う
     * </p>
     *
     * @param frames
     * @param currentFrame
     */
    private void calculateStrike(List<Frame> frames, Frame currentFrame) {

        // 前フレームのindex取得
        int lastFrameIndex = frames.size() - 1;

        // 2つ前までのフレームが無い場合スキップ
        if (lastFrameIndex < 0 || lastFrameIndex - 1 < 0) {
            return;
        }

        // ストライクの計算
        switch (currentFrame.getPins().size()) {
            case FIRST_THROW:
                calcStrikeFirstThrow(frames, currentFrame);
                break;
            case SECOND_THROW:
                calcStrikeSecondThrow(frames, currentFrame);
                break;
            case THIRD_THROW:
                calcStrikeThirdThrow(frames, currentFrame);
                break;
        }
    }

    /**
     * 現在フレームの1投目を基準にストライクの計算を行います
     *
     * @param frames
     * @param currentFrame
     */
    private void calcStrikeFirstThrow(List<Frame> frames, Frame currentFrame) {

        int lastFrameIndex = frames.size() - 1;

        Frame lastFrame = frames.get(lastFrameIndex);
        Frame secondLastFrame = frames.get(lastFrameIndex - 1);

        if (lastFrame.isStrike() &&
                secondLastFrame.isStrike() &&
                secondLastFrame.getScore() == 0) {

            // 3つ前のフレームが無い場合最新スコアは0とする
            int lastScore = 0;
            if (lastFrameIndex - 2 >= 0) {
                lastScore = frames.get(lastFrameIndex - 2).getScore();
            }
            secondLastFrame.setScore(lastScore + STRIKE + STRIKE + currentFrame.getFirstPinCount());
        }

    }

    /**
     * 現在フレームの２投目を基準にストライクの計算を行います
     *
     * @param frames
     * @param currentFrame
     */
    private void calcStrikeSecondThrow(List<Frame> frames, Frame currentFrame) {

        int lastFrameIndex = frames.size() - 1;

        // 9フレームまでで、1投目がストライクの時はスキップ
        if (currentFrame.getFrameNo() != 10 && currentFrame.isStrike()) {
            return;
        }

        Frame lastFrame = frames.get(lastFrameIndex);
        if (lastFrame.isStrike() &&
                lastFrame.getScore() == 0) {

            // 2つ前のフレームが無い場合最新スコアは0とする
            int lastScore = 0;
            if (lastFrameIndex - 1 >= 0) {
                lastScore = frames.get(lastFrameIndex - 1).getScore();
            }
            lastFrame.setScore(lastScore + STRIKE + currentFrame.getFirstPinCount() + currentFrame.getSecondPinCount());
        }
    }

    /**
     * 現在フレームの3投目を基準にストライクの計算を行います
     * このメソッドは10フレーム目の場合に使用される想定です
     *
     * @param frames
     * @param currentFrame
     */
    private void calcStrikeThirdThrow(List<Frame> frames, Frame currentFrame) {

        int lastFrameIndex = frames.size() - 1;

        if (currentFrame.isStrike()) {
            int lastScore = frames.get(lastFrameIndex).getScore();

            currentFrame.setScore(lastScore + STRIKE + currentFrame.getSecondPinCount() + currentFrame.getThirdPinCount());
        }
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

    /**
     * スペアの計算を行います
     *
     * @param frames
     * @param currentFrame
     */
    private void calculateSpare(List<Frame> frames, Frame currentFrame) {

        int lastFrameIndex = frames.size() - 1;

        if (currentFrame.getFrameNo() == 10 && currentFrame.getPins().size() == 3) {
            calculateSpare10Frame(frames, currentFrame);
            return;
        }

        // 現在フレームの2投目以降の場合はスキップ
        if (currentFrame.getPins().size() > 1) {
            return;
        }

        // 1つ前のフレームが無い場合スキップ
        if (lastFrameIndex < 0) {
            return;
        }

        Frame lastFrame = frames.get(lastFrameIndex);
        if (lastFrame.isSpare()) {

            int lastScore = 0;
            if (lastFrameIndex - 1 >= 0) {
                lastScore = frames.get(lastFrameIndex - 1).getScore();
            }
            lastFrame.setScore(lastScore + SPARE + currentFrame.getFirstPinCount());
        }
    }

    /**
     * 10フレーム目のスペアを計算します。
     *
     * @param frames
     * @param currentFrame
     */
    private void calculateSpare10Frame(List<Frame> frames, Frame currentFrame) {

        int lastFrameIndex = frames.size() - 1;

        if (currentFrame.isSpare()) {
            Frame lastFrame = frames.get(lastFrameIndex);
            int lastScore = lastFrame.getScore();
            currentFrame.setScore(lastScore + SPARE + currentFrame.getThirdPinCount());
        }
    }
}