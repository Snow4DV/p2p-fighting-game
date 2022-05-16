package itacademy.snowadv.fightinggamep2p.Classes.Server.Packets;

import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;

public class GameConnectionPacket {
    public String ip;
    public BattlePlayer player;

    public GameConnectionPacket(String ip, BattlePlayer player) {
        this.ip = ip;
        this.player = player;
    }

    public GameConnectionPacket() {
    }
}
