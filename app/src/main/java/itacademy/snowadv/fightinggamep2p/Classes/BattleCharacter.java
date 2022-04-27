package itacademy.snowadv.fightinggamep2p.Classes;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

public class BattleCharacter implements CanvasDrawable {

    private BattleCharacterAnimation characterAnimation;
    private final Point location = new Point(); // Point of sprite's center
    private float scale = 1.0f; // Bitmap scale multiplier - from 0 to 1.0f

    public BattleCharacter(BattleCharacterAnimation characterAnimation, Point locationCenter) {
        this.characterAnimation = characterAnimation;
    }

    @Override
    public void drawFrame(Canvas canvas) {
        if(characterAnimation == null) return;
        characterAnimation.drawNextFrame(canvas, getDestinationRectByCenter());
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(int x, int y) {
        location.set(x, y);
    }

    public int getX() {
        return location.x;
    }

    public int getY() {
        return location.y;
    }

    public BattleCharacterAnimation getCharacterAnimation() {
        return characterAnimation;
    }

    public void setCharacterAnimation(BattleCharacterAnimation characterAnimation) {
        this.characterAnimation = characterAnimation;
    }

    /**
     * Returns bitmap destination rectangle
     */
    private Rect getDestinationRectByCenter() {
        // TODO: frame width can be not divisible by 2. Should be fixed probably?
        int leftTopPositionX = (int) (location.x - characterAnimation.getFrameWidth() * scale /2);
        int leftTopPositionY = (int) (location.y - characterAnimation.getFrameHeight() * scale/2);
        int rightBottomPositionX = (int) (location.x + characterAnimation.getFrameWidth() * scale/2);
        int rightBottomPositionY = (int) (location.y + characterAnimation.getFrameHeight() * scale/2);
        return new Rect(leftTopPositionX, leftTopPositionY,
                rightBottomPositionX, rightBottomPositionY);
    }

    /**
     * Returns bitmap destination rectangle
     * @param locationTopLeft Point of top left corner
     * @param scale Bitmap scale multiplier - from 0 to 1.0f
     */
    public Rect getDestinationRectByTopLeft(Point locationTopLeft, float scale) {
        int leftTopPositionX = locationTopLeft.x;
        int leftTopPositionY = locationTopLeft.y;
        int rightBottomPositionX = (int) (locationTopLeft.x + characterAnimation.getFrameWidth() * scale);
        int rightBottomPositionY = (int) (locationTopLeft.y + characterAnimation.getFrameHeight() * scale);
        return new Rect(leftTopPositionX, leftTopPositionY,
                rightBottomPositionX, rightBottomPositionY);
    }

}
