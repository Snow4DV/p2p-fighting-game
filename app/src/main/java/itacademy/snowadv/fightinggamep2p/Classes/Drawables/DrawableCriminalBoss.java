package itacademy.snowadv.fightinggamep2p.Classes.Drawables;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Server.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Fragments.ServerList.Callback;

/**
 * CriminalBoss server class
 */
public class DrawableCriminalBoss extends DrawableBattleUnit {

    private static final String ABILITY = "Скоординировать бойцов";
    private static final String LIGHT_KICK = "Выстрелить";
    private static final String HARD_KICK = "Выстрелить подствольную гранату";
    private static final String ABILITY_DESC = "Прибавляет бойцам вашей банды 30 очков силы";
    private static final String LIGHT_KICK_DESC = "Выстрелите в противника, уменьшив очки его з" +
            "доровья на 15, потратив 15 очков силы.";
    private static final String HARD_KICK_DESC = "Выстрелите подствольной гранатой в противника, " +
            "сняв ему 20 очков здоровья и потратив 25 очков силы.";

    public DrawableCriminalBoss(SpritePainter spritePainter, Point location, SnapLocation snapLocation, int width) {
        super(spritePainter, location, snapLocation, width);
    }



    public static DrawableBattleUnit getAttachedToField(Field field, Context context) {
        SpritePainter painter = SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.CRIMINAL_BOSS_IDLE, context);
        return new DrawableCriminalBoss(painter, field.getBottomLeftPoint(), SnapLocation.BOTTOM_LEFT,
                field.getWidth());

    }

    @Override
    public void ability(BattlePlayer myBattlePlayer, Context context, List<BattlePlayer> playersList) {
        isIdle = false;
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.CRIMINAL_BOSS_COORDINATING, context, null,
                null, 1, object -> {
                    isIdle = true;
                    if(myBattlePlayer.isAlive()) idle(context);
                    else dead(context);
                }));
        for (BattlePlayer player :
                playersList) {
            if (!player.getPlayerName().isKind()) {
                player.increaseStamina(30);
            }
        }
    }

    @Override
    public void updateIdleAnimation(Context context, BattlePlayer myBattlePlayer) {
        if(myBattlePlayer.isAlive()) idle(context);
        else dead(context);
    }
    @Override
    public String getAbilityName() {
        return ABILITY;
    }

    @Override
    public String getLightKickName() {
        return LIGHT_KICK;
    }

    @Override
    public String getHardKickName() {
        return HARD_KICK;
    }

    @Override
    public String getAbilityDescription() {
        return ABILITY_DESC;
    }

    @Override
    public String getLightKickDescription() {
        return LIGHT_KICK_DESC;
    }

    @Override
    public String getHardKickDescription() {
        return HARD_KICK_DESC;
    }
    @Override
    public void dead(Context context) {
        Log.d(TAG, "dead: " + Arrays.toString(Thread.currentThread().getStackTrace()));
        if(!isIdle) return;
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.CRIMINAL_BOSS_DEAD, context));
    }
    @Override
    public void idle(Context context) {
        Log.d(TAG, "idle: " + Arrays.toString(Thread.currentThread().getStackTrace()));
        if(!isIdle) return;
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.CRIMINAL_BOSS_IDLE, context));
    }


    @Override
    public void lightKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer) {
        isIdle = false;
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.CRIMINAL_BOSS_SHOOTING, context, null,
                null, 1, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        isIdle = true;
                        if(myBattlePlayer.isAlive()) idle(context);
                        else dead(context);
                    }
                }));
        attackedPlayer.decreaseHp(15);
        myBattlePlayer.decreaseStamina(15);
    }


    @Override
    public void hardKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer) {
        isIdle = false;
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.CRIMINAL_BOSS_THROWING_GRENADE, context, null,
                null, 1, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        isIdle = true;
                        if(myBattlePlayer.isAlive()) idle(context);
                        else dead(context);
                    }
                }));
        attackedPlayer.decreaseHp(20);
        myBattlePlayer.decreaseStamina(25);
    }

}
