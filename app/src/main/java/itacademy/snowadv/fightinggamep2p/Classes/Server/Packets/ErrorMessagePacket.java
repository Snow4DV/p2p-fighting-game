package itacademy.snowadv.fightinggamep2p.Classes.Server.Packets;

public class ErrorMessagePacket {
    public String errorText;
    public boolean disconnected;

    public ErrorMessagePacket(String errorText, boolean disconnected) {
        this.errorText = errorText;
        this.disconnected = disconnected;
    }

    public ErrorMessagePacket() {
    }
}
