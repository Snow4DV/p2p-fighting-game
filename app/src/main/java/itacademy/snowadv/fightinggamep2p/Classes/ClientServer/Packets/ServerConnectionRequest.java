package itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets;

import itacademy.snowadv.fightinggamep2p.Classes.BattlePlayer;

public class ServerConnectionRequest {
    public BattlePlayer player;


    public ServerConnectionRequest(BattlePlayer player) {
        this.player = player;
    }
    public ServerConnectionRequest() {}
}
