package bowling.Entity;

/**
 * フレームのエンティティクラス
 */
public class Frame extends BaseEntity{

    private Integer id;
    private Integer frameNo;
    private Integer score;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(Integer frameNo) {
        this.frameNo = frameNo;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String toString() {
        return "id=" + id + " frameNo=" + frameNo + " score=" + score;
    }
}
