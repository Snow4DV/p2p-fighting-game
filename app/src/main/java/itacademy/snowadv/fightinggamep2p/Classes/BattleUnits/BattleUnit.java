package itacademy.snowadv.fightinggamep2p.Classes.BattleUnits;

import android.content.Context;
import android.graphics.Point;

import itacademy.snowadv.fightinggamep2p.Classes.Field;
import itacademy.snowadv.fightinggamep2p.Classes.Sprite;
import itacademy.snowadv.fightinggamep2p.Classes.SpritePainter;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;

public abstract class BattleUnit extends Sprite {
    private int health = 100;
    private int stamina = 100;

    protected BattleUnit(SpritePainter spritePainter, Point location, SnapLocation snapLocation,
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

    public abstract void idle(Context context);
    public abstract void lightKick(Context context, BattlePlayer attackedPlayer);
    public abstract void hardKick(Context context, BattlePlayer attackedPlayer);
    public abstract void ability(Context context, BattlePlayer player);

    public abstract String getAbilityName();
    public abstract String getLightKickName();
    public abstract String getHardKickName();

    /*public static  BattleUnit getAttachedToField(Field field, SpritePainter painter) {
        return new BattleUnit(painter, field.getBottomLeftPoint(), SnapLocation.BOTTOM_LEFT,
                field.getWidth());
    }*/
}
