package itacademy.snowadv.fightinggamep2p.Classes.Drawables.Units;

import android.content.Context;
import android.graphics.Point;

import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Drawables.SpriteAnimation;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.SpritePainter;
import itacademy.snowadv.fightinggamep2p.Classes.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Fragments.ServerList.Callback;
import itacademy.snowadv.fightinggamep2p.Sound.SoundPlayer;

/**
 * Hacker-Schoolboy server class
 */
public class DrawableSchoolboy extends DrawableBattleUnit {

    private static final String ABILITY = "Накричать на противников";
    private static final String LIGHT_KICK = "Бросать бумажку с оскорблениями";
    private static final String HARD_KICK = "Взлом";
    private static final String ABILITY_DESC = "Накричите на противников, повысив боевой дух" +
            " команды и повысив очки силы каждого на 10 пунктов";
    private static final String LIGHT_KICK_DESC = "Бросьте бумажку с оскорблениями и снесите противнику 3 очка здоровья," +
            "потратив 10 очков силы.";
    private static final String HARD_KICK_DESC = "Взломайте коммуникации противника, уменьшив их" +
            "очки силы на 25 (ваши очки силы уменьшатся на 35) .";

    public DrawableSchoolboy(SpritePainter spritePainter, Point location, SnapLocation snapLocation, int width) {
        super(spritePainter, location, snapLocation, width);
    }



    public static DrawableBattleUnit getAttachedToField(Field field, Context context) {
        SpritePainter painter = SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.SCHOOLBOY_IDLE, context);
        return new DrawableSchoolboy(painter, field.getBottomLeftPoint(), SnapLocation.BOTTOM_LEFT,
                field.getWidth());

    }

    @Override
    public void ability(BattlePlayer myBattlePlayer, Context context, List<BattlePlayer> playersList) {
        isIdle = false;
        soundPlayer.playOnceWithDelay(SoundPlayer.SfxName.SCREAM, 100);
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.SCHOOLBOY_TALKING, context, null,
                null, 6, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        isIdle = true;
                        if(myBattlePlayer.isAlive()) idle(context);
                        else dead(context);
                    }
                }));

        for (BattlePlayer player :
                playersList) {
            if (player.getPlayerName().isKind()) {
                player.increaseStamina(10);
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
    public void updateIdleAnimation(Context context, BattlePlayer myBattlePlayer) {
        if(myBattlePlayer.isAlive()) idle(context);
        else dead(context);
    }
    @Override
    public void dead(Context context) {
        if(!isIdle) return;
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.SCHOOLBOY_DEAD, context));
    }
    @Override
    public void idle(Context context) {
        if(!isIdle) return;
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.SCHOOLBOY_IDLE, context));
    }


    @Override
    public void lightKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer) {
        isIdle = false;
        soundPlayer.playOnceWithDelay(SoundPlayer.SfxName.THROW_PAPER, 100);
        soundPlayer.playOnceWithDelay(SoundPlayer.SfxName.THROW_PAPER, 1500);
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.SCHOOLBOY_THROWING_PAPER, context, null,
                null, 2, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        isIdle = true;
                        if(myBattlePlayer.isAlive()) idle(context);
                        else dead(context);
                    }
                }));

        myBattlePlayer.decreaseStamina(10);
        attackedPlayer.decreaseHp(3);
    }


    @Override
    public void hardKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer) {
        isIdle = false;
        soundPlayer.playOnceWithDelay(SoundPlayer.SfxName.HACKING, 1900);
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.SCHOOLBOY_HACKING, context, null,
                null, 1, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        isIdle = true;
                        if(myBattlePlayer.isAlive()) idle(context);
                        else dead(context);
                    }
                }));

        myBattlePlayer.decreaseStamina(15);
        attackedPlayer.decreaseStamina(25);
    }
}
