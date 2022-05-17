package itacademy.snowadv.fightinggamep2p.Fragments.BattleField;import android.content.Context;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.graphics.Canvas;import android.graphics.Color;import android.graphics.Paint;import android.graphics.Rect;import android.graphics.Typeface;import android.text.TextPaint;import android.util.Log;import android.view.SurfaceHolder;import android.view.SurfaceView;import androidx.annotation.NonNull;import androidx.core.content.res.ResourcesCompat;import java.util.Calendar;import itacademy.snowadv.fightinggamep2p.Classes.BattleUnit;import itacademy.snowadv.fightinggamep2p.Classes.DrawablesContainer;import itacademy.snowadv.fightinggamep2p.Classes.Field;import itacademy.snowadv.fightinggamep2p.Classes.SpriteAnimation;import itacademy.snowadv.fightinggamep2p.Classes.CanvasDrawable;import itacademy.snowadv.fightinggamep2p.R;/*package-private*/ class BattleFieldSurfaceView extends SurfaceView implements SurfaceHolder.Callback {    private static final String TAG = "BattleFieldSurfaceView";    private final Context context;    private BattleFieldDrawThread drawThread;    private final DrawablesContainer drawables = new DrawablesContainer();    private Bitmap background;    public BattleFieldSurfaceView(Context context) {        super(context);        this.context = context;        getHolder().addCallback(this);    }    @Override    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {        Log.d(TAG, "Surface created;");        drawThread = new BattleFieldDrawThread(surfaceHolder, getContext());        drawThread.start();    }    @Override    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {        Log.d(TAG, "Surface changed;");    }    @Override    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {        Log.d(TAG, "Surface destroyed;");        drawThread.interrupt();        drawables.clear();    }    private class BattleFieldDrawThread extends Thread{        private final SurfaceHolder surfaceHolder;        private Paint paint;        private TextPaint textPaint;        private Context parentContext;        private static final int FPS = 20;        Calendar calendar;        private  long nextUpdateTime;        public BattleFieldDrawThread(SurfaceHolder surfaceHolder, Context parentContext) {            background = BitmapFactory.decodeResource(context.getResources(),                    R.drawable.schoolboy);            this.surfaceHolder = surfaceHolder;            this.parentContext = parentContext;            calendar = Calendar.getInstance();            nextUpdateTime = calendar.getTimeInMillis();            initTools();        }        private void initTools() {            paint = new Paint();            textPaint = new TextPaint();            textPaint.setTextSize(50);            textPaint.setTextAlign(Paint.Align.CENTER);            textPaint.setColor(Color.BLACK);            Typeface typeface = ResourcesCompat.getFont(context, R.font.gouranga_pixel);            textPaint.setTypeface(typeface);            paint.setColor(Color.GREEN);            paint.setStyle(Paint.Style.FILL);        }        @Override        public void run() {            Field.spawnFieldsAndCopy(3, 3, getWidth(), getHeight(), context,                    drawables);            BattleUnit battleCharacter = BattleUnit.getAttachedToField(                    Field.getBottomField(0), SpriteAnimation.getAnimation(                            SpriteAnimation.CharacterAnimation.SCHOOLBOY_THROWING_PAPER, context));            battleCharacter.getSpriteAnimation().startAnimation();            drawables.add(battleCharacter);            // test            /*Sprite sprite = new Sprite(SpriteAnimation.getAnimation(                    SpriteAnimation.CharacterAnimation.SCHOOLBOY_THROWING_PAPER, context,                    null, null),                    new Point(getWidth() / 2, getHeight() / 2), Sprite.SnapLocation.CENTER);            sprite.getSpriteAnimation().startAnimation();            sprite.setLocation(getWidth()/2, getHeight()/2);            sprite.setScale(0.5f);            drawables.add(sprite);*/            // test            while(!interrupted()){                if(nextUpdateTime - calendar.getTimeInMillis() > 0) {                    try {                        Thread.sleep(nextUpdateTime - calendar.getTimeInMillis());                    } catch(InterruptedException ex) {                        /*                        Interrupted exception will always be called when Thread.sleep() called                        so there's no need to handle it                         */                    }                }                nextUpdateTime = calendar.getTimeInMillis() + 1000/FPS;                Canvas canvas = surfaceHolder.lockCanvas();                if(canvas == null) continue;                if(getWidth() < getHeight()) { // In case the device in portrait mode                    canvas.drawColor(Color.WHITE);                    canvas.drawText("Переверните устройство для того,", getWidth()/2f,                            getHeight()/2f, textPaint);                    canvas.drawText("чтобы продолжить отрисовку", getWidth()/2f ,                            getHeight()/2f + 50, textPaint);                } else { // Game drawing starts here                    canvas.drawBitmap(background, null, new Rect(0, 0, getWidth(),                            getHeight()), paint);                    canvas.drawColor(Color.WHITE);                    float spaceBetweenFields = 40f;                    float bmWidth = getWidth() / 3.25f;                    float bmHeight = bmWidth/2; //fields aspect ratio is 2:1                    float topPadding =  getWidth()/12f;                    float bottomPadding =  getWidth()/35f;                    for(CanvasDrawable drawable : drawables) {                        drawable.drawFrame(canvas);                    }            }                surfaceHolder.unlockCanvasAndPost(canvas);        }    }    }    protected abstract static class BattleFieldDrawState { // bullshit        private static int kindSidePlayersCount = 0;        private static int evilSidePlayersCount = 0;        public static int totalPlayersCount() {            return kindSidePlayersCount + evilSidePlayersCount;        }        public static int getKindSidePlayersCount() {            return kindSidePlayersCount;        }        public static void setKindSidePlayersCount(int kindSidePlayersCount) {            BattleFieldDrawState.kindSidePlayersCount = kindSidePlayersCount;        }        public static int getEvilSidePlayersCount() {            return evilSidePlayersCount;        }        public static void setEvilSidePlayersCount(int evilSidePlayersCount) {            BattleFieldDrawState.evilSidePlayersCount = evilSidePlayersCount;        }    }}