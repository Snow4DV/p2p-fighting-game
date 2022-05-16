package itacademy.snowadv.fightinggamep2p.Fragments.Lobby;

import com.esotericsoftware.kryonet.Connection;

import itacademy.snowadv.fightinggamep2p.R;

public class BattlePlayer {
    public enum BattlePlayerName {
        SCHOOLBOY(R.drawable.schoolboy_preview);

        public final int drawableId;
        private static BattlePlayerName[] vals = values();
        public BattlePlayerName next()
        {
            return vals[(this.ordinal()+1) % vals.length];
        }

        public BattlePlayerName prev()
        {
            return vals[(this.ordinal()-1) % vals.length];
        }

        BattlePlayerName(int drawableId) {
            this.drawableId = drawableId;
        }
    }


    public BattlePlayer(BattlePlayerName player, String name) {
        this.player = player;
        this.name = name;
    }

    public BattlePlayer() {
    }

    public BattlePlayerName player;
    public String name;
    public String ipAddress;
    public int connectionID;

}
