package itacademy.snowadv.fightinggamep2p.Classes.Drawables;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Sprite class that can be scaled, moved, can have animation and implements Canvas drawable
 * interface in order to be drawn on the screen
 */
public class Sprite implements CanvasDrawable {

    private static final String TAG = "SpriteObject";

    public enum SnapLocation {
        CENTER, BOTTOM_LEFT, TOP_LEFT, BOTTOM_RIGHT
    }

    private SpritePainter spritePainter;
    /** Snap location determines which point of sprite is set by location: center, bottomLeft or
     * topLeft*/
    private SnapLocation snapLocation;
    private final Point location = new Point(); // Point of sprite's center
    private float scale = 1f; // Bitmap scale multiplier

    public Sprite(SpritePainter spritePainter, Point location, SnapLocation snapLocation) {
        this.spritePainter = spritePainter;
        this.snapLocation = snapLocation;
        this.location.set(location.x, location. y);
    }

    public SnapLocation getSnapLocation() {
        return snapLocation;
    }

    public void setSnapLocation(SnapLocation snapLocation) {
        this.snapLocation = snapLocation;
    }

    @Override
    public void drawFrame(Canvas canvas) {
        if(spritePainter == null) return;
        spritePainter.drawNextFrame(canvas, getDestinationRect());
    }

    @Override
    public void clear() {
        Log.d(TAG, "clear: " + "Sprite " + this + " removed.");
    }

    public void setScaleByWidth(int width) {
        scale = (float)width/(float)spritePainter.getFrameWidth();
    }
    public void setScaleByHeight(int height) {
        scale = (float)height/(float)spritePainter.getFrameHeight();
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
    public SpritePainter getSpritePainter() {
        return spritePainter;
    }

    /**
     * Method that returns the animation if sprite has it
     * @return Sprite's animation
     */
    public @Nullable
    SpriteAnimation getSpriteAnimation() {
        if(spritePainter instanceof SpriteAnimation) {
            return (SpriteAnimation) spritePainter;
        }
        return null;
    }

    /**
     * Method to set the animation or static skin of the sprite
     * @param spritePainter Animation or skin
     */
    public void setSpritePainter(SpritePainter spritePainter) {
        this.spritePainter = spritePainter;
    }

    /**
     * Returns bitmap destination rectangle by center point
     */
    private Rect getDestinationRectByCenter() {
        // TODO: frame width can be not divisible by 2. Should be fixed probably?
        int leftTopPositionX = (int) (location.x - spritePainter.getFrameWidth() * scale/2);
        int leftTopPositionY = (int) (location.y - spritePainter.getFrameHeight() * scale/2);
        int rightBottomPositionX = (int) (location.x + spritePainter.getFrameWidth() * scale/2);
        int rightBottomPositionY = (int) (location.y + spritePainter.getFrameHeight() * scale/2);
        return new Rect(leftTopPositionX, leftTopPositionY,
                rightBottomPositionX, rightBottomPositionY);
    }

    /**
     * Returns bitmap destination rectangle by top left point
     */
    private Rect getDestinationRectByTopLeft() {
        int leftTopPositionX = location.x;
        int leftTopPositionY = location.y;
        int rightBottomPositionX = (int) (location.x + spritePainter.getFrameWidth() * scale);
        int rightBottomPositionY = (int) (location.y + spritePainter.getFrameHeight() * scale);
        return new Rect(leftTopPositionX, leftTopPositionY,
                rightBottomPositionX, rightBottomPositionY);
    }

    /**
     * Returns bitmap destination rectangle by bottom left point
     * @return Destination rectangle
     */
    private Rect getDestinationRectByBottomLeft() {
        int leftTopPositionX = location.x;
        int leftTopPositionY = (int) (location.y -  spritePainter.getFrameHeight() * scale);
        int rightBottomPositionX = (int) (location.x + spritePainter.getFrameWidth() * scale);
        int rightBottomPositionY = location.y;
        return new Rect(leftTopPositionX, leftTopPositionY,
                rightBottomPositionX, rightBottomPositionY);
    }

    private Rect getDestinationRectByBottomRight() {
        int leftTopPositionX = (int) (location.x - spritePainter.getFrameWidth() * scale);
        int leftTopPositionY = (int) (location.y -  spritePainter.getFrameHeight() * scale);
        int rightBottomPositionX = location.x;
        int rightBottomPositionY = location.y;
        return new Rect(leftTopPositionX, leftTopPositionY,
                rightBottomPositionX, rightBottomPositionY);
    }

    /**
     * Method to get destination rect determined by object's snap and point
     * @return Destination rect
     */
    private Rect getDestinationRect() {
        switch(snapLocation) {
            case TOP_LEFT:
                return getDestinationRectByTopLeft();
            case BOTTOM_LEFT:
                return getDestinationRectByBottomLeft();
            case BOTTOM_RIGHT:
                return getDestinationRectByBottomRight();
            default:
                Log.w(TAG, "getDestinationRect: Incorrect snap location received. Defaulting" +
                        " to CENTER.");
            case CENTER:
                return getDestinationRectByCenter(); // snap defaults to center
        }
    }

    public Point getBottomLeftPoint() {
        switch(snapLocation) {
            case CENTER:
                return new Point(location.x - spritePainter.getFrameWidth()/2,
                        location.y - getHeight()/2);
            case BOTTOM_LEFT:
                return new Point(location);
            case TOP_LEFT:
                return new Point(location.x, location.y + getHeight());
            case BOTTOM_RIGHT:
                return new Point(location.x - getWidth(), location.y);
            default:
                throw new IllegalArgumentException("Something is wrong with snapLocation");
        }
    }

    public int getWidth() {
        return (int) (spritePainter.getFrameWidth() * scale);
    }

    public int getHeight() {
        return (int) (spritePainter.getFrameHeight() * scale);
    }

}
