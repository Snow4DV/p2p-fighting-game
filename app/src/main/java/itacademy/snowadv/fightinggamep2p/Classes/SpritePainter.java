package itacademy.snowadv.fightinggamep2p.Classes;

import android.graphics.Canvas;
import android.graphics.Rect;

public interface SpritePainter {
    void drawNextFrame(Canvas canvas, Rect destination);
    int getFrameWidth();
    int getFrameHeight();
}
