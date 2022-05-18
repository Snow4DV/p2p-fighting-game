package itacademy.snowadv.fightinggamep2p.Classes.DrawableBattleUnits;

import android.content.Context;
import android.graphics.Point;

import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Sprite;
import itacademy.snowadv.fightinggamep2p.Classes.SpritePainter;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;

public abstract class DrawableBattleUnit extends Sprite {

    protected DrawableBattleUnit(SpritePainter spritePainter, Point location, SnapLocation snapLocation,
                                 int width) {
        super(spritePainter, location, snapLocation);
        setScaleByWidth(width);
    }


    public static String getAbilityNameByBattlePlayerName(BattlePlayer.BattlePlayerName name) {
        switch(name) {
            case CRIMINAL:
                return "Вылечить бойцов";
            case SCHOOLBOY:
                return "Накричать на противников";
            case POLICEMAN:
                return "Позвать подкрепление";
            case CRIMINAL_BOSS:
                return "Скоординировать бойцов";
        }
        return null;
    }

    public static String getLightAttackNameByBattlePlayerName(BattlePlayer.BattlePlayerName name) {
        switch(name) {
            case CRIMINAL:
            case POLICEMAN:
            case CRIMINAL_BOSS:
                return "Выстрелить";
            case SCHOOLBOY:
                return "Бросать бумажки с оскорблениями";
        }
        return null;
    }

    public static String getHardAttackNameByBattlePlayerName(BattlePlayer.BattlePlayerName name) {
        switch(name) {
            case CRIMINAL:
                return "Бросить Молотов";
            case POLICEMAN:
                return "Бросить гранату";
            case CRIMINAL_BOSS:
                return "Выстрелить подствольную гранату";
            case SCHOOLBOY:
                return "Бросать бумажки с оскорблениями";
        }
        return null;
    }


    public abstract void idle(Context context);
    public abstract void lightKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer);
    public abstract void hardKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer);
    public abstract void ability(BattlePlayer myBattlePlayer, Context context, List<BattlePlayer> playersList);

    public abstract String getAbilityName();
    public abstract String getLightKickName();
    public abstract String getHardKickName();

    /*public static  BattleUnit getAttachedToField(Field field, SpritePainter painter) {
        return new BattleUnit(painter, field.getBottomLeftPoint(), SnapLocation.BOTTOM_LEFT,
                field.getWidth());
    }*/
}
