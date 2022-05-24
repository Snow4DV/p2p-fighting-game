package itacademy.snowadv.fightinggamep2p.Classes.Server;

import android.content.Context;
import android.util.Log;

import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Drawables.DrawableBattleUnit;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.BattleUnitContainer;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.DrawableCriminal;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.DrawableCriminalBoss;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.DrawablePoliceman;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.DrawableSchoolboy;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.DrawablesContainer;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.Field;
import itacademy.snowadv.fightinggamep2p.R;
import itacademy.snowadv.fightinggamep2p.Sound.SoundPlayer;

/**
 * Server-client class that represents the player's state
 */
public class BattlePlayer {
    private static final String TAG = "BattlePlayer";
    public enum BattlePlayerName {
        SCHOOLBOY(R.drawable.schoolboy_preview, true),
        CRIMINAL(R.drawable.criminal_preview, false),
        CRIMINAL_BOSS(R.drawable.criminal_boss_preview, false),
        POLICEMAN(R.drawable.policeman_preview, true);

        public final int drawableId;
        private final boolean isKind;
        private final boolean abilityForWholeTeam = false;
        private static final BattlePlayerName[] vals = values();
        public BattlePlayerName next()
        {
            return vals[(this.ordinal()+1) % vals.length];
        }

        public BattlePlayerName prev()
        {
            int id = (this.ordinal()-1);
            if(id == -1) id = vals.length - 1;
            return vals[id];
        }

        public boolean isKind() {
            return isKind;
        }
        BattlePlayerName(int drawableId, boolean isKind) {
            this.drawableId = drawableId;
            this.isKind = isKind;
        }
    }

    public enum BattlePlayerAction {
        LIGHT_KICK, HARD_KICK, ABILITY
    }



    public BattlePlayer(BattlePlayerName player, String name) {
        this.player = player;
        this.name = name;
    }

    public BattlePlayer() {
        // Empty constructor is needed for Kryo's serialization. Keep it!
    }


    private BattlePlayerName player;
    private String name;
    private String ipAddress;
    private int connectionID;
    private Integer battleUnitID = null;

    private int health = 100;
    private int stamina = 100;
    private boolean abilityToStep = false;

    public void setAbilityToStep(boolean isAbleToStep) {
        abilityToStep = isAbleToStep;
    }
    public boolean getAbilityToStep() {
        return this.abilityToStep;
    }
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }


    public BattlePlayerName getPlayerName() {
        return player;
    }

    public void setPlayer(BattlePlayerName player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public boolean isAlive() {
        return health > 0 && stamina > 0;
    }

    public void decreaseHp(int health) {
        this.health -=health;
        if(this.health < 0) {
            this.health = 0;
        }
    }
    public void decreaseStamina(int stamina) {
        this.stamina -=stamina;
        if(this.stamina < 0) {
            this.stamina = 0;
        }
    }
    public void increaseHp(int health) {
        this.health +=health;
        if(this.health > 100) {
            this.health = 100;
        }
    }
    public void increaseStamina(int stamina) {
        this.stamina +=stamina;
        if(this.stamina > 100) {
            this.stamina = 100;
        }
    }




    public int getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(int connectionID) {
        this.connectionID = connectionID;
    }

    public DrawableBattleUnit getAssignedBattleUnit(BattleUnitContainer container) {
        if(battleUnitID == null) {
            return null;
        }
        Log.d(TAG, "Trying to receive: " + battleUnitID + ". data now:\n"
                + container.getListAsString());
        return container.getBattleUnitByID(battleUnitID);
    }



    public void assignBattleUnit(DrawableBattleUnit assignedDrawableBattleUnit,
                                 BattleUnitContainer container, SoundPlayer soundPlayer) {
        assignedDrawableBattleUnit.assignSoundPlayer(soundPlayer);
        battleUnitID = container.storeBattleUnitAndGetID(assignedDrawableBattleUnit);
        Log.d(TAG, "assignBattleUnit stored: " + battleUnitID + ". data now:\n" +
                container.getListAsString());
    }

    public static void assignAllBattleUnitsToFields(List<BattlePlayer> playersList, Context context,
                                                    BattleUnitContainer container,
                                                    SoundPlayer soundPlayer) {
        int assignedKinds = 0;
        int assignedEvils = 0;
        for (BattlePlayer player :
                playersList) {
            Field field;
            if(player.player.isKind()) {
                field = Field.getBottomField(assignedKinds++);
            } else {
                field = Field.getTopField(assignedEvils++);
            }
            switch (player.getPlayerName()) {
                case SCHOOLBOY:
                    player.assignBattleUnit(DrawableSchoolboy.getAttachedToField(field, context),
                            container, soundPlayer);
                    break;
                case POLICEMAN:
                    player.assignBattleUnit(DrawablePoliceman.getAttachedToField(field, context),
                            container, soundPlayer);
                    break;
                case CRIMINAL:
                    player.assignBattleUnit(DrawableCriminal.getAttachedToField(field, context),
                            container, soundPlayer);
                    break;
                case CRIMINAL_BOSS:
                    player.assignBattleUnit(DrawableCriminalBoss.getAttachedToField(field, context),
                            container, soundPlayer);
                    break;
            }
        }
    }

    public static void addAllBattleUnitsToDrawables(List<BattlePlayer> playersList,
                                                    DrawablesContainer drawables,
                                                    BattleUnitContainer container) {
        for (BattlePlayer player :
                playersList) {
            drawables.add(player.getAssignedBattleUnit(container));
        }
    }

    @Override
    public String toString() {
        return "BattlePlayer{" +
                "name='" + name + '\'' +
                ", connectionID=" + connectionID +
                ", health=" + health +
                ", stamina=" + stamina +
                ", isAlive=" + isAlive() +
                ", isKind=" + player.isKind +
                '}';
    }
}
