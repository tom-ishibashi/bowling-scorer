package bowling.model;

import java.util.List;

/**
 * フレームクラス
 */
public class Frame {
    private int frameNo;

    private List<Pin> pins;

    private int score;

    public int getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(int frameNo) {
        this.frameNo = frameNo;
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


    /**
     * フレームの1投目のピン数を取得します。
     *
     * @return
     */
    public int getFirstPinCount() {
        return getPins().get(0).getCount();
    }

    /**
     * フレームの2投目のピン数を取得します。
     *
     * @return
     */
    public int getSecondPinCount() {
        return getPins().get(1).getCount();
    }

    /**
     * フレームの3投目のピン数を取得します。
     * 10フレーム目ではない場合0を返します。
     *
     * @return
     */
    public int getThirdPinCount() {
        if (getFrameNo() != 10) {
            return 0;
        }
        return getPins().get(2).getCount();
    }

}
