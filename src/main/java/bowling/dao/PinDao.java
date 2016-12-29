package bowling.dao;

import bowling.Entity.Pin;
import bowling.model.Frame;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * ピンテーブルに対するデータベース操作を行います
 */
public class PinDao extends BaseDao {

    private PreparedStatement insert;

    @Override
    public void init() throws SQLException {
        this.insert = getCon().prepareStatement("insert into pin (frame_id, frame_no, throwing, count, entry_date, upd_date, version) values (?,?,?,?,?,?,?)");
    }

    public PinDao(Connection con) throws SQLException {
        super(con);
        init();
    }

    /**
     * ピンを保存します
     *
     * @param pin エンティティ
     * @throws SQLException SQL例外
     */
    public void save(Pin pin) throws SQLException {
        setSaveParams(pin);

        PreparedStatement ps = getInsert();
        ps.setInt(1, pin.getFrameId());
        ps.setInt(2, pin.getFrameNo());
        ps.setInt(3, pin.getThrowing());
        ps.setInt(4, pin.getCount());
        ps.setTimestamp(5, pin.getEntryDate());
        ps.setTimestamp(6, pin.getUpdDate());
        ps.setInt(7, pin.getVersion());
        executeUpdate(ps);
    }

    /**
     * モデルからエンティティへ詰め替えを行います
     * TODO ユーティリティに切り出してもいいかも。
     *
     * @param frame フレーム
     * @param throwing 投数
     * @return エンティティ
     */
    public Pin convertToEntity(Frame frame, int throwing) {
        Pin entity = new Pin();
        entity.setFrameId(frame.getId());
        entity.setFrameNo(frame.getFrameNo());
        entity.setThrowing(throwing);

        bowling.model.Pin pin = frame.getPins().get(throwing - 1);
        entity.setCount(pin.getCount());
        return entity;
    }

    private PreparedStatement getInsert() {
        return insert;
    }
}