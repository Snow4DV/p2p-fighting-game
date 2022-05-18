package itacademy.snowadv.fightinggamep2p.Classes.BattleUnits;

import android.content.Context;
import android.graphics.Point;

import itacademy.snowadv.fightinggamep2p.Classes.Field;
import itacademy.snowadv.fightinggamep2p.Classes.SpriteAnimation;
import itacademy.snowadv.fightinggamep2p.Classes.SpritePainter;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Fragments.ServerList.Callback;

public class Schoolboy extends BattleUnit{

    private static final String ABILITY = "Накричать на противников";
    private static final String LIGHT_KICK = "Бросать бумажки с оскорблениями";
    private static final String HARD_KICK = "Взлом";

    public Schoolboy(SpritePainter spritePainter, Point location, SnapLocation snapLocation, int width) {
        super(spritePainter, location, snapLocation, width);
    }



    public static BattleUnit getAttachedToField(Field field, Context context) {
        SpritePainter painter = SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.SCHOOLBOY_IDLE, context);
        return new Schoolboy(painter, field.getBottomLeftPoint(), SnapLocation.BOTTOM_LEFT,
                field.getWidth());

    }

    @Override
    public void ability(Context context, BattlePlayer player) {
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.SCHOOLBOY_TALKING, context, null,
                null, 6, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        idle(context);
                    }
                }));
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
    public void lightKick(Context context, BattlePlayer attackedPlayer) {
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.SCHOOLBOY_THROWING_PAPER, context, null,
                null, 2, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        idle(context);
                    }
                }));
    }


    @Override
    public void hardKick(Context context, BattlePlayer attackedPlayer) {
        setSpritePainter(SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.SCHOOLBOY_HACKING, context, null,
                null, 1, new Callback<String>() {
                    @Override
                    public void evaluate(String object) {
                        idle(context);
                    }
                }));
    }
}
