package bowling.Entity;

import java.sql.Timestamp;

/**
 * エンティティの基底クラス
 */
public class BaseEntity {

    private Timestamp entryDate;
    private Timestamp updDate;
    private Integer version;

    public Timestamp getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Timestamp entryDate) {
        this.entryDate = entryDate;
    }

    public Timestamp getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Timestamp updDate) {
        this.updDate = updDate;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
