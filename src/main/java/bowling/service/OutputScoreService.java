package bowling.service;

import bowling.model.Frame;
import bowling.model.Pin;

import java.util.List;

public class OutputScoreService {

    private static final String PIPE = "|";
    private static final String STRIKE = "X";
    private static final String SPARE = "/";
    private static final String GUTTER = "G";
    private static final String FAUL = "F";
    private static final String HALF_SPACE = " ";

    /**
     * スコアを出力します
     *
     * @param frames
     */
    public void outputScore(List<Frame> frames) {

        StringBuilder frameNoRow = createFrameRow(frames);
        StringBuilder pinRow = createPinRow(frames);
        StringBuilder scoreRow = createScoreRow(frames);

        System.out.println(frameNoRow.toString());
        System.out.println(pinRow.toString());
        System.out.println(scoreRow.toString());
    }

    /**
     * フレーム番号の行を作成
     *
     * @param frames
     * @return
     */
    private StringBuilder createFrameRow(List<Frame> frames) {
        StringBuilder frameNoRow = new StringBuilder();
        frames.forEach(frame -> {
            frameNoRow.append(PIPE);
            frameNoRow.append(String.format("%3d",frame.getFrameNo()));
        });
        frameNoRow.append(PIPE);
        return frameNoRow;
    }

    /**
     * ピン数の行を作成
     *
     * @param frames
     * @return
     */
    private StringBuilder createPinRow(List<Frame> frames) {
        StringBuilder pinRow = new StringBuilder();
        frames.forEach(frame -> {
            pinRow.append(PIPE);

            List<Pin> pins = frame.getPins();
            int pin1 = pins.get(0).getCount();
            int pin2 = pins.get(1).getCount();
            pinRow.append(pin1);
            pinRow.append(PIPE);
            pinRow.append(pin2);

            if (frame.getFrameNo() == 10) {
                int pin3 = pins.get(2).getCount();
                pinRow.append(pin3);
            }
        });
        pinRow.append(PIPE);
        return pinRow;
    }

    /**
     * スコアの行を作成
     *
     * @param frames
     * @return
     */
    private StringBuilder createScoreRow(List<Frame> frames) {
        StringBuilder scoreRow = new StringBuilder();
        frames.forEach(frame -> {
            scoreRow.append(PIPE);
            scoreRow.append(String.format("%3d", frame.getScore()));
        });
        scoreRow.append(PIPE);
        return scoreRow;
    }
}
