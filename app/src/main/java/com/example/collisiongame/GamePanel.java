package com.example.collisiongame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{

    private RectPlayer player;
    private Point playerPoint;
    private MainThread thread;
    private boolean movingPlayer = false;
    private boolean gameOver = false;
    private long gameOverTime;

    private Rect r = new Rect();

    private ObstacleManager obstacleManager;

        public GamePanel(Context context) {
            super(context);

            getHolder().addCallback(this);

            thread = new MainThread(getHolder(), this);

            player = new RectPlayer(new Rect(100, 100,200,200), Color.rgb(255,0,0));
            playerPoint= new Point(Constants.SCREEN_WIDTH/2,3*Constants.SCREEN_HEIGHT/4 );
            player.update(playerPoint);
            obstacleManager = new ObstacleManager(200,350, 75, Color.BLACK);
            setFocusable(true);

        }

        public void reset() {

            playerPoint= new Point(Constants.SCREEN_WIDTH/2,3*Constants.SCREEN_HEIGHT/4 );
            player.update(playerPoint);
            obstacleManager = new ObstacleManager(200,350,75,Color.BLACK);
            movingPlayer = false;
        }


        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

        }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        thread = new MainThread(getHolder(), this);

        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        boolean retry = true;
        while(true){
            try {
                thread.setRunning(true);
                thread.join();
            }catch(Exception e ) {e.printStackTrace();}
            retry= false;
        }
    }

    public boolean onTouchEvent(MotionEvent event){
            //return super.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!gameOver && player.getRectangle().contains((int)event.getX(), (int)event.getY()))
                    movingPlayer = true;
                if (gameOver && System.currentTimeMillis() - gameOverTime >= 200){
                    reset();
                    gameOver= false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!gameOver && movingPlayer)
                playerPoint.set((int)event.getX(),(int) event.getY());
            break;
            case MotionEvent.ACTION_UP:
                movingPlayer= false;
                break;
        }
        return true;
    }

    public void update() {
            if (!gameOver) {
                player.update(playerPoint);
                obstacleManager.update();
                if (obstacleManager.playerCollide(player)){
                    gameOver = true;
                    gameOverTime = System.currentTimeMillis();
                }
            }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawColor(Color.WHITE);
        player.draw(canvas);

        obstacleManager.draw(canvas);

        if (gameOver){
            Paint paint = new Paint();
            paint.setTextSize(100);
            paint.setColor(Color.MAGENTA);
            drawCenterText(canvas, paint, "GAME OVER");
        }
    }
    private void drawCenterText(Canvas canvas, Paint paint, String text) {
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, paint);
    }
}


