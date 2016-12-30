package bowling.service;

import bowling.dao.BaseDao;
import bowling.dao.FrameDao;
import bowling.dao.PinDao;
import bowling.model.Frame;
import bowling.model.Pin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * スコア計算のクラス
 */
public class ScoreCalculator {

    private static final int STRIKE = 10;
    private static final int SPARE = 10;

    private FrameDao frameDao;
    private PinDao pinDao;

    private int frameId = 0;

    public ScoreCalculator() {

        try {
            Connection con = BaseDao.getConnection();
            this.frameDao = new FrameDao(con);
            this.pinDao = new PinDao(con);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(9); // 異常終了
        }
    }

    /**
     * スコアを計算します
     *
     * @param frames 前フレームまでのフレームリスト
     */
    public void calculate(List<Frame> frames) throws SQLException {

        Frame currentFrame = getFrame(frames, Cursor.LAST);

        // フレームID採番
        if (frameId == 0) {
            frameId = getFrameDao().getNewId();
        }
        currentFrame.setId(frameId);

        // ストライクの計算
        calculateStrike(frames);

        // スペアの計算
        calculateSpare(frames);

        // ストライクとスペア以外の計算
        calculateNormal(frames);

        // 計算結果保存
        save(frames);
    }


    /**
     * 通常のスコア計算を行う
     *
     * <p>
     *     ストライクでもスペアでもない場合にスコアを計算します。
     * </p>
     *
     * @param frames フレーム
     */
    private void calculateNormal(List<Frame> frames) throws SQLException{

        // 最新のフレーム取得
        Frame currentFrame = getFrame(frames, Cursor.LAST);

        // ストライクではないかつスペアではない、2回以上投げている場合
        if (!currentFrame.isStrike() &&
                !currentFrame.isSpare() &&
                1 < currentFrame.getThrownCount()) {

            int score = sumPinCount(currentFrame);

            // 1つ前のフレームが無い場合最新スコアは0とする
            int lastScore = 0;
            if (0 <= getFrameIndex(frames, Cursor.SECOND_LAST)) {
                Frame lastFrame = getFrame(frames, Cursor.SECOND_LAST);
                lastScore = lastFrame.getScore();
            }

            // 現在のフレームのスコア
            currentFrame.setScore(lastScore + score);
        }
    }

    /**
     * 更新対象のフレームを取得する
     *
     * @param frameNo フレームNo
     * @param frames フレーム
     * @return 更新対象のフレーム
     */
    private Frame getUpdateTargetFrame(final int frameNo, List<Frame> frames) {
        return frames.stream().filter(f -> f.getFrameNo() == frameNo).findFirst().orElse(null);
    }

    /**
     * ストライクの計算を行う
     *
     * <p>
     * 入力中のフレームを基準に過去フレームのストライクの計算を行う
     * </p>
     *
     * @param frames フレームのリスト
     */
    private void calculateStrike(List<Frame> frames) throws SQLException {

        int updatedFrameNo = 0;

        // 2つ前までのフレームが無い場合スキップ
        if (getFrameIndex(frames, Cursor.THIRD_LAST) < 0 ||
                getFrameIndex(frames, Cursor.SECOND_LAST) < 0) {
            return;
        }

        // 投球に合わせてストライクを計算
        switch (Throwing.getThrowing(getFrame(frames, Cursor.LAST).getThrownCount())) {
            case FIRST:
                updatedFrameNo = calcStrikeFirstThrow(frames);
                break;
            case SECOND:
                updatedFrameNo = calcStrikeSecondThrow(frames);
                break;
            case THIRD:
                updatedFrameNo = calcStrikeThirdThrow(frames);
                break;
            default:
                throw new IllegalArgumentException("Throwing count is invalid");
        }

        // ストライクフレームの更新
        Frame strikeFrame = getUpdateTargetFrame(updatedFrameNo, frames);
        update(strikeFrame);
    }

    /**
     * 現在フレームの1投目を基準にストライクの計算を行います
     *
     * @param frames フレームのリスト
     * @return 更新対象のフレームNo
     */
    private int calcStrikeFirstThrow(List<Frame> frames) throws SQLException {

        int updateTarget = 0;

        Frame currentFrame = getFrame(frames, Cursor.LAST);
        Frame secondLastFrame = getFrame(frames, Cursor.SECOND_LAST);
        Frame thirdLastFrame = getFrame(frames, Cursor.THIRD_LAST);

        if (secondLastFrame.isStrike() &&
                thirdLastFrame.isStrike() &&
                thirdLastFrame.getScore() == 0) {

            // 3つ前のフレームが無い場合最新スコアは0とする
            int lastScore = 0;
            if (0 <= getFrameIndex(frames, Cursor.FOURTH_LAST)) {
                lastScore = getFrame(frames, Cursor.FOURTH_LAST).getScore();
            }
            thirdLastFrame.setScore(lastScore + STRIKE + STRIKE + currentFrame.getFirstPinCount());
            updateTarget = thirdLastFrame.getFrameNo();
        }
        return updateTarget;
    }

    /**
     * 現在フレームの２投目を基準にストライクの計算を行います
     *
     * @param frames フレームのリスト
     * @return 更新対象のフレームNo
     */
    private int calcStrikeSecondThrow(List<Frame> frames) throws SQLException {

        int updateTarget = 0;

        Frame currentFrame = getFrame(frames, Cursor.LAST);
        Frame secondLastFrame = getFrame(frames, Cursor.SECOND_LAST);

        // 9フレームまでで、1投目がストライクの時はスキップ
        if (currentFrame.getFrameNo() < 10 && currentFrame.isStrike()) {
            return updateTarget;
        }

        if (secondLastFrame.isStrike() &&
                secondLastFrame.getScore() == 0) {

            // 2つ前のフレームが無い場合最新スコアは0とする
            int lastScore = 0;
            if (0 <= getFrameIndex(frames, Cursor.THIRD_LAST)) {
                lastScore = getFrame(frames, Cursor.THIRD_LAST).getScore();
            }
            secondLastFrame.setScore(lastScore + STRIKE + currentFrame.getFirstPinCount() + currentFrame.getSecondPinCount());
            updateTarget = secondLastFrame.getFrameNo();
        }
        return updateTarget;
    }

    /**
     * 現在フレームの3投目を基準にストライクの計算を行います。
     * このメソッドは10フレーム目の場合に使用される想定です。
     * 10フレームの更新は不要なため戻り値は必ず0を返します。
     *
     * @param frames フレームのリスト
     * @return 更新対象のフレームNo
     */
    private int calcStrikeThirdThrow(List<Frame> frames) throws SQLException {

        Frame currentFrame = getFrame(frames, Cursor.LAST);

        if (currentFrame.isStrike()) {
            int lastScore = getFrame(frames, Cursor.SECOND_LAST).getScore();

            currentFrame.setScore(lastScore + STRIKE + currentFrame.getSecondPinCount() + currentFrame.getThirdPinCount());
        }
        return 0;
    }

    /**
     * 2投を合計します
     *
     * @param frame フレーム
     * @return 2投の合計
     */
    private int sumPinCount(Frame frame) {

        List<Pin> pins = frame.getPins();

        int sum = 0;
        sum += pins.get(0).getCount();
        sum += pins.get(1).getCount();
        return sum;
    }

    /**
     * スペアの計算を行います
     *
     * @param frames フレームのリスト
     */
    private void calculateSpare(List<Frame> frames) throws SQLException {

        int updatedFrameNo = 0;

        Frame currentFrame = getFrame(frames, Cursor.LAST);

        // 10フレームの3投目の場合
        if (currentFrame.getFrameNo() == 10 && currentFrame.getThrownCount() == 3) {
            updatedFrameNo = calculateSpare10Frame(frames);

            Frame spareFrame = getUpdateTargetFrame(updatedFrameNo, frames);
            update(spareFrame);
            return;
        }

        // 現在フレームの2投目以降の場合はスキップ
        if (currentFrame.getThrownCount() > 1) {
            return;
        }

        // 1つ前のフレームが無い場合スキップ
        if (getFrameIndex(frames, Cursor.SECOND_LAST) < 0) {
            return;
        }

        Frame secondLastFrame = getFrame(frames, Cursor.SECOND_LAST);
        if (secondLastFrame.isSpare()) {

            // 2つ前のフレームが無い場合最新スコアは0とする
            int lastScore = 0;
            if (0 <= getFrameIndex(frames, Cursor.THIRD_LAST)) {
                lastScore = getFrame(frames, Cursor.THIRD_LAST).getScore();
            }
            secondLastFrame.setScore(lastScore + SPARE + currentFrame.getFirstPinCount());
            updatedFrameNo = secondLastFrame.getFrameNo();
        }

        // スペアフレームの更新
        Frame spareFrame = getUpdateTargetFrame(updatedFrameNo, frames);
        update(spareFrame);
    }

    /**
     * 10フレーム目のスペアを計算します
     * このメソッドは10フレーム目の場合に使用される想定です
     * 10フレームの更新は不要なため戻り値は必ず0を返します。
     *
     * @param frames フレームのリスト
     * @return 更新対象のフレームNo
     */
    private int calculateSpare10Frame(List<Frame> frames) throws SQLException {

        Frame currentFrame = getFrame(frames, Cursor.LAST);

        if (currentFrame.isSpare()) {
            Frame secondLastFrame = getFrame(frames, Cursor.SECOND_LAST);
            int lastScore = secondLastFrame.getScore();
            currentFrame.setScore(lastScore + SPARE + currentFrame.getThirdPinCount());
        }
        return 0;
    }

    /**
     * 計算結果を保存します
     *
     * @param frames フレームのリスト
     */
    private void save(List<Frame> frames) throws SQLException {

        Frame currentFrame = getFrame(frames, Cursor.LAST);

        // 1~9フレームの場合
        if (currentFrame.getFrameNo() < 10) {

            // 2回投げてない場合スキップ
            if (currentFrame.getThrownCount() < 2) {
                return;
            }

        // 10フレームの場合
        } else {

            // ストライクではない、かつ2回投げてない場合スキップ
            if (!currentFrame.isStrike() && currentFrame.getThrownCount() < 2) {
                return;
            }

            // ストライクまたはスペア、かつ3回投げてない場合スキップ
            if ((currentFrame.isStrike() || currentFrame.isSpare()) &&
                    currentFrame.getThrownCount() < 3) {
                return;
            }
        }

        saveFrame(currentFrame);
        savePin(currentFrame);
    }

    /**
     * フレームを保存します
     *
     * @param frame フレーム
     */
    private void saveFrame(Frame frame) throws SQLException {

        getFrameDao().save(getFrameDao().convertToEntity(frame));
    }

    /**
     * ピンを保存します
     *
     * @param frame フレーム
     */
    private void savePin(Frame frame) throws SQLException {

        for (int i = 1; i <= frame.getPins().size(); i++) {
            bowling.Entity.Pin pinEntity = getPinDao().convertToEntity(frame, i);
            getPinDao().save(pinEntity);
        }
    }

    /**
     * フレームを更新します
     *
     * @param frame フレーム
     */
    private void update(Frame frame) throws SQLException {

        if (frame != null) {
            getFrameDao().update(getFrameDao().convertToEntity(frame));
        }
    }

    /**
     * 指定のフレームを取得します
     *
     * @param frames フレームのリスト
     * @param cursor カーソル
     * @return フレーム
     */
    private Frame getFrame(List<Frame> frames, Cursor cursor) {

        Frame frame;
        switch (cursor) {
            case LAST:
                frame = frames.get(frames.size() - Cursor.LAST.getRelativeNo());
                break;
            case SECOND_LAST:
                frame = frames.get(frames.size() - Cursor.SECOND_LAST.getRelativeNo());
                break;
            case THIRD_LAST:
                frame = frames.get(frames.size() - Cursor.THIRD_LAST.getRelativeNo());
                break;
            case FOURTH_LAST:
                frame = frames.get(frames.size() - Cursor.FOURTH_LAST.getRelativeNo());
                break;
            default:
                throw new IllegalArgumentException("Cursor is invalid");
        }
        return frame;
    }

    /**
     * フレームのリストのインデックスを取得します
     *
     * @param frames フレーム
     * @param cursor カーソル
     * @return インデックス
     */
    private int getFrameIndex(List<Frame> frames, Cursor cursor) {
        return frames.size() - cursor.getRelativeNo();
    }

    private FrameDao getFrameDao() {
        return frameDao;
    }

    private PinDao getPinDao() {
        return pinDao;
    }

    private enum Cursor {

        /** 現在 */
        LAST(1),
        /** 1つ前 */
        SECOND_LAST(2),
        /** 2つ前 */
        THIRD_LAST(3),
        /** 3つ前 */
        FOURTH_LAST(4);

        private int relativeNo;

        Cursor(int relativeNo) {
            this.relativeNo = relativeNo;
        }

        public int getRelativeNo() {
            return relativeNo;
        }
    }

    private enum Throwing {
        FIRST(1),
        SECOND(2),
        THIRD(3);

        private int count;

        Throwing(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public static Throwing getThrowing(int count) {
            return Arrays.stream(Throwing.values())
                    .filter(t -> t.getCount() == count)
                    .findFirst()
                    .get();
        }
    }

}