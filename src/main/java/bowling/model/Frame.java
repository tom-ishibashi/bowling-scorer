package bowling.model;

import java.util.List;

/**
 * フレームクラス
 */
public class Frame {
    private int frame;

    private List<Pin> pins;

    private int score;

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public List<Pin> getPins() {
        return pins;
    }

    public void setPins(List<Pin> pins) {
        this.pins = pins;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
