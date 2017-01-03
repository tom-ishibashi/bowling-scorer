package bowling.presentation;

import bowling.Entity.Fail;
import bowling.Entity.Success;
import bowling.model.Frame;

import java.util.List;

public class OutputData {

    private static final String PIPE = "|";
    private static final String HALF_SPACE = " ";

    /**
     * スコアを出力します
     *
     * @param frames フレームのリスト
     */
    public void outputScore(List<Frame> frames) {

        String frameNoRow = createFrameRow(frames);
        String pinRow = createPinRow(frames);
        String scoreRow = createScoreRow(frames);

        System.out.println(frameNoRow);
        System.out.println(pinRow);
        System.out.println(scoreRow);
    }

    /**
     * フレーム番号の行を作成
     *
     * @param frames フレームのリスト
     * @return フレーム番号の行
     */
    private String createFrameRow(List<Frame> frames) {
        StringBuilder frameNoRow = new StringBuilder();
        frames.forEach(frame -> {
            frameNoRow.append(PIPE);

            if (frame.getFrameNo() != 10) {
                frameNoRow.append(String.format("%3d",frame.getFrameNo()));

            } else {
                frameNoRow.append(String.format("%5d",frame.getFrameNo()));
            }
        });
        frameNoRow.append(PIPE);
        return frameNoRow.toString();
    }

    /**
     * ピン数の行を作成
     *
     * @param frames フレームのリスト
     * @return ピン数の行
     */
    private String createPinRow(List<Frame> frames) {
        StringBuilder pinRow = new StringBuilder();
        frames.forEach(frame -> {
            pinRow.append(PIPE);
            pinRow.append(showFirst(frame));
            pinRow.append(PIPE);
            pinRow.append(showSecond(frame));
            pinRow.append(showThird(frame));
        });
        pinRow.append(PIPE);
        return pinRow.toString();
    }

    /**
     * スコアの行を作成
     *
     * @param frames フレームのリスト
     * @return スコアの行
     */
    private String createScoreRow(List<Frame> frames) {
        StringBuilder scoreRow = new StringBuilder();
        frames.forEach(frame -> {
            scoreRow.append(PIPE);

            if (frame.getFrameNo() != 10) {
                scoreRow.append(String.format("%3d", frame.getScore()));

            } else {
                scoreRow.append(String.format("%5d", frame.getScore()));
            }
        });
        scoreRow.append(PIPE);
        return scoreRow.toString();
    }

    /**
     * 1投目の表示内容を返します。
     *
     * @param frame フレーム
     * @return 表示内容
     */
    private String showFirst(Frame frame) {

        if (frame.getFirstFailCode() == 0) {
            if (frame.getFirstPinCount() == 10) {
                return Success.STRIKE.getMark();

            } else if (frame.getFirstPinCount() == 0) {
                return Fail.GUTTER.getMark();

            } else {
                return String.valueOf(frame.getFirstPinCount());
            }
        }
        return Fail.getFail(frame.getFirstFailCode()).getMark();
    }

    /**
     * 2投目の表示内容を返します。
     *
     * @param frame フレーム
     * @return 表示内容
     */
    private String showSecond(Frame frame) {

        // 1~9フレームの場合
        if (frame.getFrameNo() < 10) {
            if (frame.isStrike()) {
                return HALF_SPACE;
            }

        // 10フレームの場合
        } else {
            if (frame.isStrike() && frame.getSecondPinCount() == 10) {
                return Success.STRIKE.getMark();
            }
        }

        if (frame.isSpare()) {
            return Success.SPARE.getMark();

        } else {
            if (frame.getSecondFailCode() == 0) {
                if (frame.getSecondPinCount() == 0) {
                    return Fail.GUTTER_HYPHEN.getMark();

                } else {
                    return String.valueOf(frame.getSecondPinCount());
                }
            }
        }

        return Fail.getFail(frame.getSecondFailCode()).getMark();
    }

    /**
     * 3投目の表示内容を返します。
     *
     * @param frame フレーム
     * @return 表示内容
     */
    private String showThird(Frame frame) {

        if (frame.getFrameNo() == 10 && frame.getThrownCount() == 3) {
            if (frame.isStrike() &&
                    frame.getSecondPinCount() == 10 &&
                    frame.getThirdPinCount() == 10) {
                return PIPE + Success.STRIKE.getMark();

            } else if (frame.isSpare() && frame.getThirdPinCount() == 10) {
                return PIPE + Success.STRIKE.getMark();

            } else if (frame.isSpareSecondAndThird()) {
                return PIPE + Success.SPARE.getMark();

            } else {
                if (frame.getThirdFailCode() == 0) {
                    if (frame.getThirdPinCount() == 0) {
                        return PIPE + Fail.GUTTER_HYPHEN.getMark();

                    } else {
                        return PIPE + String.valueOf(frame.getThirdPinCount());
                    }
                }
            }

            return PIPE + Fail.getFail(frame.getThirdFailCode()).getMark();

        } else if (frame.getFrameNo() == 10 && frame.getThrownCount() < 3) {
            return PIPE + HALF_SPACE;
        }

        return "";
    }
}
