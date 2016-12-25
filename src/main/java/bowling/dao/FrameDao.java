package bowling.dao;

import bowling.Entity.Frame;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * フレームテーブルに対するデータベース操作を行います
 */
public class FrameDao extends BaseDao {

    private PreparedStatement insert;
    private PreparedStatement update;

    public FrameDao(Connection con) throws SQLException {
        super(con);
        init();
    }

    @Override
    public void init() throws SQLException {
        this.insert = getCon().prepareStatement("insert into frame (id, frame_no, score, entry_date, upd_date, version) values (?,?,?,?,?,?)");
        this.update = getCon().prepareStatement("update frame set score = ?, upd_date = ?, version = version + 1 where id = ? and frame_no = ?");
    }


    /**
     * IDを採番します
     *
     * @return
     */
    public int getNewId() throws SQLException {

        String sql = "select (case when max(id) is NULL then 1 else max(id) + 1 end) as id from frame";
        ResultSet rs = executeQuery(sql);
        int result = 0;
        while (rs.next()) {
            result = rs.getInt(1);
        }
        return result;
    }

    /**
     * フレームを保存します
     *
     * @param frame
     * @throws SQLException
     */
    public void save(Frame frame) throws SQLException {
        setSaveParams(frame);

        PreparedStatement ps = getInsert();
        ps.setInt(1, frame.getId());
        ps.setInt(2, frame.getFrameNo());
        ps.setInt(3, frame.getScore());
        ps.setTimestamp(4, frame.getEntryDate());
        ps.setTimestamp(5, frame.getUpdDate());
        ps.setInt(6, frame.getVersion());
        executeUpdate(ps);
    }

    /**
     * フレームを更新します
     *
     * @param frame
     * @throws SQLException
     */
    public void update(Frame frame) throws SQLException {
        setUpdateParams(frame);

        PreparedStatement ps = getUpdate();
        ps.setInt(1, frame.getScore());
        ps.setTimestamp(2, frame.getUpdDate());
        ps.setInt(3, frame.getId());
        ps.setInt(4, frame.getFrameNo());
        executeUpdate(ps);
    }

    /**
     * モデルからエンティティへ詰め替えを行います
     *
     * @param model
     * @return
     */
    public Frame convertToEntity(bowling.model.Frame model) {
        Frame entity = new Frame();
        entity.setId(model.getId());
        entity.setFrameNo(model.getFrameNo());
        entity.setScore(model.getScore());
        return entity;
    }


    /**
     * エンティティからモデルへ詰め替えを行います
     *
     * @param entity
     * @return
     */
    public bowling.model.Frame convertToModel(Frame entity) {
        bowling.model.Frame model = new bowling.model.Frame();
        model.setFrameNo(entity.getFrameNo());
        model.setScore(entity.getScore());
        return model;
    }

    private PreparedStatement getInsert() {
        return insert;
    }

    private PreparedStatement getUpdate() {
        return update;
    }
}
