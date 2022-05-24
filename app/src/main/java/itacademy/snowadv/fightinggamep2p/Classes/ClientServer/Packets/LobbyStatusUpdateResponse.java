package itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets;

import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.BattlePlayer;

public class LobbyStatusUpdateResponse {
    public List<BattlePlayer> players;
    private String hostIP;

    public LobbyStatusUpdateResponse(List<BattlePlayer> players, String hostIP) {
        this.hostIP = hostIP;
        this.players = players;
    }

    public List<BattlePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<BattlePlayer> players) {
        this.players = players;
    }

    public String getHostIP() {
        return hostIP;
    }

    public void setHostIP(String hostIP) {
        this.hostIP = hostIP;
    }

    public LobbyStatusUpdateResponse() {
    }
}
