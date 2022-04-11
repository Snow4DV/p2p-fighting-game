package itacademy.snowadv.fightinggamep2p.Fragments.BattleField;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

/*package-private*/ class BattleFieldSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Context context;
    public BattleFieldSurfaceView(Context context) {
        super(context);
        this.context = context;
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }


    private class BattleFieldDrawThread extends Thread{
        private SurfaceHolder surfaceHolder;
        private boolean isRunning = false;

        public boolean isRunning() {
            return isRunning;
        }

        public void stop (boolean running) {
            isRunning = false;
        }

        public BattleFieldDrawThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        @Override
        public void run() {
            isRunning = true;
            while(isRunning){
                Canvas canvas = surfaceHolder.lockCanvas();

            }
        }
    }

}