package itacademy.snowadv.fightinggamep2p.Classes.DrawableBattleUnits;

import java.util.ArrayList;
import java.util.List;

public class BattleUnitContainer {
    private static final List<DrawableBattleUnit> DRAWABLE_BATTLE_UNITS = new ArrayList<>();


    /**
     * Stores an object in the container and returns an ID to access it later
     * @param drawableBattleUnit BattleUnit to store
     * @return ID of battle unit
     */
    public static int storeBattleUnitAndGetID(DrawableBattleUnit drawableBattleUnit) {
        DRAWABLE_BATTLE_UNITS.add(drawableBattleUnit);
        return DRAWABLE_BATTLE_UNITS.size() - 1;
    }

    public static DrawableBattleUnit getBattleUnitByID(int id) {
        return DRAWABLE_BATTLE_UNITS.get(id);
    }

    /**
     * For debug purposes
     * @return string with data of array
     */
    public static String getListAsString() {
        return DRAWABLE_BATTLE_UNITS.toString();
    }
}