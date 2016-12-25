package bowling.Entity;

/**
 * ピンのエンティティクラス
 */
public class Pin extends BaseEntity {

    private Integer frameId;
    private Integer frameNo;
    private Integer throwing;
    private Integer count;

    public Integer getFrameId() {
        return frameId;
    }

    public void setFrameId(Integer frameId) {
        this.frameId = frameId;
    }

    public Integer getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(Integer frameNo) {
        this.frameNo = frameNo;
    }

    public Integer getThrowing() {
        return throwing;
    }

    public void setThrowing(Integer throwing) {
        this.throwing = throwing;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
