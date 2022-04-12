package itacademy.snowadv.fightinggamep2p.Fragments.BattleField;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import itacademy.snowadv.fightinggamep2p.R;

public class ServerBattleFragment extends Fragment {
    private BattleFieldSurfaceView battleFieldSurfaceView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        battleFieldSurfaceView = new BattleFieldSurfaceView(getContext());
        return battleFieldSurfaceView;
    }

    public class ServerEntityAdapter extends RecyclerView.Adapter<ServerEntityAdapter.ServerEntityAdapterViewHolder> {
        private final LayoutInflater inflater;
        private final List<ServerEntity> entities;

        public ServerEntityAdapter(Context context, List<ServerEntity> entities) {
            this.inflater = LayoutInflater.from(context);
            this.entities = entities;
        }

        @NonNull
        @Override
        public ServerEntityAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.fragment_server_list, parent, false);
            return new ServerEntityAdapterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ServerEntityAdapterViewHolder holder, int position) {
            ServerEntity server = entities.get(position);
            holder.ipAddress.setText(server.getIp());
        }

        @Override
        public int getItemCount() {
            return entities.size();
        }

        private class ServerEntityAdapterViewHolder extends RecyclerView.ViewHolder {
            final TextView ipAddress;

            ServerEntityAdapterViewHolder(View view) {
                super(view);
                ipAddress = view.findViewById(R.id.serverIpAddress);
            }
        }
    }

    private class ServerEntity {
        private String ip;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }

}
