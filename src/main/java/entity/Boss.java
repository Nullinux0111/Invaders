package entity;

import engine.Cooldown;
import engine.DrawManager;
import screen.Screen;

import java.awt.*;
import java.util.*;

public class Boss extends Entity {


    private int live = 10;

    private static final int POINTVALUE = 300;

    /** Initial position in the x-axis. */
    private static final int INIT_POS_X = 265;
    /** Initial position in the x-axis. */
    private static final int INIT_POS_Y = 200;
    /** Time between shots. */
    private int shootingInterval = 1200;
    /** Speed of the bullets shot by the members. */
    private static final int BULLET_SPEED = 4;
    /** Margin on the sides of the screen. */
    private static final int SIDE_MARGIN = 20;
    /** Margin on the bottom of the screen. */
    private static final int BOTTOM_MARGIN = 450;
    /** Margin on the top of the screen. */
    private static final int TOP_MARGIN = 150;
    /** Lateral speed of the formation. */
    private static final int X_SPEED = 2;
    /** Downwards speed of the formation. */
    private static final int Y_SPEED = 1;


    /** Checks if the ship has been hit by a bullet. */
    private boolean isDestroyed;
    /** Cooldown between sprite changes. */
    private Cooldown animationCooldown;
    /** Minimum time between shots. */
    private Cooldown shootingCooldown;
    /** Interval between movements, in frames. */
    private int movementInterval;
    /** Current direction the formation is moving on. */
    private Direction currentDirection;

    /** Screen to draw ships on. */
    private Screen screen;

    /** Directions the formation can move. */
    private enum Direction {
        /** Movement to the right side of the screen. */
        RIGHT,
        RIGHT_DOWN,
        RIGHT_UP,
        /** Movement to the left side of the screen. */
        LEFT,
        LEFT_DOWN,
        LEFT_UP,
        /** Movement to the bottom of the screen. */
        DOWN,
        /** Movement to the top of the screen. */
        UP

    };


    /**
     * Constructor, establishes the boss generic properties.

     */
    public Boss() {
        super(INIT_POS_X,INIT_POS_Y, 50 *2 , 50 * 2, Color.white);
        this.movementInterval = 0;
        this.spriteType = DrawManager.SpriteType.Boss1;
        this.animationCooldown = Cooldown.getCooldown(500);
        this.isDestroyed = false;
        this.currentDirection = Direction.UP;

    }

    /**
     * Associates the formation to a given screen.
     *
     * @param newScreen
     *            Screen to attach.
     */
    public final void attach(final Screen newScreen) {
        screen = newScreen;
    }


    /**
     * Updates the position of the ships.
     */
    public final void update(){
        if(this.animationCooldown.checkFinished()){
            this.animationCooldown.reset();
            switch (this.spriteType) {
                case Boss1:
                    this.spriteType = DrawManager.SpriteType.Boss2;
                    break;
                case Boss2:
                    this.spriteType = DrawManager.SpriteType.Boss3;
                    break;
                case Boss3:
                    this.spriteType = DrawManager.SpriteType.Boss4;
                    break;
                case Boss4:
                    this.spriteType = DrawManager.SpriteType.Boss1;
                    break;
                default:
                    break;
            }
        }
        if(this.shootingCooldown == null) {
            this.shootingCooldown = Cooldown.getCooldown(shootingInterval);
            this.shootingCooldown.reset();
        }

        movementInterval++;
        if(movementInterval >= 50) {
            movementInterval = 0;

            switch (new Random().nextInt(8)) {
                case 0: // LEFT_DOWN
                    currentDirection =  Direction.LEFT_DOWN;
                    break;
                case 1: // RIGHT_DOWN
                    currentDirection = Direction.RIGHT_DOWN;
                    break;
                case 2: // RIGHT_UP
                    currentDirection = Direction.RIGHT_UP;
                    break;
                case 3: // LEFT
                    currentDirection =  Direction.LEFT;
                    break;
                case 4: //RIGHT
                    currentDirection = Direction.RIGHT;
                    break;
                case 5: // LEFT_UP
                    currentDirection =Direction.LEFT_UP;
                    break;
                case 6: // UP
                    currentDirection = Direction.UP;
                    break;
                case 7: // DOWN
                    currentDirection = Direction.DOWN;
                    break;


            }

        }

        if (currentDirection == Direction.RIGHT) {
            positionX += X_SPEED;
        } else if (currentDirection == Direction.RIGHT_DOWN) {
            positionX += X_SPEED;
            positionY += Y_SPEED;
        } else if (currentDirection == Direction.RIGHT_UP) {
            positionX += X_SPEED;
            positionY += -Y_SPEED;
        } else if (currentDirection == Direction.LEFT) {
            positionX += -X_SPEED;
        } else if (currentDirection == Direction.LEFT_DOWN) {
            positionX += -X_SPEED;
            positionY += Y_SPEED;
        } else if (currentDirection == Direction.LEFT_UP) {
            positionX += -X_SPEED;
            positionY += -Y_SPEED;
        } else if (currentDirection == Direction.DOWN) {
            positionY += Y_SPEED;
        } else if (currentDirection == Direction.UP) {
            positionY += -Y_SPEED;
        }

        if(this.getPositionY()+ this.getHeight() >= this.screen.getHeight()-BOTTOM_MARGIN){
            positionY += -Y_SPEED;
        }
        if(this.getPositionY() <= TOP_MARGIN){
            positionY += Y_SPEED;
        }
        if(this.getPositionX()+ this.getWidth() >= this.screen.getWidth() - SIDE_MARGIN){
            positionX += -X_SPEED;
        }
        if(this.getPositionX() <= SIDE_MARGIN){
            positionX += X_SPEED ;
        }
    }

    /**
     * targeting shoot
     * @param bullets
     *                 Bullets set to add the bullet being shot.
     * @param target
     *                 target
     */
    public final void targetingShoot(final Set<Bullet> bullets, final Entity target) {
        if (this.shootingCooldown.checkFinished()) {
            this.shootingCooldown.reset();

            int difX = target.getPositionX() + target.width / 2 - this.getPositionX() - this.width / 2;
            int difY = target.getPositionY()-this.getPositionY();
            int divideNum = 200;
            if(difX>200) divideNum = (int) (difX*0.95);
            else if(difX<-200) divideNum = (int) (difX* -0.95);
            bullets.add(BulletPool.getBullet(this.getPositionX() +this.width / 2,
                    this.getPositionY(), difX*BULLET_SPEED/divideNum, difY*BULLET_SPEED/divideNum));
        }
    }
    /**
     * pinwhellShoot
     * @param bullets
     *               Bullets set to add the bullet being shot.
     */
    public final void pinwheelShoot(final Set<Bullet> bullets){

        if (this.shootingCooldown.checkFinished()) {
            this.shootingCooldown.reset();
            for (int j = 0; j>=-2; j--) {
                for (int i = 30; i <= 150; i = i + 15) {
                    bullets.add(BulletPool.getBullet(this.getPositionX() + 40
                                    + this.getWidth() / 2 + this.X_SPEED, this.getPositionY() + + this.getHeight()/2,
                            (int) Math.round((BULLET_SPEED+j) * Math.cos(Math.toRadians(i))),
                            (int) Math.round((BULLET_SPEED+j) * Math.sin(Math.toRadians(i)))));
                }
            }
        }
    }
    /**
     * @param bullets
     *               Bullets set to add the bullet being shot.
     */
    public final void randomShoot(final Set<Bullet> bullets){

        int[] xlist = {-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5};
        HashSet<Integer> speedX = new HashSet<>();

        if (this.shootingCooldown.checkFinished()) {
            this.shootingCooldown.reset();
            for (int i = 0; i<=6; i++) {
                speedX.add(xlist[new Random().nextInt(8)]);
                while(speedX.size()!=i+1) speedX.add(xlist[new Random().nextInt(8)]);
            }

            for(Iterator i = speedX.iterator(); i.hasNext();){
                bullets.add(BulletPool.getBullet(this.getPositionX() + 40
                        + this.getWidth() / 2, this.getPositionY()+this.getHeight()/2, Integer.parseInt(i.next().toString()), BULLET_SPEED));
            }
        }
    }


    /**
     * Destroys the ship, causing an explosion.
     */
    public final void destroy() {
        this.live--;
        if( this.live == 5){
            this.spriteType = DrawManager.SpriteType.BossHpLow1;
        }
        else if(this.live == 2){
            this.spriteType = DrawManager.SpriteType.BossHpLow2;
        }
        else if (this.live == 0){
            this.isDestroyed = true;
            this.spriteType = DrawManager.SpriteType.BossDestroyed;
        }
    }

    /**
     * Getter for the score bonus if this boss is destroyed.
     *
     * @return Value of the boss.
     */
    public final int getPointValue() {
        return this.POINTVALUE;
    }
    /**
     * Getter for the score bonus if this boss is destroyed.
     *
     * @return Value of the boss.
     */
    public final boolean isDestroyed() {
        return this.isDestroyed;
    }

}
