package itacademy.snowadv.fightinggamep2p.Classes.Drawables;

import android.content.Context;
import android.graphics.Point;

import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Server.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Fragments.ServerList.Callback;
import itacademy.snowadv.fightinggamep2p.Sound.SoundPlayer;

/**
 * Policeman server class
 */
public class DrawablePoliceman extends DrawableBattleUnit {

    private static final String ABILITY = "Позвать подкрепление";
    private static final String LIGHT_KICK = "Выстрелить";
    private static final String HARD_KICK = "Бросить гранату";
    private static final String ABILITY_DESC = "Вызовите подкрепление, спутав силы противника и " +
            "уменьшив их очки силы на 15";
    private static final String LIGHT_KICK_DESC = "Выстрелите в противника, уменьшив очки его з" +
            "доровья на 10, потратив 5 очков силы.";
    private static final String HARD_KICK_DESC = "Бросьте гранату в противника, уменьшив его здоровье" +
            "на 35 очков и потратив 30 очков силы.";

    public DrawablePoliceman(SpritePainter spritePainter, Point location, SnapLocation snapLocation, int width) {
        super(spritePainter, location, snapLocation, width);
    }



    public static DrawableBattleUnit getAttachedToField(Field field, Context context) {
        SpritePainter painter = SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.POLICEMAN_IDLE, context);
        return new DrawablePoliceman(painter, field.getBottomLeftPoint(), SnapLocation.BOTTOM_LEFT,
                field.getWidth());

    }

    @Override
    public void ability(BattlePlayer myBattlePlayer, Context context, List<BattlePlayer> playersList) {
        isIdle = false;
        soundPlayer.playOnceWithDelay(SoundPlayer.SfxName.REPORTING, 1500);
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.POLICEMAN_REPORTING, context, null,
                null, 1, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        isIdle = true;
                        if(myBattlePlayer.isAlive()) idle(context);
                        else dead(context);
                    }
                }));

        for (BattlePlayer player :
                playersList) {
            if (!player.getPlayerName().isKind()) {
                player.decreaseStamina(15);
            }
        }
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
        if(!isIdle) return;
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.POLICEMAN_DEAD, context));
    }
    @Override
    public void idle(Context context) {
        if(!isIdle) return;
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.POLICEMAN_IDLE, context));
    }


    @Override
    public void lightKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer) {
        isIdle = false;
        soundPlayer.playOnceWithDelay(SoundPlayer.SfxName.PISTOL, 1500);
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.POLICEMAN_SHOOTING, context, null,
                null, 1, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        isIdle = true;
                        if(myBattlePlayer.isAlive()) idle(context);
                        else dead(context);
                    }
                }));
        attackedPlayer.decreaseHp(10);
        myBattlePlayer.decreaseStamina(5);
    }


    @Override
    public void updateIdleAnimation(Context context, BattlePlayer myBattlePlayer) {
        if(myBattlePlayer.isAlive()) idle(context);
        else dead(context);
    }
    @Override
    public void hardKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer) {
        isIdle = false;
        soundPlayer.playOnceWithDelay(SoundPlayer.SfxName.BLOWN_GRENADE, 1500);
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.POLICEMAN_THROWING_GRENADE, context, null,
                null, 1, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        isIdle = true;
                        if(myBattlePlayer.isAlive()) idle(context);
                        else dead(context);
                    }
                }));
        attackedPlayer.decreaseHp(30);
        myBattlePlayer.decreaseStamina(30);
    }
}
