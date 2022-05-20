package itacademy.snowadv.fightinggamep2p.Classes.Drawables;

import android.content.Context;
import android.graphics.Point;

import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Server.BattlePlayer;

public abstract class DrawableBattleUnit extends Sprite {

    protected boolean isIdle = true;

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
                return "Взлом";
        }
        return null;
    }

    public static String getAbilityDescByBattlePlayerName(BattlePlayer.BattlePlayerName name) {
        switch(name) {
            case CRIMINAL:
                return "Прибавляет бойцам вашей банды 15 очков здоровья";
            case SCHOOLBOY:
                return "Накричите на противников, повысив боевой дух" +
                        " команды и повысив очки силы каждого на 10 пунктов";
            case POLICEMAN:
                return "Вызовите подкрепление, спутав силы противника и " +
                        "уменьшив их очки силы на 15";

            case CRIMINAL_BOSS:
                return "Прибавляет бойцам вашей банды 30 очков силы";
        }
        return null;
    }

    public static String getLightAttackDescByBattlePlayerName(BattlePlayer.BattlePlayerName name) {
        switch(name) {
            case CRIMINAL:
                return "Выстрелите в противника, уменьшив очки его з" +
                        "доровья на 15, потратив 10 очков силы.";
            case POLICEMAN:
                return "Выстрелите в противника, уменьшив очки его з" +
                        "доровья на 10, потратив 5 очков силы.";

            case CRIMINAL_BOSS:
                return "Выстрелите в противника, уменьшив очки его з" +
                        "доровья на 15, потратив 15 очков силы.";
            case SCHOOLBOY:
                return "Бросьте бумажку с оскорблениями и снесите противнику 3 очка здоровья," +
                        "потратив 10 очков силы.";
        }
        return null;
    }

    public static String getHardAttackDescByBattlePlayerName(BattlePlayer.BattlePlayerName name) {
        switch(name) {
            case CRIMINAL:
                return "Бросьте коктейль молотова в противника, потратив " +
                        "30 очков силы и сняв 10 очков его здоровья.";
            case POLICEMAN:
                return "Бросьте гранату в противника, уменьшив его здоровье" +
                        "на 35 очков и потратив 30 очков силы.";

            case CRIMINAL_BOSS:
                return "Выстрелите подствольной гранатой в противника, " +
                        "сняв ему 20 очков здоровья и потратив 25 очков силы.";
            case SCHOOLBOY:
                return "Взломайте коммуникации противника, уменьшив их" +
                        "очки силы на 25 (ваши очки силы уменьшатся на 35) .";
        }
        return null;
    }


    public abstract void dead(Context context);
    public abstract void idle(Context context);
    public abstract void lightKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer);
    public abstract void hardKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer);
    public abstract void ability(BattlePlayer myBattlePlayer, Context context, List<BattlePlayer> playersList);
    public abstract void updateIdleAnimation(Context context, BattlePlayer myBattlePlayer);
    public abstract String getAbilityDescription();
    public abstract String getLightKickDescription();
    public abstract String getHardKickDescription();
    public abstract String getAbilityName();
    public abstract String getLightKickName();
    public abstract String getHardKickName();

    /*public static  BattleUnit getAttachedToField(Field field, SpritePainter painter) {
        return new BattleUnit(painter, field.getBottomLeftPoint(), SnapLocation.BOTTOM_LEFT,
                field.getWidth());
    }*/
}
