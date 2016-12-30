package bowling.presentation;

import bowling.model.Frame;

import java.util.List;

public class OutputData {

    private static final String PIPE = "|";
    private static final String STRIKE = "X";
    private static final String SPARE = "/";
    private static final String GUTTER = "G";
    private static final String FAUL = "F";
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

            int firstThrow = frame.getFirstPinCount();
            int secondThrow = frame.getSecondPinCount();
            pinRow.append(firstThrow);
            pinRow.append(PIPE);
            pinRow.append(secondThrow);

            if (frame.getFrameNo() == 10 && frame.getThrownCount() == 3) {
                int thirdThrow = frame.getThirdPinCount();
                pinRow.append(PIPE);
                pinRow.append(thirdThrow);

            } else if (frame.getFrameNo() == 10 && frame.getThrownCount() < 3) {
                pinRow.append(PIPE);
                pinRow.append(HALF_SPACE);
            }
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
}
