package bowling.service;

import bowling.dao.BaseDao;
import bowling.dao.FrameDao;
import bowling.dao.PinDao;
import bowling.model.Frame;
import bowling.model.Pin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * スコア計算のクラス
 */
public class CalculationService {

    private static final int STRIKE = 10;
    private static final int SPARE = 10;
    private static final int FIRST_THROW = 1;
    private static final int SECOND_THROW = 2;
    private static final int THIRD_THROW = 3;

    private FrameDao frameDao;
    private PinDao pinDao;

    private int frameId = 0;

    public CalculationService() {

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
     * @param frames       前フレームまでのフレームリスト
     * @param currentFrame 現在投球中のフレーム
     */
    public void calculateScore(List<Frame> frames, Frame currentFrame) throws SQLException {

        // フレームID採番
        if (frameId == 0) {
            frameId = getFrameDao().getNewId();
        }
        currentFrame.setId(frameId);

        // 前フレームのindex取得
        int lastFrameIndex = frames.size() - 1;

        // ストライクの計算
        calculateStrike(frames, currentFrame);

        // スペアの計算
        calculateSpare(frames, currentFrame);

        // 2投目を入力してない場合はスキップする
        if (currentFrame.getPins().size() != 2) {
            return;
        }

        // ストライクではないかつスペアではない場合
        if (!currentFrame.isStrike() && !currentFrame.isSpare()) {

            int score = sumPinCount(currentFrame.getPins());

            // 1つ前のフレームが無い場合最新スコアは0とする
            int lastScore = 0;
            if (0 <= lastFrameIndex) {
                Frame lastFrame = frames.get(lastFrameIndex);
                lastScore = lastFrame.getScore();
            }

            // 現在のフレームのスコア
            currentFrame.setScore(lastScore + score);
        }

        save(currentFrame);
    }

    private Frame getUpdateTargetFrame(final int target, List<Frame> frames) {
        return frames.stream().filter(f -> f.getFrameNo() == target).findFirst().orElse(null);
    }

    /**
     * ストライクの計算を行う
     *
     * <p>
     * 入力中のフレームを基準に過去フレームのストライクの計算を行う
     * </p>
     *
     * @param frames
     * @param currentFrame
     * @return 更新対象のフレームNo
     */
    private void calculateStrike(List<Frame> frames, Frame currentFrame) throws SQLException {

        int updatedFrameNo = 0;

        // 前フレームのindex取得
        int lastFrameIndex = frames.size() - 1;

        // 2つ前までのフレームが無い場合スキップ
        if (lastFrameIndex - 1 < 0 || lastFrameIndex < 0) {
            return;
        }

        // ストライクの計算
        switch (currentFrame.getPins().size()) {
            case FIRST_THROW:
                updatedFrameNo = calcStrikeFirstThrow(frames, currentFrame);
                break;
            case SECOND_THROW:
                updatedFrameNo = calcStrikeSecondThrow(frames, currentFrame);
                break;
            case THIRD_THROW:
                updatedFrameNo = calcStrikeThirdThrow(frames, currentFrame);
                break;
        }

        // ストライクフレームの更新
        // TODO 10フレーム目はここで10フレームが取得できない。framesには9フレームまでしか入ってないから。
        Frame strikeFrame = getUpdateTargetFrame(updatedFrameNo, frames);
        update(strikeFrame);
    }

    /**
     * 現在フレームの1投目を基準にストライクの計算を行います
     *
     * @param frames
     * @param currentFrame
     * @return 更新対象のフレームNo
     */
    private int calcStrikeFirstThrow(List<Frame> frames, Frame currentFrame) throws SQLException {

        int updateTarget = 0;
        int lastFrameIndex = frames.size() - 1;

        Frame lastFrame = frames.get(lastFrameIndex);
        Frame secondLastFrame = frames.get(lastFrameIndex - 1);

        if (lastFrame.isStrike() &&
                secondLastFrame.isStrike() &&
                secondLastFrame.getScore() == 0) {

            // 3つ前のフレームが無い場合最新スコアは0とする
            int lastScore = 0;
            if (lastFrameIndex - 2 >= 0) {
                lastScore = frames.get(lastFrameIndex - 2).getScore();
            }
            secondLastFrame.setScore(lastScore + STRIKE + STRIKE + currentFrame.getFirstPinCount());
            updateTarget = secondLastFrame.getFrameNo();
        }
        return updateTarget;
    }

    /**
     * 現在フレームの２投目を基準にストライクの計算を行います
     *
     * @param frames
     * @param currentFrame
     */
    private int calcStrikeSecondThrow(List<Frame> frames, Frame currentFrame) throws SQLException {

        int updateTarget = 0;
        int lastFrameIndex = frames.size() - 1;

        // 9フレームまでで、1投目がストライクの時はスキップ
        if (currentFrame.getFrameNo() != 10 && currentFrame.isStrike()) {
            return updateTarget;
        }

        Frame lastFrame = frames.get(lastFrameIndex);
        if (lastFrame.isStrike() &&
                lastFrame.getScore() == 0) {

            // 2つ前のフレームが無い場合最新スコアは0とする
            int lastScore = 0;
            if (lastFrameIndex - 1 >= 0) {
                lastScore = frames.get(lastFrameIndex - 1).getScore();
            }
            lastFrame.setScore(lastScore + STRIKE + currentFrame.getFirstPinCount() + currentFrame.getSecondPinCount());
            updateTarget = lastFrame.getFrameNo();
        }
        return updateTarget;
    }

    /**
     * 現在フレームの3投目を基準にストライクの計算を行います
     * このメソッドは10フレーム目の場合に使用される想定です
     *
     * @param frames
     * @param currentFrame
     */
    private int calcStrikeThirdThrow(List<Frame> frames, Frame currentFrame) throws SQLException {

        int updateTarget = 0;
        int lastFrameIndex = frames.size() - 1;

        if (currentFrame.isStrike()) {
            int lastScore = frames.get(lastFrameIndex).getScore();

            currentFrame.setScore(lastScore + STRIKE + currentFrame.getSecondPinCount() + currentFrame.getThirdPinCount());
            updateTarget = currentFrame.getFrameNo();
        }
        return updateTarget;
    }

    /**
     * 2投を合計します
     *
     * @param pins
     * @return
     */
    private int sumPinCount(List<Pin> pins) {
        int sum = 0;
        sum += pins.get(0).getCount();
        sum += pins.get(1).getCount();
        return sum;
    }

    /**
     * スペアの計算を行います
     *
     * @param frames
     * @param currentFrame
     */
    private void calculateSpare(List<Frame> frames, Frame currentFrame) throws SQLException {

        int updatedFrameNo = 0;
        int lastFrameIndex = frames.size() - 1;

        // 10フレームの3投目の場合
        if (currentFrame.getFrameNo() == 10 && currentFrame.getPins().size() == 3) {
            updatedFrameNo = calculateSpare10Frame(frames, currentFrame);

            // TODO 10フレーム目はここで10フレームが取得できない。framesには9フレームまでしか入ってないから。
            Frame spareFrame = getUpdateTargetFrame(updatedFrameNo, frames);
            update(spareFrame);
            return;
        }

        // 現在フレームの2投目以降の場合はスキップ
        if (currentFrame.getPins().size() > 1) {
            return;
        }

        // 1つ前のフレームが無い場合スキップ
        if (lastFrameIndex < 0) {
            return;
        }

        Frame lastFrame = frames.get(lastFrameIndex);
        if (lastFrame.isSpare()) {

            int lastScore = 0;
            if (lastFrameIndex - 1 >= 0) {
                lastScore = frames.get(lastFrameIndex - 1).getScore();
            }
            lastFrame.setScore(lastScore + SPARE + currentFrame.getFirstPinCount());
            updatedFrameNo = lastFrame.getFrameNo();
        }

        // スペアフレームの更新
        Frame spareFrame = getUpdateTargetFrame(updatedFrameNo, frames);
        update(spareFrame);
    }

    /**
     * 10フレーム目のスペアを計算します
     *
     * @param frames
     * @param currentFrame
     */
    private int calculateSpare10Frame(List<Frame> frames, Frame currentFrame) throws SQLException {

        int updateTarget = 0;
        int lastFrameIndex = frames.size() - 1;

        if (currentFrame.isSpare()) {
            Frame lastFrame = frames.get(lastFrameIndex);
            int lastScore = lastFrame.getScore();
            currentFrame.setScore(lastScore + SPARE + currentFrame.getThirdPinCount());
            updateTarget = currentFrame.getFrameNo();
        }
        return updateTarget;
    }

    /**
     * フレームを保存します
     *
     * @param frame
     */
    private void save(Frame frame) throws SQLException {

        bowling.Entity.Frame frameEntity = getFrameDao().convertToEntity(frame);
        System.out.println(frameEntity);
        getFrameDao().save(frameEntity);

        // TODO 1投目しか保存されない
        for (int i = 1; i <= frame.getPins().size(); i++) {
            bowling.Entity.Pin pinEntity = getPinDao().convertToEntity(frame, i);
            getPinDao().save(pinEntity);
        }
    }

    /**
     * フレームを更新します
     *
     * @param frame
     */
    private void update(Frame frame) throws SQLException {

        Optional<Frame> nullableFrame = Optional.ofNullable(frame);
        if (nullableFrame.isPresent()) {
            bowling.Entity.Frame entity = getFrameDao().convertToEntity(nullableFrame.get());
            getFrameDao().update(entity);
        }
    }

    private FrameDao getFrameDao() {
        return frameDao;
    }

    private PinDao getPinDao() {
        return pinDao;
    }
}