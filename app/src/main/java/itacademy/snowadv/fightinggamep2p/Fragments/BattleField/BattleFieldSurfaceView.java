package itacademy.snowadv.fightinggamep2p.Fragments.BattleField;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import itacademy.snowadv.fightinggamep2p.R;

/*package-private*/ class BattleFieldSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "BattleFieldSurfaceView";
    private Context context;
    private BattleFieldDrawThread drawThread;

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
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        Log.d(TAG, "Surface destroyed;");
        drawThread.requestStop();
        while(true) {
            try {
                drawThread.join();
                return;
            } catch(InterruptedException ex) {}
        }
    }


    private class BattleFieldDrawThread extends Thread{
        private SurfaceHolder surfaceHolder;
        private boolean isRunning = false;
        private Paint paint;
        private TextPaint textPaint;
        private Context parentContext;

        public boolean isRunning() {
            return isRunning;
        }

        public void requestStop () {
            isRunning = false;
        }

        public BattleFieldDrawThread(SurfaceHolder surfaceHolder, Context parentContext) {
            this.surfaceHolder = surfaceHolder;
            this.parentContext = parentContext;
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
            isRunning = true;
            while(isRunning){
                Canvas canvas = surfaceHolder.lockCanvas();
                if(getWidth() < getHeight()) {
                    canvas.drawColor(Color.WHITE);
                    canvas.drawText("Переверните устройство для того,", getWidth()/2f, getHeight()/2f, textPaint);
                    canvas.drawText("чтобы продолжить отрисовку", getWidth()/2f , getHeight()/2f + 50, textPaint);
                } else {
                    float fieldPadding = 40f;
                    canvas.drawColor(Color.BLUE);
                    Bitmap fieldBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.field);

                    for(int offsetY = 0; offsetY < getHeight(); offsetY += getHeight()/3.25f + 200) {
                        for (int offset = 0; offset < getWidth() - getWidth() / 3.25f; offset += getWidth() / 3.25f + fieldPadding) {
                            canvas.drawBitmap(fieldBitmap, null, new RectF(offset, 100 + offsetY, getWidth() / 3.25f + offset, getWidth() / 3.25f / 2 + 100 + offsetY), paint);
                        }
                    }
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    protected static class BattleFieldDrawState {
        private int playersCount = 0;

        public int getPlayersCount() {
            return playersCount;
        }

        public void setPlayersCount(int playersCount) {
            this.playersCount = playersCount;
        }
    }

}