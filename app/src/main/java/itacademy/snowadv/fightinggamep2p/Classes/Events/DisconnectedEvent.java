package itacademy.snowadv.fightinggamep2p.Classes.Events;

public class DisconnectedEvent {
    public boolean showToast = true;

    public DisconnectedEvent(boolean showToast) {
        this.showToast = showToast;
    }

    public DisconnectedEvent() {
    }
}
