package entity;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager;

import java.awt.*;

public class Item extends Entity{


    // 아이템떨어지는 속도.
    private int speed;
    // 보너스 포인트 아이템의 보너스 점수값
    private int pointValue;

    /** Cooldown between sprite changes. */
    // 이부분을활용하여 떨어질떄 애니메이션을 줄수있음. EnemyShip.java, update부분보면됨.
    //private Cooldown animationCooldown;

    /**
     * Constructor, establishes the entity's generic properties.
     *
     * @param positionX Initial position of the entity in the X axis.
     * @param positionY Initial position of the entity in the Y axis.
     *
     */
    public Item(int positionX, int positionY, DrawManager.SpriteType spriteType) {
        super(positionX, positionY, 16 * 2, 16 * 2, Color.BLUE);
        this.spriteType = spriteType;
        this.speed = 2;
        switch (this.spriteType) {
            case BonusScoreItem1:
                this.pointValue = 50;
                this.speed = 2;
                break;
            case BonusScoreItem2:
                this.pointValue = 100;
                this.speed = 4;
                break;
            case BonusScoreItem3:
                this.pointValue = 300;
                this.speed = 6;
                break;
            default:
                this.pointValue = 0;
                break;
        }
    }

    public final void update() {
        this.positionY += this.speed;
    }
    public int getSpeed(){
        return this.speed;
    }
    public int getPointValue(){
        return this.pointValue;
    }
}
