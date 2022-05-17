package itacademy.snowadv.fightinggamep2p.Fragments.Lobby;

import com.esotericsoftware.kryonet.Connection;

import itacademy.snowadv.fightinggamep2p.Classes.BattleUnit;
import itacademy.snowadv.fightinggamep2p.R;

public class BattlePlayer {
    public enum BattlePlayerName {
        SCHOOLBOY(R.drawable.schoolboy_preview, true);

        public final int drawableId;
        public boolean isKind = false;
        private static BattlePlayerName[] vals = values();
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
}
