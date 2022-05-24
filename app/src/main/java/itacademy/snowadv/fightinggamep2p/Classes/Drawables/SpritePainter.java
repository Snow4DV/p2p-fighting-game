package itacademy.snowadv.fightinggamep2p.Classes.Drawables;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Every sprite painter class should implement SpritePainter - only animations are used in my
 * project but had plain skin class in my thoughts or some other thing
 */
public interface SpritePainter {
    void drawNextFrame(Canvas canvas, Rect destination);
    int getFrameWidth();
    int getFrameHeight();
}
