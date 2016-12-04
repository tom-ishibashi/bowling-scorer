package bowling.service;

import bowling.model.Frame;
import bowling.model.Pin;

import java.util.List;

/**
 * スコア計算のクラス
 *
 */
public class CalculateService {

    public int calculateScore(Frame frame) {

        if (frame.getFrame() == 10) {

        }
        if (!validateCounts(frame.getPins())) {
            return 0;
        }

        return 1; // todo エラー以外ということで1を返す。あとで実装する。
    }

    private boolean validateCounts(List<Pin> pins) {
        int count = 0;
        count += pins.get(0).getCount();
        count += pins.get(1).getCount();

        if (count > 10) {
            System.out.println("倒したピン数の合計が10を超えています。入力し直してください。");
            return false;
        }
        return true;
    }
}
