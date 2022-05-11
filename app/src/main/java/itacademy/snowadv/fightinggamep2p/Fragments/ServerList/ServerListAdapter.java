package itacademy.snowadv.fightinggamep2p.Fragments.ServerList;

import android.net.InetAddresses;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import itacademy.snowadv.fightinggamep2p.R;
import itacademy.snowadv.fightinggamep2p.UI.FlatButton;

public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ServerViewHolder> {
    private List<InetAddress> serverItemList = new ArrayList<>();

    public void setDevicesList(List<InetAddress> devices) {
        serverItemList = devices;
    }

    @NonNull
    @Override
    public ServerListAdapter.ServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_server, parent, false);
        return new ServerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServerListAdapter.ServerViewHolder holder, int position) {
        holder.bind(serverItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return serverItemList.size();
    }

    class ServerViewHolder extends RecyclerView.ViewHolder {

        private TextView serverIP;
        private FlatButton joinButton;
        public ServerViewHolder(@NonNull View itemView) {
            super(itemView);
            serverIP = itemView.findViewById(R.id.server_ip_address);
            joinButton = itemView.findViewById(R.id.join_button);
        }

        public void bind(InetAddress device) {
            serverIP.setText(device.getHostAddress());
        }
    }
}
