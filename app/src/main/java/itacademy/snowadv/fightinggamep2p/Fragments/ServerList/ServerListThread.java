package itacademy.snowadv.fightinggamep2p.Fragments.ServerList;

import android.os.Handler;

import com.esotericsoftware.kryonet.Client;

import java.net.InetAddress;
import java.util.List;

class ServerListThread {
    public static void discoverPeers(Client client, int udpPort, Callback<List<InetAddress>> callback){
        Runnable discovery = new Runnable() {
            @Override
            public void run() {
                callback.evaluate(client.discoverHosts(udpPort, 1000));
            }
        };
        new Thread(discovery).start();
    }


}