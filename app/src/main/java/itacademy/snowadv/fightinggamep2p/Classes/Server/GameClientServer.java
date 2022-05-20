package itacademy.snowadv.fightinggamep2p.Classes.Server;

import android.graphics.Canvas;

import com.esotericsoftware.kryo.Kryo;

import java.net.Inet4Address;

import itacademy.snowadv.fightinggamep2p.Classes.Drawables.DrawableBattleUnit;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.DrawableCriminal;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.DrawableCriminalBoss;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.DrawablePoliceman;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.DrawableSchoolboy;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ChatMessage;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameActionRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameStatsPacket;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GetLobbyStatusRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.LobbyStatusUpdateResponse;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ServerConnectionRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.StartTheGameRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.Sprite;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.SpritePainter;

public interface GameClientServer {
    static void registerClasses(Kryo kryo) {
        kryo.register(GetLobbyStatusRequest.class);
        kryo.register(LobbyStatusUpdateResponse.class);
        kryo.register(ServerConnectionRequest.class);
        kryo.register(BattlePlayer.class);
        kryo.register(BattlePlayer.BattlePlayerName.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(com.esotericsoftware.kryonet.Client.class);
        kryo.register(Inet4Address.class);
        kryo.register(ChatMessage.class);
        kryo.register(itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ErrorMessagePacket.class);
        kryo.register(GameStatsPacket.class);
        kryo.register(StartTheGameRequest.class);
        kryo.register(DrawableBattleUnit.class);
        kryo.register(GameActionRequest.class);
        kryo.register(BattlePlayer.BattlePlayerAction.class);
        kryo.register(GameStatsPacket.GameSide.class);
        kryo.register(DrawableCriminal.class);
        kryo.register(DrawableCriminalBoss.class);
        kryo.register(DrawablePoliceman.class);
        kryo.register(DrawableSchoolboy.class);
        kryo.register(android.graphics.Point.class);
        kryo.register(SpritePainter.class);
        kryo.register(Canvas.class);
        kryo.register(Sprite.class);
    }
    void sendChatMessage(ChatMessage chatMessage);
}
