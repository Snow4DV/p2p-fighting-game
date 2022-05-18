package itacademy.snowadv.fightinggamep2p.Fragments.ServerList;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.esotericsoftware.kryonet.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
    private static final String IPv4_REGEX = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)" +
            "|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
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
        viewBinding.joinServerByIpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = new EditText(getActivity());
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setMessage("Введите IP-адрес сервера:")
                        .setView(editText)
                        .setPositiveButton("Войти", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String editTextInput = editText.getText().toString();
                                Log.d("onclick","Entered manual ip: "+ editTextInput);
                                if(!editTextInput.matches(IPv4_REGEX)) {
                                    Toast.makeText(getActivity(),
                                            "Неверный IP-адрес",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                connectToServer(editTextInput);
                            }
                        })
                        .setNegativeButton("Отмена", null)
                        .create();
                dialog.show();
                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                Typeface face= ResourcesCompat.getFont(getActivity(), R.font.gouranga_pixel_font);
                Button btn1 = dialog.findViewById(android.R.id.button1);
                Button btn2 = dialog.findViewById(android.R.id.button2);
                btn1.setTypeface(face);
                btn2.setTypeface(face);
                editText.setTypeface(face);
                textView.setTypeface(face);
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
