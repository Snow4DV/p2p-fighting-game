package itacademy.snowadv.fightinggamep2p.Fragments.ServerList;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.esotericsoftware.kryonet.Client;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import itacademy.snowadv.fightinggamep2p.R;
import itacademy.snowadv.fightinggamep2p.databinding.FragmentServerListBinding;

public class ServerListFragment extends Fragment {


    private FragmentServerListBinding viewBinding;
    private Client client;
    private ServerListAdapter adapter = new ServerListAdapter();
    private static final int FIXED_PORT = 54777;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentServerListBinding.inflate(inflater);
        client = new Client();
        viewBinding.serverRecyclerView.setAdapter(adapter);
        discoverPeers();
        viewBinding.refreshServerListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoverPeers();
            }
        });
        return viewBinding.getRoot();
    }



    private void discoverPeers() {
        ServerListThread.discoverPeers(client, FIXED_PORT, new Callback<List<InetAddress>>() {
            @Override
            public void evaluate(List<InetAddress> devices) {
                try {
                    devices.add(InetAddress.getByName("127.0.0.1"));
                    devices.add(InetAddress.getByName("8.8.8.8"));
                } catch(Exception ex) {
                    Log.d("debug", "evaluate: bug"); // TODO: REMOVE
                }
                adapter.setDevicesList(devices);
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
