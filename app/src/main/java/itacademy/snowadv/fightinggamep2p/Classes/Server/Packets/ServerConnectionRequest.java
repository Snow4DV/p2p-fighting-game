package itacademy.snowadv.fightinggamep2p.Classes.Server.Packets;

import itacademy.snowadv.fightinggamep2p.Classes.Server.BattlePlayer;

public class ServerConnectionRequest {
    public BattlePlayer player;


    public ServerConnectionRequest(BattlePlayer player) {
        this.player = player;
    }
    public ServerConnectionRequest() {}
}
