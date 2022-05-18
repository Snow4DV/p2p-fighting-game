package itacademy.snowadv.fightinggamep2p.Classes;

import android.graphics.Canvas;
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

    public void drawAllDrawablesOnCanvas(Canvas canvas) {
        for(CanvasDrawable drawable : this) {
            drawable.drawFrame(canvas);
        }
    }
}
