package itacademy.snowadv.fightinggamep2p.Classes;

import android.graphics.Point;

public class BattleUnit extends Sprite{
    private int health = 100;
    private int stamina = 100;

    private BattleUnit(SpritePainter spritePainter, Point location, SnapLocation snapLocation,
                       int width) {
        super(spritePainter, location, snapLocation);
        setScaleByWidth(width);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public static BattleUnit getAttachedToField(Field field, SpritePainter painter) {
        return new BattleUnit(painter, field.getBottomLeftPoint(), SnapLocation.BOTTOM_LEFT,
                field.getWidth());
    }
}
