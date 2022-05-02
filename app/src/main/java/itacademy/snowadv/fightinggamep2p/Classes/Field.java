package itacademy.snowadv.fightinggamep2p.Classes;

import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Field extends Sprite {
    /* multiplier sets height of field corresponding to the view height (1.0 means field's  height
    * will equal view's height */
    private static final double FIELD_HEIGHT_MULTIPLIER = 0.25;
    private static final float spaceBetweenFields = 40f;
    private static List<Field> fieldsTop = new ArrayList<>();
    private static List<Field> fieldsBottom = new ArrayList<>();

    private Field(SpritePainter spritePainter, Point location, SnapLocation snapLocation) {
        super(spritePainter, location, snapLocation);
    }

    /**
     * This method spawns the fields and copies them to the drawables list
     * @param topCount Amount of fields on the top
     * @param bottomCount Amount of fields on the bottom
     * @param viewWidth Width of game view
     * @param viewHeight Height of game view
     * @param context Context to get the drawables
     * @param drawables List where drawables are stored
     */
    public static void spawnFieldsAndCopy(int topCount, int bottomCount, int viewWidth, int viewHeight,
                                          Context context, List<CanvasDrawable> drawables) {
        spawnFields(topCount, bottomCount, viewWidth, viewHeight, context);
        copyToDrawables(drawables);
    }
    /**
     * This method spawns the fields and stores them in the internal list that can be accessed
     * through the getter methods
     * @param topCount Amount of fields on the top
     * @param bottomCount Amount of fields on the bottom
     * @param viewWidth Width of game view
     * @param viewHeight Height of game view
     * @param context Context to get the drawables
     */
    public static void spawnFields(int topCount, int bottomCount, int viewWidth, int viewHeight,
                            Context context) {
        SpriteAnimation fieldIdleAnimation = SpriteAnimation.getAnimation(
                SpriteAnimation.CharacterAnimation.FIELD_IDLE, context, null, null);

        int topPadding =  viewHeight/8;
        int bottomPadding =  viewHeight/35;
        int fieldPadding = viewWidth/40;
        /* Bottom fields spawning */
        for (int offset = 0, i = 0; offset < viewWidth
                && i < bottomCount; i++) {
            Field field = new Field(fieldIdleAnimation, new Point(viewWidth - offset,
                    viewHeight - topPadding), SnapLocation.BOTTOM_RIGHT);
            field.setScaleByHeight((int) (viewHeight * FIELD_HEIGHT_MULTIPLIER));
            offset += field.getWidth() + fieldPadding;
            fieldsBottom.add(field);
        }
        /* Top fields spawning */
        for (int offset = 0, i = 0; offset < viewWidth
                && i < topCount; i++) {
            Field field = new Field(fieldIdleAnimation, new Point(offset,
                    topPadding), SnapLocation.TOP_LEFT);
            field.setScaleByHeight((int) (viewHeight * FIELD_HEIGHT_MULTIPLIER));
            offset += field.getWidth() + fieldPadding;
            fieldsTop.add(field);
        }

    }

    public static Field getTopField(int index) {
        return fieldsTop.get(index);
    }
    public static Field getBottomField(int index) {
        return fieldsBottom.get(index);
    }
    public static Point getTopFieldBottomLeftPoint(int index) {
        return fieldsTop.get(index).getBottomLeftPoint();
    }
    public static Point getBottomFieldBottomLeftPoint(int index) {
        return fieldsBottom.get(index).getBottomLeftPoint();
    }
    public List<Field> getTopFieldsList() {
        return fieldsTop;
    }
    public List<Field> getBottomFieldsList() {
        return fieldsBottom;
    }

    /**
     * Method copies drawables to list that is used by thread to draw
     * @param drawables list where drawables are stored
     */
    public static void copyToDrawables(List<CanvasDrawable> drawables) {
        drawables.addAll(fieldsTop);
        drawables.addAll(fieldsBottom);
    }

    /**
     * Method removes fields from drawables and objects itself
     * @param drawables list where drawables are stored
     */
    public static void clearFields(List<CanvasDrawable> drawables) {
        drawables.removeAll(fieldsBottom);
        drawables.removeAll(fieldsTop);
        fieldsTop.clear();
        fieldsBottom.clear();
    }
}
