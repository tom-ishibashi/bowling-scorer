package bowling.model;

import java.util.ArrayList;
import java.util.List;

/**
 * フレーム
 */
public class Frame {

    private int id;

    private int frameNo;

    private List<Pin> pins;

    private int score;

    public Frame() {
        this.frameNo = 0;
        this.score = 0;
        this.pins = new ArrayList<>();
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * フレームの1投目のピン数を取得します。
     *
     * @return 1投目のピン数
     */
    public int getFirstPinCount() {

        // 1投目がまだの場合0を返します。
        if (getPins().size() == 0) {
            return 0;
        }
        return getPins().get(0).getCount();
    }

    /**
     * フレームの2投目のピン数を取得します。
     *
     * @return 2投目のピン数
     */
    public int getSecondPinCount() {

        // 2投目がまだの場合0を返します。
        if (getPins().size() == 1) {
            return 0;
        }

        return getPins().get(1).getCount();
    }

    /**
     * フレームの3投目のピン数を取得します。
     * 10フレーム目ではない場合0を返します。
     *
     * @return 3投目のピン数
     */
    public int getThirdPinCount() {
        if (getFrameNo() != 10) {
            return 0;
        }

        // 3投目がまだの場合0を返します。
        if (getPins().size() == 2) {
            return 0;
        }

        return getPins().get(2).getCount();
    }

    /**
     * このフレームの1投目がストライクならtrueを返す
     *
     * @return ストライクならtrue
     */
    public boolean isStrike() {
        return getFirstPinCount() == 10;
    }

    /**
     * このフレームの1投目と2投目がスペアならtrueを返す
     *
     * @return スペアならtrue
     */
    public boolean isSpare() {
        return !isStrike() && getFirstPinCount() + getSecondPinCount() == 10;
    }

    /**
     * このフレームの2投目と3投目がスペアならtrueを返す
     *
     * <p>
     *     1投目と2投目でスペアの場合はfalseとなる。
     * </p>
     *
     * @return スペアならtrue
     */
    public boolean isSpareSecondAndThird() {
        return isStrike() && !isSpare() && getSecondPinCount() < 10 && getSecondPinCount() + getThirdPinCount() == 10;
    }

    /**
     * 投球回数を返す
     *
     * @return 回数
     */
    public int getThrownCount() {
        return getPins().size();
    }

    public int getFirstFailCode() {

        // 1投目がまだの場合0を返します。
        if (getPins().size() == 0) {
            return 0;
        }
        return getPins().get(0).getFailCode();
    }

    public int getSecondFailCode() {

        // 2投目がまだの場合0を返します。
        if (getPins().size() == 1) {
            return 0;
        }
        return getPins().get(1).getFailCode();
    }

    public int getThirdFailCode() {

        // 2投目がまだの場合0を返します。
        if (getPins().size() == 2) {
            return 0;
        }
        return getPins().get(2).getFailCode();
    }
}
