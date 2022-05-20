package itacademy.snowadv.fightinggamep2p.Classes.Server.Packets;

import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Server.BattlePlayer;

public class GameActionRequest {
    public BattlePlayer player;
    public BattlePlayer.BattlePlayerAction action;
    public BattlePlayer affectedPlayer;
    public List<BattlePlayer> affectedPlayers;

    public GameActionRequest(BattlePlayer player, BattlePlayer.BattlePlayerAction action, BattlePlayer affectedPlayer) {
        this.player = player;
        this.action = action;
        this.affectedPlayer = affectedPlayer;
    }

    public GameActionRequest(BattlePlayer player, BattlePlayer.BattlePlayerAction action, List<BattlePlayer> affectedPlayers) {
        this.player = player;
        this.action = action;
        this.affectedPlayers = affectedPlayers;
    }

    public GameActionRequest() {
        // Empty constructor for kryo
    }
}
