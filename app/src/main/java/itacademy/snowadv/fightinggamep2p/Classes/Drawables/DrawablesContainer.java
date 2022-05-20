package itacademy.snowadv.fightinggamep2p.Classes.Drawables;

import android.graphics.Canvas;

import java.util.ArrayList;

import itacademy.snowadv.fightinggamep2p.Classes.Drawables.CanvasDrawable;

public class DrawablesContainer extends ArrayList<CanvasDrawable> {
    @Override
    public void clear() {
        for(CanvasDrawable drawable: this) {
            drawable.clear();
        }
        super.clear();
    }

    public void drawAllDrawablesOnCanvas(Canvas canvas) {
        for(CanvasDrawable drawable : this) {
            drawable.drawFrame(canvas);
        }
    }
}
