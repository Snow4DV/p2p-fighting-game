package itacademy.snowadv.fightinggamep2p.Fragments.ServerList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.esotericsoftware.kryonet.Client;

import java.net.InetAddress;
import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.NotifiableActivity;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameConnectionPacket;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.R;
import itacademy.snowadv.fightinggamep2p.databinding.FragmentServerListBinding;

public class ServerListFragment extends Fragment {


    private FragmentServerListBinding viewBinding;
    private Client client;
    private ServerListAdapter adapter;
    private static final int FIXED_PORT = 54777;
    private BattlePlayer player;

    public ServerListFragment(BattlePlayer player) {
        this.player = player;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentServerListBinding.inflate(inflater);
        client = new Client();
        adapter = new ServerListAdapter(this::connectToServer);
        viewBinding.serverRecyclerView.setAdapter(adapter);
        discoverPeers();
        viewBinding.refreshServerListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewBinding.serverListPlaceholderText.setText(R.string.discovering_servers);
                discoverPeers();
            }
        });
        return viewBinding.getRoot();
    }

    private void connectToServer(String ip) {
        Toast.makeText(getActivity(), "Подключение к " + ip, Toast.LENGTH_SHORT).show();
        if(getActivity() instanceof NotifiableActivity) {
            ((NotifiableActivity)getActivity()).notifyWithObject(
                    new GameConnectionPacket((String) ip, player));
        }
    }

    private void discoverPeers() {
        adapter.clearDeviceList();
        ServerListThread.discoverPeers(client, FIXED_PORT, new Callback<List<InetAddress>>() {
            @Override
            public void evaluate(List<InetAddress> devices) {
                try {
                    devices.add(InetAddress.getByName("127.0.0.1"));
                    devices.add(InetAddress.getByName("192.168.0.150"));
                    devices.add(InetAddress.getByName("8.8.8.8"));
                } catch(Exception ex) {
                    Log.d("debug", "evaluate: bug"); // TODO: REMOVE
                }
                adapter.setDevicesList(devices);
                if(devices.size() > 0) {
                    viewBinding.serverListPlaceholderText.setText("");
                } else {
                    viewBinding.serverListPlaceholderText.setText(R.string.no_servers);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();

                    }
                });
            }
        });
    }




}
