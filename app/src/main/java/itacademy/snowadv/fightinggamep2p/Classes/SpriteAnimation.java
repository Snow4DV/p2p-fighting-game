package itacademy.snowadv.fightinggamep2p.Classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.annotation.Nullable;

import itacademy.snowadv.fightinggamep2p.R;

/**
 * Animation class handles the canvas drawing process
 */
public class SpriteAnimation implements SpritePainter {

    public enum CharacterAnimation {
        SCHOOLBOY_TALKING, SCHOOLBOY_THROWING_PAPER, SCHOOLBOY_IDLE, FIELD_IDLE
    }
    /**
     *
     * @param animation Animation enum
     * @param context Context to get the resources
     * @return Animation object that can be drawn by character object
     */
    public static SpriteAnimation getAnimation(CharacterAnimation animation, Context context) {
        return getAnimation(animation, context, null, null);
    }
    /**
     *
     * @param animation Animation enum
     * @param context Context to get the resources
     * @param animationRangeStart No of first frame for cycling
     * @param animationRangeEnd No of last frame for cycling
     * @return Animation object that can be drawn by character object
     */
    public static SpriteAnimation getAnimation(CharacterAnimation animation, Context
            context, @Nullable Integer animationRangeStart,
                                               @Nullable Integer animationRangeEnd) {
        switch(animation) {
            case SCHOOLBOY_TALKING:
                return new SpriteAnimation(2, 1,
                        BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.animation_schoolboy_talking), animationRangeStart,
                        animationRangeEnd);
            case SCHOOLBOY_IDLE:
                return new SpriteAnimation(1, 1,
                        BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.schoolboy), animationRangeStart,
                        animationRangeEnd);
            case SCHOOLBOY_THROWING_PAPER:
                return new SpriteAnimation(5, 1,
                        BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.animation_schoolboy_throwing_paper), animationRangeStart,
                        animationRangeEnd);
            case FIELD_IDLE:
                return new SpriteAnimation(1, 1,
                        BitmapFactory.decodeResource(context.getResources(), R.drawable.field),
                        animationRangeStart, animationRangeEnd);
        }
        throw new IllegalArgumentException("Incorrect animation has been passed to the " +
                "animations factory.");
    }


    private static final Paint paint = new Paint();
    private int spriteSheetColumns;
    private int spriteSheetLines;
    private final Bitmap spriteSheet;

    private int animationRangeStart;
    private int animationRangeEnd;
    private int currentFrame;
    private boolean isPlaying = true;


    private SpriteAnimation(int spriteSheetColumns, int spriteSheetLines,
                            Bitmap spriteSheet, Integer
                                             animationRangeStart, Integer animationRangeEnd) {
        if(spriteSheetColumns < 1 || spriteSheetLines < 1) {
            throw new IllegalArgumentException("Bad spriteSheetColumns or spriteSheetLines " +
                    "argument: should be 1 or larger");
        }

        this.spriteSheetColumns = spriteSheetColumns;
        this.spriteSheetLines = spriteSheetLines;
        this.spriteSheet = spriteSheet;
        this.animationRangeStart = animationRangeStart == null ? 0 : animationRangeStart;
        this.animationRangeEnd = animationRangeEnd == null ?
                spriteSheetLines * spriteSheetColumns - 1 : animationRangeEnd;
    }

    public void startAnimation(){
        isPlaying = true;
    }

    public void stopAnimation(){
        isPlaying = false;
    }

    public int getFrameWidth(){
        return spriteSheet.getWidth()/ spriteSheetColumns;
    }

    public int getFrameHeight(){
        return spriteSheet.getHeight()/ spriteSheetLines;
    }

    public void updateAnimationRange(int animationRangeStart, int animationRangeEnd) {
        if(animationRangeStart > spriteSheetColumns * spriteSheetLines - 1 || animationRangeEnd
                < animationRangeStart) {
            throw new IllegalArgumentException("Incorrect range is passed to " +
                    "updateAnimationRange: from " + animationRangeStart + " to " + animationRangeEnd);
        }

        this.animationRangeStart = animationRangeStart;
        this.animationRangeEnd = animationRangeEnd;
    }

    public void drawNextFrame(Canvas canvas, Rect destination){
        if(!isPlaying) return;
        drawSingleFrame(canvas, currentFrame, destination);
        if(++currentFrame > animationRangeEnd) {
            currentFrame = animationRangeStart;
        }
    }

    /**
     *
     * @param canvas Canvas where frame will be drawn
     * @param frameNo No of frame
     */
    final void drawSingleFrame(Canvas canvas, int frameNo, Rect destination) {
        if(!isPlaying) return;

        int frameColumn = frameNo % spriteSheetColumns;
        int frameLine = frameNo / spriteSheetColumns;

        if(frameNo > spriteSheetColumns * spriteSheetLines - 1 ) {
            throw new IllegalArgumentException("There's no frame " + frameNo +
                    " in the sprite sheet.");
        }

        drawSingleFrame(canvas, frameColumn, frameLine, destination);
    }
    /**

     * @param canvas Canvas where frame will be drawn
     * @param frameColumn Frame column no from the spritesheet
     * @param frameLine Frame line no from the spritesheet
     */
    final void drawSingleFrame(Canvas canvas, int frameColumn, int frameLine, Rect destination){
        if(!isPlaying) return;

        if(frameColumn > spriteSheetColumns - 1|| frameLine > spriteSheetLines - 1) {
            throw new IllegalArgumentException("There is no frame on [" + spriteSheetLines +
                    ',' + spriteSheetColumns + ']');
        }

        int spriteWidth = spriteSheet.getWidth()/ spriteSheetColumns;
        int spriteHeight = spriteSheet.getHeight()/ spriteSheetLines;

        Rect src = new Rect(spriteWidth * frameColumn, spriteHeight * frameLine,
                spriteWidth * (frameColumn+1), spriteHeight * (frameLine+1));

        canvas.drawBitmap(spriteSheet, src, destination, paint);
    }


}


