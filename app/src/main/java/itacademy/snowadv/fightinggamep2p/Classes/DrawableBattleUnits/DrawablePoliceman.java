package itacademy.snowadv.fightinggamep2p.Classes.DrawableBattleUnits;

import android.content.Context;
import android.graphics.Point;

import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Field;
import itacademy.snowadv.fightinggamep2p.Classes.SpriteAnimation;
import itacademy.snowadv.fightinggamep2p.Classes.SpritePainter;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Fragments.ServerList.Callback;

public class DrawablePoliceman extends DrawableBattleUnit {

    private static final String ABILITY = "Позвать подкрепление";
    private static final String LIGHT_KICK = "Выстрелить";
    private static final String HARD_KICK = "Бросить гранату";

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
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.POLICEMAN_REPORTING, context, null,
                null, 1, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        idle(context);
                    }
                }));

        for (BattlePlayer player :
                playersList) {
            if (!player.getPlayer().isKind()) {
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
    public void idle(Context context) {
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.POLICEMAN_IDLE, context));
    }


    @Override
    public void lightKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer) {
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.POLICEMAN_SHOOTING, context, null,
                null, 1, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        idle(context);
                    }
                }));
        attackedPlayer.decreaseHp(10);
        myBattlePlayer.decreaseStamina(20);
    }


    @Override
    public void hardKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer) {
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.POLICEMAN_THROWING_GRENADE, context, null,
                null, 1, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        idle(context);
                    }
                }));
        attackedPlayer.decreaseHp(30);
        myBattlePlayer.decreaseStamina(30);
    }
}
