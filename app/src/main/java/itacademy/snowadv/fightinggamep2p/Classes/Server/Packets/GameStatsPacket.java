package itacademy.snowadv.fightinggamep2p.Classes.Server.Packets;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Server.BattlePlayer;

public class GameStatsPacket {
    private static final String TAG = "GameStatsPacket";

    public enum GameSide {
        KIND, EVIL
    }

    private List<BattlePlayer> playersList;
    private GameSide phase;
    private GameSide wonSide = null;

    public GameSide getWonSide() {
        return wonSide;
    }

    public void setWonSide(GameSide wonSide) {
        this.wonSide = wonSide;
    }

    private int phaseNo = 0;

    public GameStatsPacket(List<BattlePlayer> playersList, GameSide phase) {
        this.playersList = playersList;
        this.phase = phase;
    }

    public GameStatsPacket() {
        // Empty constructor for kryo
    }

    public List<BattlePlayer> getPlayersList() {
        return playersList;
    }


    public GameSide getPhase() {
        return phase;
    }


    public int getKindPlayersAmount() {
        int count = 0;
        for (BattlePlayer player :
                playersList) {
            if (player.getPlayerName().isKind()) {
                count++;
            }
        }
        return count;
    }

    public int getEvilPlayersAmount() {
        int count = 0;
        for (BattlePlayer player :
                playersList) {
            if (!player.getPlayerName().isKind()) {
                count++;
            }
        }
        return count;
    }


    public int getKindPlayersHP() {
        int hp = 0;
        for (BattlePlayer player :
                playersList) {
            if (player.getPlayerName().isKind()) {
                hp+=player.getHealth();
            }
        }
        return hp;
    }

    public int getEvilPlayersHP() {
        int hp = 0;
        for (BattlePlayer player :
                playersList) {
            if (!player.getPlayerName().isKind()) {
                hp+=player.getHealth();
            }
        }
        return hp;
    }
    public void setPlayersList(List<BattlePlayer> playersList) {
        this.playersList = playersList;
    }

    public BattlePlayer getBattlePlayerByConnectionID(int connectionID) {
        for (BattlePlayer player :
                playersList) {
            if (player.getConnectionID() == connectionID) {
                return player;
            }
            }
        return null;
    }

    public void setPhase(GameSide phase) {
        this.phase = phase;
        // Give all players ability to step according to the phase
        boolean kindStep = (phase == GameSide.KIND);
        for (BattlePlayer player :
                playersList) {
            player.setAbilityToStep(player.getPlayerName().isKind() == kindStep && player.isAlive());
        }
        phaseNo++;
        // Increase stamina on phase change
        for (BattlePlayer player :
                playersList) {
            player.increaseStamina(10);
        }
    }

    /**
     * Returns won side if someone won or null
     * @return won side
     */
    public @Nullable GameSide checkIfSomeoneWon() {
        if(getKindPlayersHP() <= 0) {
            setWonSide(GameSide.EVIL);
            Log.d(TAG, "checkIfSomeoneWon: returning evil side. | " + this.toString());
        } else if(getEvilPlayersHP() <= 0) {
            setWonSide(GameSide.KIND);
            Log.d(TAG, "checkIfSomeoneWon: returning kind side. | " + this.toString());
        }
        return getWonSide();
    }

    @Override
    public String toString() {
        return "GameStatsPacket{" +
                "playersList=" + playersList.toString() +
                ", phase=" + phase +
                '}';
    }
}
