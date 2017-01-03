package bowling.util;

import bowling.Entity.Pin;
import bowling.model.Frame;

/**
 * モデルとエンティティの詰め替えを行います
 */
public class ModelAndEntityConverter {

    /**
     * モデルからエンティティへ詰め替えを行います
     *
     * @param frame フレーム
     * @param throwing 投数
     * @return エンティティ
     */
    public static Pin convertToEntity(Frame frame, int throwing) {
        Pin entity = new Pin();
        entity.setFrameId(frame.getId());
        entity.setFrameNo(frame.getFrameNo());
        entity.setThrowing(throwing);

        bowling.model.Pin pin = frame.getPins().get(throwing - 1);
        entity.setCount(pin.getCount());
        entity.setFailCode(pin.getFailCode());
        return entity;
    }

    /**
     * エンティティからモデルへの詰め替えを行います
     *
     * @param pin エンティティ
     * @return モデル
     */
    public static bowling.model.Pin convertToModel(Pin pin) {
        bowling.model.Pin model = new bowling.model.Pin();
        model.setCount(pin.getCount());
        model.setFailCode(pin.getFailCode());
        return model;
    }

    /**
     * モデルからエンティティへ詰め替えを行います
     *
     * @param model モデル
     * @return エンティティ
     */
    public static bowling.Entity.Frame convertToEntity(bowling.model.Frame model) {
        bowling.Entity.Frame entity = new bowling.Entity.Frame();
        entity.setId(model.getId());
        entity.setFrameNo(model.getFrameNo());
        entity.setScore(model.getScore());
        return entity;
    }

    /**
     * エンティティからモデルへ詰め替えを行います
     *
     * @param entity エンティティ
     * @return モデル
     */
    public static bowling.model.Frame convertToModel(bowling.Entity.Frame entity) {
        bowling.model.Frame model = new bowling.model.Frame();
        model.setId(entity.getId());
        model.setFrameNo(entity.getFrameNo());
        model.setScore(entity.getScore());
        return model;
    }

}
