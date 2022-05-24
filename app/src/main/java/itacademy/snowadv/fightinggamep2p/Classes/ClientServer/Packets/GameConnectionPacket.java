package itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets;

import itacademy.snowadv.fightinggamep2p.Classes.BattlePlayer;

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
