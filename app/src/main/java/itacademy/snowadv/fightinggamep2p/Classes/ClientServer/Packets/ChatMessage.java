package itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets;

import itacademy.snowadv.fightinggamep2p.Classes.BattlePlayer;

public class ChatMessage {
    public BattlePlayer player;
    public String text;

    public ChatMessage(BattlePlayer player, String text) {
        this.player = player;
        this.text = text;
    }

    public ChatMessage() {
    }
}
