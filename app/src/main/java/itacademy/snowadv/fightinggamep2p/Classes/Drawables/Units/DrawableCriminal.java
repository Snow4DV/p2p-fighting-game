package itacademy.snowadv.fightinggamep2p.Classes.Drawables.Units;

import android.content.Context;
import android.graphics.Point;

import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Drawables.SpriteAnimation;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.SpritePainter;
import itacademy.snowadv.fightinggamep2p.Classes.Server.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Fragments.ServerList.Callback;
import itacademy.snowadv.fightinggamep2p.Sound.SoundPlayer;

/**
 * Criminal server class
 */
public class DrawableCriminal extends DrawableBattleUnit {

    private static final String ABILITY = "Вылечить бойцов";
    private static final String LIGHT_KICK = "Выстрелить";
    private static final String HARD_KICK = "Бросить Молотов";
    private static final String ABILITY_DESC = "Прибавляет бойцам вашей банды 15 очков здоровья";
    private static final String LIGHT_KICK_DESC = "Выстрелите в противника, уменьшив очки его з" +
            "доровья на 15, потратив 10 очков силы.";
    private static final String HARD_KICK_DESC = "Бросьте коктейль молотова в противника, потратив " +
            "30 очков силы и сняв 10 очков его здоровья.";


    public DrawableCriminal(SpritePainter spritePainter, Point location, SnapLocation snapLocation, int width) {
        super(spritePainter, location, snapLocation, width);
    }



    public static DrawableBattleUnit getAttachedToField(Field field, Context context) {
        SpritePainter painter = SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.CRIMINAL_IDLE, context);
        return new DrawableCriminal(painter, field.getBottomLeftPoint(), SnapLocation.BOTTOM_LEFT,
                field.getWidth());

    }

    @Override
    public void ability(BattlePlayer myBattlePlayer, Context context, List<BattlePlayer> playersList) {
        isIdle = false;
        soundPlayer.playOnceWithDelay(SoundPlayer.SfxName.HEAL, 1500);
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.CRIMINAL_HEALING, context, null,
                null, 1, object -> {
                    isIdle = true;
                    if(myBattlePlayer.isAlive()) idle(context);
                    else dead(context);
                }));

        for (BattlePlayer player :
                playersList) {
            if (!player.getPlayerName().isKind()) {
                player.increaseHp(15);
            }
        }
    }

    @Override
    public void updateIdleAnimation(Context context, BattlePlayer myBattlePlayer) {
        if(!isIdle) return;
        if(myBattlePlayer.isAlive()) idle(context);
        else dead(context);
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
    public void idle(Context context) {
        if(!isIdle) return;
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.CRIMINAL_IDLE, context));
    }

    @Override
    public void dead(Context context) {
        if(!isIdle) return;
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.CRIMINAL_DEAD, context));
    }


    @Override
    public void lightKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer) {
        isIdle = false;
        soundPlayer.playOnceWithDelay(SoundPlayer.SfxName.PISTOL, 1500);
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.CRIMINAL_SHOOTING, context, null,
                null, 1, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        isIdle = true;
                        if(myBattlePlayer.isAlive()) idle(context);
                        else dead(context);
                    }
                }));
        attackedPlayer.decreaseHp(15);
        myBattlePlayer.decreaseStamina(10);
    }


    @Override
    public void hardKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer) {
        isIdle = false;
        soundPlayer.playOnceWithDelay(SoundPlayer.SfxName.BLOWN_GRENADE, 1500 );
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.CRIMINAL_THROWING_MOLOTOV, context, null,
                null, 1, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        isIdle = true;
                        if(myBattlePlayer.isAlive()) idle(context);
                        else dead(context);
                    }
                }));
        attackedPlayer.decreaseHp(10);
        myBattlePlayer.decreaseStamina(30);
    }
}
