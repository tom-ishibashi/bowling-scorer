package bowling.presentation;

import bowling.model.Frame;
import bowling.model.Pin;

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

        System.out.println(frameNoRow.toString());
        System.out.println(pinRow.toString());
        System.out.println(scoreRow.toString());
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

            List<Pin> pins = frame.getPins();
            int pin1 = pins.get(0).getCount();
            int pin2 = pins.get(1).getCount();
            pinRow.append(pin1);
            pinRow.append(PIPE);
            pinRow.append(pin2);

            if (frame.getFrameNo() == 10 && pins.size() == 3) {
                int pin3 = pins.get(2).getCount();
                pinRow.append(PIPE);
                pinRow.append(pin3);

            } else if (frame.getFrameNo() == 10 && pins.size() < 3) {
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
