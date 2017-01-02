package bowling.Entity;

import java.util.Arrays;
import java.util.List;

/**
 * 投球失敗を表すenum
 */
public enum Fail {
    GUTTER(1, "G"),
    GUTTER_HYPHEN(2, "-"),
    FOUL(3, "F");

    private int code;
    private String mark;
    private static final List<Fail> values = Arrays.asList(Fail.values());

    Fail(int code, String mark) {
        this.code = code;
        this.mark = mark;
    }

    public int getCode() {
        return code;
    }

    public String getMark() {
        return mark;
    }

    public static Fail getFail(String mark) {
        return values
                .stream()
                .filter(e -> e.getMark().equals(mark))
                .findFirst()
                .get();
    }

    public static Fail getFail(int code) {
        return values
                .stream()
                .filter(e -> e.getCode() == code)
                .findFirst()
                .get();
    }
}
