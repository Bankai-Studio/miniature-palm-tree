package com.mpt.objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import static com.mpt.constants.Constants.PPM;

public class Player extends GameEntity{

    private int jumpCounter;
    private boolean running;
    public boolean playerDead = false;
    private int damageToEnemy; //danni al nemico
    private int minDmg = 50;  //50 di attacco
    private int maxDmg = 150; //150 di attacco
    private int player_health;
    private int damageValue;
    private final int MAXSTM = 100;
    private int stamina = MAXSTM;
    private final int STMXJ = 10;
    private final float TSTM = 0.02f;
    private float timerStm = 0f;
    private boolean doubleJumpReady = true;
    private float timerDoubleJump = 0f;
    private final float TDJMP = 2f;

    public Player(float width, float height, Body body) {
        super(width, height, body);
        this.speed = 10f;
        this.jumpCounter = 0;
        player_health = 3; //3 vite all'inizio del livello
        damageToEnemy = (int)(Math.random()*(maxDmg-minDmg+1)+minDmg);
    }

    public void attackEnemy(Enemy enemy){
        damageValue = damageToEnemy;
        enemy.getDamaged(damageValue);
    }

    public void playerGetDamaged(int damageV){
        player_health -= damageV;
        if(health == 0)
            playerDead = true;
    }

    @Override
    public void update() {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        timerStm += Gdx.graphics.getDeltaTime();
        if((!running || body.getLinearVelocity().x == 0) && stamina < MAXSTM && timerStm > TSTM){
            stamina++;
            timerStm = 0f;
        }

        timerDoubleJump += Gdx.graphics.getDeltaTime();
        if(!doubleJumpReady && timerDoubleJump > TDJMP)
            doubleJumpReady = true;

        checkUserInput();
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    private void checkUserInput() {
        velX = 0;
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
            running = true;
        else
            running = false;
        if(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            velX = 1;
        if(Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))
            velX = -1;
        if((!running || body.getLinearVelocity().y != 0) && speed > 8f) speed -= 0.2f;
        if((running && body.getLinearVelocity().y == 0) && speed<= 14f && stamina > 0) speed += 0.4f;
        if(running && body.getLinearVelocity().x != 0 && body.getLinearVelocity().y == 0 && stamina > 0) stamina -= 1;
        if((Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) && stamina >= STMXJ) {
            if(jumpCounter == 0 || (jumpCounter == 1 && doubleJumpReady)){
                if(jumpCounter == 1 && doubleJumpReady){
                    doubleJumpReady = false;
                    timerDoubleJump = 0f;
                }
                float force = body.getMass() * 9;
                body.setLinearVelocity(body.getLinearVelocity().x, 0);
                body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
                jumpCounter++;
                stamina -= STMXJ;
            }
        }

        if(body.getLinearVelocity().y == 0)
            jumpCounter = 0;

        body.setLinearVelocity(velX * speed, body.getLinearVelocity().y < 25 ? body.getLinearVelocity().y : 25);
    }

}
