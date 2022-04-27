package itacademy.snowadv.fightinggamep2p.Fragments.BattleField;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import itacademy.snowadv.fightinggamep2p.Classes.BattleCharacter;
import itacademy.snowadv.fightinggamep2p.Classes.BattleCharacterAnimation;
import itacademy.snowadv.fightinggamep2p.Classes.CanvasDrawable;
import itacademy.snowadv.fightinggamep2p.R;

/*package-private*/ class BattleFieldSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "BattleFieldSurfaceView";
    private final Context context;
    private BattleFieldDrawThread drawThread;
    private final List<CanvasDrawable> drawables = new ArrayList<>();

    public BattleFieldSurfaceView(Context context) {
        super(context);
        this.context = context;
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Log.d(TAG, "Surface created;");
        drawThread = new BattleFieldDrawThread(surfaceHolder, getContext());
        drawThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d(TAG, "Surface changed;");
        drawThread.requestStop();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        Log.d(TAG, "Surface destroyed;");
        drawThread.requestStop();
        while(true) {
            try {
                drawThread.join();
                return;
            } catch(InterruptedException ex) {
                /*
                TODO: Remove the join statement. It is redurant!
                 */
            }
        }
    }


    private class BattleFieldDrawThread extends Thread{
        private final SurfaceHolder surfaceHolder;
        private boolean isRunning = false;
        private Paint paint;
        private TextPaint textPaint;
        private Context parentContext;
        private static final int FPS = 10;

        Calendar calendar;
        private  long nextUpdateTime;

        public boolean isRunning() {
            return isRunning;
        }

        public void requestStop () {
            isRunning = false;
        }

        public BattleFieldDrawThread(SurfaceHolder surfaceHolder, Context parentContext) {
            this.surfaceHolder = surfaceHolder;
            this.parentContext = parentContext;
            calendar = Calendar.getInstance();
            nextUpdateTime = calendar.getTimeInMillis();
            initTools();
        }

        private void initTools() {
            paint = new Paint();
            textPaint = new TextPaint();
            textPaint.setTextSize(50);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setColor(Color.BLACK);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.gouranga_pixel);
            textPaint.setTypeface(typeface);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void run() {
            // test
            BattleCharacter battleCharacter = new BattleCharacter(BattleCharacterAnimation.getAnimation(BattleCharacterAnimation.CharacterAnimation.SCHOOLBOY_THROWING_PAPER, context, null, null), new Point(getWidth() / 2, getHeight() / 2));
            battleCharacter.getCharacterAnimation().startAnimation();
            battleCharacter.setLocation(getWidth()/2, getHeight()/2);
            battleCharacter.setScale(0.5f);
            drawables.add(battleCharacter);
            // test
            isRunning = true;
            while(isRunning){ // TODO: replace with isInterrupted
                if(nextUpdateTime - calendar.getTimeInMillis() > 0) {
                    try {
                        Thread.sleep(nextUpdateTime - calendar.getTimeInMillis());
                    } catch(InterruptedException ex) {
                        /*
                        Interrupted exception will always be called when Thread.sleep() called
                        so there's no need to handle it
                         */
                    }
                }
                nextUpdateTime = calendar.getTimeInMillis() + 1000/FPS;

                Canvas canvas = surfaceHolder.lockCanvas();
                if(canvas == null) continue;
                if(getWidth() < getHeight()) { // In case the device in portrait mode
                    canvas.drawColor(Color.WHITE);
                    canvas.drawText("Переверните устройство для того,", getWidth()/2f,
                            getHeight()/2f, textPaint);
                    canvas.drawText("чтобы продолжить отрисовку", getWidth()/2f ,
                            getHeight()/2f + 50, textPaint);
                } else { // Game drawing starts here

                    canvas.drawColor(Color.BLUE);
                    Bitmap fieldBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.field);

                    float spaceBetweenFields = 40f;
                    float bmWidth = getWidth() / 3.25f;
                    float bmHeight = bmWidth/2; //fields aspect ratio is 2:1
                    float topPadding =  getWidth()/12f;
                    float buttomPadding =  getWidth()/35f;
                    // Draw kind forces fields
                    for (int offset = 0,i = 0; offset < getWidth() - bmWidth && i <
                            BattleFieldDrawState.getKindSidePlayersCount(); offset += bmWidth +
                            spaceBetweenFields, i++) {
                        RectF dst = new RectF(offset, topPadding, bmWidth + offset,
                                bmHeight + topPadding);
                        canvas.drawBitmap(fieldBitmap, null, dst, paint);


                    }
                    // Draw evil forces fields
                    for (int offset = 0, i = 0; offset < getWidth() - bmWidth
                            && i < BattleFieldDrawState.getEvilSidePlayersCount();
                         offset += bmWidth + spaceBetweenFields, i++) {
                        RectF dst = new RectF(getWidth() - bmWidth - offset,
                                getHeight() - bmHeight - buttomPadding,
                                getWidth() - offset, getHeight() - buttomPadding);
                        canvas.drawBitmap(fieldBitmap, null, dst, paint);
                    }

                    for(CanvasDrawable drawable : drawables) {
                        drawable.drawFrame(canvas);
                    }

            }
                surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    }
    protected abstract static class BattleFieldDrawState {
        private static int kindSidePlayersCount = 0;
        private static int evilSidePlayersCount = 0;

        public static int totalPlayersCount() {
            return kindSidePlayersCount + evilSidePlayersCount;
        }
        public static int getKindSidePlayersCount() {
            return kindSidePlayersCount;
        }

        public static void setKindSidePlayersCount(int kindSidePlayersCount) {
            BattleFieldDrawState.kindSidePlayersCount = kindSidePlayersCount;
        }

        public static int getEvilSidePlayersCount() {
            return evilSidePlayersCount;
        }

        public static void setEvilSidePlayersCount(int evilSidePlayersCount) {
            BattleFieldDrawState.evilSidePlayersCount = evilSidePlayersCount;
        }
    }

}