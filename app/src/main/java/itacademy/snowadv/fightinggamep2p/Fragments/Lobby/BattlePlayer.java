package itacademy.snowadv.fightinggamep2p.Fragments.Lobby;

import android.content.Context;

import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.BattleUnits.BattleUnit;
import itacademy.snowadv.fightinggamep2p.Classes.BattleUnits.Schoolboy;
import itacademy.snowadv.fightinggamep2p.Classes.DrawablesContainer;
import itacademy.snowadv.fightinggamep2p.Classes.Field;
import itacademy.snowadv.fightinggamep2p.Classes.SpriteAnimation;
import itacademy.snowadv.fightinggamep2p.R;

public class BattlePlayer {
    public enum BattlePlayerName {
        SCHOOLBOY(R.drawable.schoolboy_preview, true),
        CRIMINAL(R.drawable.criminal_preview, false),
        CRIMINAL_BOSS(R.drawable.criminal_boss_preview, false),
        POLICEMAN(R.drawable.policeman_preview, true);

        public final int drawableId;
        private final boolean isKind;
        private static final BattlePlayerName[] vals = values();
        public BattlePlayerName next()
        {
            return vals[(this.ordinal()+1) % vals.length];
        }

        public BattlePlayerName prev()
        {
            return vals[(this.ordinal()-1) % vals.length];
        }

        public boolean isKind() {
            return isKind;
        }
        BattlePlayerName(int drawableId, boolean isKind) {
            this.drawableId = drawableId;
            this.isKind = isKind;
        }
    }

    public enum BattlePlayerAction {
        LIGHT_KICK, HARD_KICK, ABILITY
    }



    public BattlePlayer(BattlePlayerName player, String name) {
        this.player = player;
        this.name = name;
    }

    public BattlePlayer() {
        // Empty constructor is needed for Kryo's serialization. Keep it!
    }


    private BattlePlayerName player;
    private String name;
    private String ipAddress;
    private int connectionID;
    private BattleUnit assignedBattleUnit;


    public BattlePlayerName getPlayer() {
        return player;
    }

    public void setPlayer(BattlePlayerName player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(int connectionID) {
        this.connectionID = connectionID;
    }

    public BattleUnit getAssignedBattleUnit() {
        return assignedBattleUnit;
    }



    public void assignBattleUnit(BattleUnit assignedBattleUnit) {
        this.assignedBattleUnit = assignedBattleUnit;
    }

    public static void assignAllBattleUnitsToFields(List<BattlePlayer> playersList, Context context) {
        int assignedKinds = 0;
        int assignedEvils = 0;
        for (BattlePlayer player :
                playersList) {
            switch (player.getPlayer()) {
                case SCHOOLBOY:
                    Field field;
                    if(player.player.isKind()) {
                        field = Field.getBottomField(assignedKinds++);
                    } else {
                        field = Field.getTopField(assignedEvils++);
                    }
                    player.assignBattleUnit(Schoolboy.getAttachedToField(field, context));
                    break;
            }
        }
    }

    public static void addAllBattleUnitsToDrawables(List<BattlePlayer> playersList, DrawablesContainer drawables) {
        for (BattlePlayer player :
                playersList) {
            drawables.add(player.getAssignedBattleUnit());
        }
    }


}
