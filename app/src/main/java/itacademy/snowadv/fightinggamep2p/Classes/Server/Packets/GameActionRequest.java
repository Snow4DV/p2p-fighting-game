package itacademy.snowadv.fightinggamep2p.Classes.Server.Packets;

import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;

public class GameActionRequest {
    public BattlePlayer player;
    public BattlePlayer.BattlePlayerAction action;

    public GameActionRequest(BattlePlayer player, BattlePlayer.BattlePlayerAction action) {
        this.player = player;
        this.action = action;
    }

    public GameActionRequest() {
        // Empty constructor for kryo
    }
}
