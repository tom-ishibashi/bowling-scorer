package bowling.service;

import bowling.dao.BaseDao;
import bowling.dao.FrameDao;
import bowling.dao.PinDao;
import bowling.model.Frame;
import bowling.model.Pin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * スコア計算のクラス
 */
public class Calculation {

    private static final int STRIKE = 10;
    private static final int SPARE = 10;
    private static final int FIRST_THROW = 1;
    private static final int SECOND_THROW = 2;
    private static final int THIRD_THROW = 3;

    private FrameDao frameDao;
    private PinDao pinDao;

    private int frameId = 0;

    public Calculation() {

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
    public void calculateScore(List<Frame> frames) throws SQLException {

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

        // 2投目を入力してない場合はスキップする
        if (currentFrame.getPins().size() != 2) {
            return;
        }

        // ストライクとスペア以外の計算
        calculateNormal(frames);
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

        // ストライクではないかつスペアではない場合
        if (!currentFrame.isStrike() && !currentFrame.isSpare()) {

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

        save(currentFrame);
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

        // ストライクの計算
        switch (getFrame(frames, Cursor.LAST).getPins().size()) {
            case FIRST_THROW:
                updatedFrameNo = calcStrikeFirstThrow(frames);
                break;
            case SECOND_THROW:
                updatedFrameNo = calcStrikeSecondThrow(frames);
                break;
            case THIRD_THROW:
                updatedFrameNo = calcStrikeThirdThrow(frames);
                break;
            default:
                break;
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
     * 現在フレームの3投目を基準にストライクの計算を行います
     * このメソッドは10フレーム目の場合に使用される想定です
     *
     * @param frames フレームのリスト
     * @return 更新対象のフレームNo
     */
    private int calcStrikeThirdThrow(List<Frame> frames) throws SQLException {

        int updateTarget = 0;

        Frame currentFrame = getFrame(frames, Cursor.LAST);

        if (currentFrame.isStrike()) {
            int lastScore = getFrame(frames, Cursor.SECOND_LAST).getScore();

            currentFrame.setScore(lastScore + STRIKE + currentFrame.getSecondPinCount() + currentFrame.getThirdPinCount());
            updateTarget = currentFrame.getFrameNo();
        }
        return updateTarget;
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
        if (currentFrame.getFrameNo() == 10 && currentFrame.getPins().size() == 3) {
            updatedFrameNo = calculateSpare10Frame(frames);

            Frame spareFrame = getUpdateTargetFrame(updatedFrameNo, frames);
            update(spareFrame);
            return;
        }

        // 現在フレームの2投目以降の場合はスキップ
        if (currentFrame.getPins().size() > 1) {
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
     *
     * @param frames フレームのリスト
     * @return 更新対象のフレームNo
     */
    private int calculateSpare10Frame(List<Frame> frames) throws SQLException {

        int updateTarget = 0;

        Frame currentFrame = getFrame(frames, Cursor.LAST);

        if (currentFrame.isSpare()) {
            Frame secondLastFrame = getFrame(frames, Cursor.SECOND_LAST);
            int lastScore = secondLastFrame.getScore();
            currentFrame.setScore(lastScore + SPARE + currentFrame.getThirdPinCount());
            updateTarget = currentFrame.getFrameNo();
        }
        return updateTarget;
    }

    /**
     * フレームを保存します
     *
     * @param frame フレーム
     */
    private void save(Frame frame) throws SQLException {

        getFrameDao().save(getFrameDao().convertToEntity(frame));

        // TODO 10フレームの3投目が保存されない
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
                throw new IllegalArgumentException("Cursor invalid");
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

}