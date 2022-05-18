package itacademy.snowadv.fightinggamep2p.Classes.DrawableBattleUnits;

import android.content.Context;
import android.graphics.Point;

import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Field;
import itacademy.snowadv.fightinggamep2p.Classes.SpriteAnimation;
import itacademy.snowadv.fightinggamep2p.Classes.SpritePainter;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Fragments.ServerList.Callback;

public class DrawableSchoolboy extends DrawableBattleUnit {

    private static final String ABILITY = "Накричать на противников";
    private static final String LIGHT_KICK = "Бросать бумажки с оскорблениями";
    private static final String HARD_KICK = "Взлом";

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
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.SCHOOLBOY_TALKING, context, null,
                null, 6, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        idle(context);
                    }
                }));

        for (BattlePlayer player :
                playersList) {
            if (player.getPlayer().isKind()) {
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
    public void idle(Context context) {
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.SCHOOLBOY_IDLE, context));
    }


    @Override
    public void lightKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer) {
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.SCHOOLBOY_THROWING_PAPER, context, null,
                null, 2, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        idle(context);
                    }
                }));

        myBattlePlayer.decreaseStamina(10);
        attackedPlayer.decreaseHp(3);
    }


    @Override
    public void hardKick(BattlePlayer myBattlePlayer, Context context, BattlePlayer attackedPlayer) {
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.SCHOOLBOY_HACKING, context, null,
                null, 1, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        idle(context);
                    }
                }));

        myBattlePlayer.decreaseStamina(20);
        attackedPlayer.decreaseStamina(30);
    }
}
