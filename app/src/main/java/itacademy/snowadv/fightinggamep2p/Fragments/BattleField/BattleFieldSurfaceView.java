package itacademy.snowadv.fightinggamep2p.Fragments.BattleField;import android.app.Activity;import android.content.Context;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.graphics.Canvas;import android.graphics.Color;import android.graphics.Paint;import android.graphics.Rect;import android.graphics.Typeface;import android.graphics.drawable.ColorDrawable;import android.os.Handler;import android.os.Looper;import android.text.TextPaint;import android.util.Log;import android.view.LayoutInflater;import android.view.SurfaceHolder;import android.view.SurfaceView;import android.view.View;import android.widget.TextView;import androidx.annotation.NonNull;import androidx.appcompat.app.AlertDialog;import androidx.core.content.res.ResourcesCompat;import java.util.Calendar;import java.util.List;import itacademy.snowadv.fightinggamep2p.Classes.Drawables.BattleUnitContainer;import itacademy.snowadv.fightinggamep2p.Classes.Drawables.DrawableBattleUnit;import itacademy.snowadv.fightinggamep2p.Classes.Drawables.DrawablesContainer;import itacademy.snowadv.fightinggamep2p.Classes.Drawables.Field;import itacademy.snowadv.fightinggamep2p.Classes.Notifiable;import itacademy.snowadv.fightinggamep2p.Classes.Server.GameServer;import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ChatMessage;import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameActionRequest;import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameStatsPacket;import itacademy.snowadv.fightinggamep2p.Classes.Server.BattlePlayer;import itacademy.snowadv.fightinggamep2p.R;import itacademy.snowadv.fightinggamep2p.Sound.SoundPlayer;/*package-private*/ class BattleFieldSurfaceView extends SurfaceView implements SurfaceHolder.Callback {    private static final String TAG = "BattleFieldSurfaceView";    private final Context context;    private final ServerBattleFragment serverBattleFragment;    private BattleFieldDrawThread drawThread;    private final DrawablesContainer drawables = new DrawablesContainer();    private Bitmap background;    private final GameServer gameServer;    private GameThread gameThread;    private final Object gameThreadSyncObject = new Object();    private final BattleUnitContainer battleUnitContainer;    private final SoundPlayer soundPlayer;    public BattleFieldSurfaceView(Context context, GameServer gameServer,                                  ServerBattleFragment serverBattleFragment,                                  BattleUnitContainer container) {        super(context);        this.context = context;        this.battleUnitContainer = container;        this.gameServer = gameServer;        soundPlayer = SoundPlayer.build(context);        gameServer.setCallbackForBattleSurfaceView(object -> {            if(drawThread == null || drawThread.isInterrupted()) {                return; // Return if drawing thread is dead            }            if(object instanceof ChatMessage) {                ChatMessage msg = (ChatMessage) object;                serverBattleFragment.addChatMessageToLog(msg);            } else if(object instanceof GameActionRequest) {                GameActionRequest request = (GameActionRequest) object;                BattlePlayer requestedPlayer = gameServer.getGameStatsPacket()                        .getBattlePlayerByConnectionID(request.player.getConnectionID());                requestedPlayer.setAbilityToStep(false);                switch(request.action) {                    case ABILITY:                        requestedPlayer.getAssignedBattleUnit(battleUnitContainer).ability(requestedPlayer,                                context, gameServer.getGameStatsPacket().getPlayersList());                        serverBattleFragment.addChatMessageToLog(new ChatMessage(requestedPlayer,                                " способность \"" + DrawableBattleUnit                                        .getAbilityNameByBattlePlayerName(                                                requestedPlayer.getPlayerName())                                        + "\" испольована на всей команде"));                        break;                    case HARD_KICK:                        requestedPlayer.getAssignedBattleUnit(battleUnitContainer).hardKick(requestedPlayer,                                context, gameServer.getGameStatsPacket()                                        .getBattlePlayerByConnectionID(request.affectedPlayer                                                .getConnectionID()));                        serverBattleFragment.addChatMessageToLog(new ChatMessage(requestedPlayer,                                " сильная атака \"" + DrawableBattleUnit                                        .getHardAttackNameByBattlePlayerName(                                                requestedPlayer.getPlayerName())                                        + "\" использована на " + request.affectedPlayer.getName()));                        break;                    case LIGHT_KICK:                        requestedPlayer.getAssignedBattleUnit(battleUnitContainer).lightKick(requestedPlayer,                                context, gameServer.getGameStatsPacket()                                        .getBattlePlayerByConnectionID(request.affectedPlayer.getConnectionID()));                        serverBattleFragment.addChatMessageToLog(new ChatMessage(requestedPlayer,                                " слабая атака \"" + DrawableBattleUnit                                        .getLightAttackNameByBattlePlayerName(                                                requestedPlayer.getPlayerName())                                        + "\" использована на " + request.affectedPlayer.getName()));                }                updateIdleAnimationsOfPlayers(gameServer.getGameStatsPacket().getPlayersList());                changePhaseIfEveryoneMadeTheStep(gameServer.getGameStatsPacket().getPlayersList());                checkIfSomeoneWon();                updateBars();            }        });        this.serverBattleFragment = serverBattleFragment;        getHolder().addCallback(this);    }    private void checkIfSomeoneWon() {        GameStatsPacket.GameSide wonSide = gameServer.getGameStatsPacket().checkIfSomeoneWon();        if(wonSide != null) {            String winText = wonSide == GameStatsPacket.GameSide.KIND ?                    "Победа за добрыми! ^-^" :                    "Победа за злыми! >:)";            showAlertDialogWithText(winText);            if(serverBattleFragment.getActivity() instanceof Notifiable) {                ((Notifiable)serverBattleFragment.getActivity())                        .notifyFragmentIsDone(serverBattleFragment);            }            gameServer.stopServerAndStreamString(winText);        }    }    private void showAlertDialogWithText(String text) {        ((Activity)context).runOnUiThread(() -> {            if(serverBattleFragment.getActivity() == null) return;            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(serverBattleFragment                    .getActivity(), R.style.TransparentDialog);            LayoutInflater inflater = LayoutInflater.from(context);            View customDialog = inflater.inflate(R.layout.alert_dialog_text, null);            alertDialogBuilder.setView(customDialog);            AlertDialog alertDialog = alertDialogBuilder.show();            TextView titleView = alertDialog.findViewById(R.id.message);            titleView.setText(text);        });    }    private void changePhaseIfEveryoneMadeTheStep(List<BattlePlayer> players) {        for (BattlePlayer player :                players) {            if (player.getAbilityToStep()) return;        }        // Notify the game thread to go on        if(gameThread != null) {            gameThread.skipStep();        }    }    @Override    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {        Log.d(TAG, "Surface created;");        drawThread = new BattleFieldDrawThread(surfaceHolder, getContext());        drawThread.start();        gameThread = new GameThread();        gameThread.start();    }    @Override    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {        Log.d(TAG, "Surface changed;");    }    @Override    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {        Log.d(TAG, "Surface destroyed;");        drawThread.interrupt();        gameThread.interrupt();        drawables.clear();        gameServer.stopServer();    }    private void updateIdleAnimationsOfPlayers(List<BattlePlayer> players) {        for (BattlePlayer player :                players) {            player.getAssignedBattleUnit(battleUnitContainer).updateIdleAnimation(context, player);        }    }    private void updateBars() {        if(serverBattleFragment.getActivity() != null) {            serverBattleFragment.getActivity().runOnUiThread(new Runnable() {                @Override                public void run() {                    serverBattleFragment.setBalanceHpBar(gameServer.getGameStatsPacket()                                    .getEvilPlayersHP(),                            gameServer.getGameStatsPacket().getKindPlayersHP());                }            });        }    }    /**     * This thread leads the step-by-step game process. Every side is given [30] seconds for a     * step and continues if everyone made their choice     */    private class GameThread extends Thread{        private final Handler handler = new Handler(Looper.getMainLooper());        private Runnable runnableCallbackUnfreeze;        public void skipStep() {            new Handler(Looper.getMainLooper()).postDelayed((Runnable) () -> {                if(runnableCallbackUnfreeze != null) {                    handler.removeCallbacks(runnableCallbackUnfreeze);                    runnableCallbackUnfreeze = null;                }                synchronized (gameThreadSyncObject) {                    gameThreadSyncObject.notify();                }            }, 3000);        }        @Override        public void run() {            while(!isInterrupted()) {                serverBattleFragment.showMessageOnDisplay("Ход \"добрых\", 30 сек.", 2000);                gameServer.getGameStatsPacket().setPhase(GameStatsPacket.GameSide.KIND);                gameServer.sendUpdatedGameStatsToEveryone();                unfreezeThreadInTime(30);                checkIfSomeoneWon();                synchronized(gameThreadSyncObject) {                    try {                        gameThreadSyncObject.wait();                    } catch (InterruptedException e) {/*Interrupted exception ignored*/}                }                serverBattleFragment.showMessageOnDisplay("Ход \"Злых\", 30 сек.", 2000);                gameServer.getGameStatsPacket().setPhase(GameStatsPacket.GameSide.EVIL);                gameServer.sendUpdatedGameStatsToEveryone();                unfreezeThreadInTime(30);                checkIfSomeoneWon();                synchronized(gameThreadSyncObject) {                    try {                        gameThreadSyncObject.wait();                    } catch (InterruptedException e) {/*Interrupted exception ignored*/}                }            }        }        private void unfreezeThreadInTime(int sec) {            runnableCallbackUnfreeze = () -> {                synchronized(gameThreadSyncObject) {                    gameThreadSyncObject.notify();                }            };            handler.postDelayed(runnableCallbackUnfreeze,                    sec * 1000L);        }    }    private class BattleFieldDrawThread extends Thread{        private final SurfaceHolder surfaceHolder;        private Paint paint;        private TextPaint textPaint;        private Context parentContext;        private static final int FPS = 10;        Calendar calendar;        private  long nextUpdateTime;        public BattleFieldDrawThread(SurfaceHolder surfaceHolder, Context parentContext) {            background = BitmapFactory.decodeResource(context.getResources(),                    R.drawable.green_background);            this.surfaceHolder = surfaceHolder;            this.parentContext = parentContext;            calendar = Calendar.getInstance();            nextUpdateTime = calendar.getTimeInMillis();            initTools();        }        private void initTools() {            paint = new Paint();            textPaint = new TextPaint();            textPaint.setTextSize(50);            textPaint.setTextAlign(Paint.Align.CENTER);            textPaint.setColor(Color.BLACK);            Typeface typeface = ResourcesCompat.getFont(context, R.font.gouranga_pixel);            textPaint.setTypeface(typeface);            paint.setColor(Color.GREEN);            paint.setStyle(Paint.Style.FILL);        }        @Override        public void run() {            // Creates field objects            Field.spawnFieldsAndCopy(gameServer.getGameStatsPacket().getEvilPlayersAmount()                    , gameServer.getGameStatsPacket().getKindPlayersAmount(),                    getWidth(), getHeight(), context, drawables);            // Gets battle unit for every battle player and assigns it to the field            BattlePlayer.assignAllBattleUnitsToFields(                    gameServer.getGameStatsPacket().getPlayersList(), context, battleUnitContainer,                    soundPlayer);            // Add all battle units to drawables            BattlePlayer.addAllBattleUnitsToDrawables(gameServer                    .getGameStatsPacket().getPlayersList(), drawables, battleUnitContainer);            // Send updated gamestats with assigned battleunits to everyone            gameServer.sendStartEventToEveryone();            while(!interrupted()){                if(nextUpdateTime - calendar.getTimeInMillis() > 0) {                    try {                        Thread.sleep(nextUpdateTime - calendar.getTimeInMillis());                    } catch(InterruptedException ex) {                        /*                        Interrupted exception will always be called when Thread.sleep() called                        so there's no need to handle it                         */                    }                }                nextUpdateTime = calendar.getTimeInMillis() + 1000/FPS;                Canvas canvas = surfaceHolder.lockCanvas();                if(canvas == null) continue;                if(getWidth() < getHeight()) { // In case the device in portrait mode                    serverBattleFragment.changeHUDVisibility(GONE);                    // Draw green background                    canvas.drawColor(Color.WHITE);                    canvas.drawText("Переверните устройство для того,", getWidth()/2f,                            getHeight()/2f, textPaint);                    canvas.drawText("чтобы продолжить отрисовку", getWidth()/2f ,                            getHeight()/2f + 50, textPaint);                } else { // Game drawing starts here                    serverBattleFragment.changeHUDVisibility(VISIBLE);                    // Draw green background                    canvas.drawBitmap(background, null, new Rect(0, 0, getWidth(),                            getHeight()), paint);                    // Draw all drawables                    drawables.drawAllDrawablesOnCanvas(canvas);            }                surfaceHolder.unlockCanvasAndPost(canvas);        }    }    }}