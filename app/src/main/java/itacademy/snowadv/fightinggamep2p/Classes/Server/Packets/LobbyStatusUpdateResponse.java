package itacademy.snowadv.fightinggamep2p.Classes.Server.Packets;

import java.util.List;

import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;

public class LobbyStatusUpdateResponse {
    public List<BattlePlayer> players;
    public BattlePlayer host;

    public LobbyStatusUpdateResponse(List<BattlePlayer> players, BattlePlayer host) {
        this.players = players;
        this.host = host;
    }

    public LobbyStatusUpdateResponse() {
    }
}
