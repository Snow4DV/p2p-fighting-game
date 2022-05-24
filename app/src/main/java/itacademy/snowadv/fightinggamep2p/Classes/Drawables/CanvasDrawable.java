package itacademy.snowadv.fightinggamep2p.Classes.Drawables;

import android.graphics.Canvas;

/**
 * Interface that is used for every sprite that will be drawn on a canvas
 */
public interface CanvasDrawable {
    /**
     * This method is being called every frame to make sprite do the render job
     * @param canvas Canvas that sprite will be drawn on
     */
    void drawFrame(Canvas canvas);

    /**
     * Sprite destructor for under-hood work
     */
    void clear();
}
