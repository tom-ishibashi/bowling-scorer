package bowling.dao;

import bowling.Entity.BaseEntity;

import java.sql.*;
import java.util.Calendar;

/**
 * データベースアクセスのスーパークラス
 */
abstract public class BaseDao {

    private static final String USER = "study1";
    private static final String PASSWORD = "devStudy1!";
    private static final String URL = "jdbc:mysql://localhost:3306/javaedu";

    protected Connection con;

    public BaseDao(Connection con) throws SQLException {
        this.con = con;
    }

    abstract public void init() throws SQLException;
//    abstract public void closeConnection() throws SQLException; // todo いらんかも？

    /**
     * データベースのコネクションを取得する
     *
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("データベースに接続できませんでした");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 保存時のパラメータをセットします
     * @param entity
     */
    protected void setSaveParams(BaseEntity entity) {
        Calendar now = Calendar.getInstance();

        entity.setEntryDate(new Timestamp(now.getTimeInMillis()));
        entity.setUpdDate(new Timestamp(now.getTimeInMillis()));
        entity.setVersion(1);
    }

    /**
     * 更新自のパラメータをセットします
     * @param entity
     */
    protected void setUpdateParams(BaseEntity entity) {

        entity.setUpdDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    }


    /**
     * selectを実行します
     */
    protected ResultSet executeQuery(String sql) throws SQLException {
        ResultSet rs;
        try {
            Statement statement = getCon().createStatement();
            rs = statement.executeQuery(sql);

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return rs;
    }

    /**
     * insert, update, deleteを実行します
     * @param ps
     */
    protected void executeUpdate(PreparedStatement ps) throws SQLException {

        try {
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    protected Connection getCon() {
        return this.con;
    }
}
