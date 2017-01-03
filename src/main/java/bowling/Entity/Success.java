package bowling.Entity;

import java.util.Arrays;
import java.util.List;

/**
 * ストライクとスペアを表すenum
 */
public enum Success {
    STRIKE("X"),
    STRIKE_LOWER("x"),
    SPARE("/");

    private String mark;
    private static final List<Success> values = Arrays.asList(Success.values());

    Success(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }

    public static Success getSuccess(String mark) {
        return values
                .stream()
                .filter(e -> e.getMark().equals(mark))
                .findFirst()
                .get();
    }
}
