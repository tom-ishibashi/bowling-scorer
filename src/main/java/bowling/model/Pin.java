package bowling.model;

/**
 * 倒したピンの数を保持するクラス
 *
 */
public class Pin {
    private int count;

    public Pin() {
        this.count = 0;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
