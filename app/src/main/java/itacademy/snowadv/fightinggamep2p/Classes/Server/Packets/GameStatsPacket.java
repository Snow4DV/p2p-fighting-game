package itacademy.snowadv.fightinggamep2p.Classes.Server.Packets;

import java.util.ArrayList;
import java.util.List;

import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;

public class GameStatsPacket {

    public enum GamePhase {
        KIND_MOVE, EVIL_MOVE
    }

    private List<BattlePlayer> playersList;
    private GamePhase phase;


    public GameStatsPacket(List<BattlePlayer> playersList, GamePhase phase) {
        this.playersList = playersList;
        this.phase = phase;
    }

    public GameStatsPacket() {
        // Empty constructor for kryo
    }

    public List<BattlePlayer> getPlayersList() {
        return playersList;
    }


    public GamePhase getPhase() {
        return phase;
    }


    public int getKindPlayersAmount() {
        int count = 0;
        for (BattlePlayer player :
                playersList) {
            if (player.getPlayer().isKind()) {
                count++;
            }
        }
        return count;
    }

    public int getEvilPlayersAmount() {
        int count = 0;
        for (BattlePlayer player :
                playersList) {
            if (!player.getPlayer().isKind()) {
                count++;
            }
        }
        return count;
    }

}
