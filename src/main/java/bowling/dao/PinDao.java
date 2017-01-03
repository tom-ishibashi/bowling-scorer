package bowling.dao;

import bowling.Entity.Pin;
import bowling.model.Frame;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ピンテーブルに対するデータベース操作を行います
 */
public class PinDao extends BaseDao {

    private PreparedStatement insert;
    private PreparedStatement selectById;

    @Override
    public void init() throws SQLException {
        this.insert = getCon().prepareStatement("insert into pin (frame_id, frame_no, throwing, count, entry_date, upd_date, version, fail_code) values (?,?,?,?,?,?,?,?)");
        this.selectById = getCon().prepareStatement("select frame_id, frame_no, throwing, count, fail_code from pin where frame_id = ? order by frame_no, throwing");
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
        ps.setInt(8, pin.getFailCode());
        executeUpdate(ps);
    }

    /**
     * idをもとにピンを検索します。
     * 検索結果が無い場合は空のリストを返します
     *
     * @param id id
     * @return エンティティのリスト
     * @throws SQLException SQL例外
     */
    public List<Pin> selectById(int id) throws SQLException {
        PreparedStatement ps = getSelectById();
        ps.setInt(1, id);
        ResultSet result = executeQuery(ps);

        if (result == null) {
            return new ArrayList<>();
        }

        List<Pin> entities = new ArrayList<>();
        while (result.next()) {
            Pin entity = new Pin();
            entity.setFrameId(result.getInt("frame_id"));
            entity.setFrameNo(result.getInt("frame_no"));
            entity.setThrowing(result.getInt("throwing"));
            entity.setCount(result.getInt("count"));
            entity.setFailCode(result.getInt("fail_code"));
            entities.add(entity);
        }

        return entities;
    }

    private PreparedStatement getInsert() {
        return insert;
    }

    private PreparedStatement getSelectById() {
        return selectById;
    }
}