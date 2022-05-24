package itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets;

public class StartTheGameRequest {
    public GameStatsPacket gameStatsPacket;

    public StartTheGameRequest(GameStatsPacket gameStatsPacket) {
        this.gameStatsPacket = gameStatsPacket;
    }

    public StartTheGameRequest() {
        // Non-parametrized constructor for Kryo
    }
}
