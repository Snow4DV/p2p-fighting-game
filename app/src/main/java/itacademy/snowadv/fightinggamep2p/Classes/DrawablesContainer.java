package itacademy.snowadv.fightinggamep2p.Classes;

import android.util.Log;

import java.util.ArrayList;

public class DrawablesContainer extends ArrayList<CanvasDrawable> {
    @Override
    public void clear() {
        for(CanvasDrawable drawable: this) {
            drawable.clear();
        }
        super.clear();
    }
}
