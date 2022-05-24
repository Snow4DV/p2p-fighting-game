package itacademy.snowadv.fightinggamep2p.Classes.Drawables;

import java.util.ArrayList;
import java.util.List;

/**
 * BattleUnits can't and shouldn't be serialized because it contains graphics logic. That's
 * why there's a container which stores the battle units and battlePlayer only has id for it.
 */
public class BattleUnitContainer {
    private final List<DrawableBattleUnit> drawableBattleUnits = new ArrayList<>();


    /**
     * Stores an object in the container and returns an ID to access it later
     * @param drawableBattleUnit BattleUnit to store
     * @return ID of battle unit
     */
    public int storeBattleUnitAndGetID(DrawableBattleUnit drawableBattleUnit) {
        drawableBattleUnits.add(drawableBattleUnit);
        return drawableBattleUnits.size() - 1;
    }

    public DrawableBattleUnit getBattleUnitByID(int id) {
        return drawableBattleUnits.get(id);
    }

    /**
     * For debug purposes
     * @return string with data of array
     */
    public String getListAsString() {
        return drawableBattleUnits.toString();
    }
}