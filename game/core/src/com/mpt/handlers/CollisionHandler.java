package com.mpt.handlers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mpt.modules.MusicModule;
import com.mpt.objects.checkpoint.Checkpoint;
import com.mpt.objects.endpoint.Endpoint;
import com.mpt.objects.interactables.Coin;
import com.mpt.objects.interactables.KillBlock;
import com.mpt.objects.player.Player;
import com.mpt.platform.GameScreen;
import com.mpt.platform.LoadingScreen;

public class CollisionHandler implements ContactListener {

    PreferencesHandler preferencesHandler;
    GameScreen gameScreen;

    public CollisionHandler(PreferencesHandler preferencesHandler, GameScreen gameScreen) {
        this.preferencesHandler = preferencesHandler;
        this.gameScreen = gameScreen;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        checkCollision(fixtureA, fixtureB);
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    private void checkCollision(Fixture fixtureA, Fixture fixtureB) {
        if (fixtureA.getBody().getUserData() instanceof Player && fixtureB.getBody().getUserData() instanceof Checkpoint)
            setNewCheckpoint(fixtureA, fixtureB);
        if (fixtureB.getBody().getUserData() instanceof Player && fixtureA.getBody().getUserData() instanceof Checkpoint)
            setNewCheckpoint(fixtureB, fixtureA);
        if (fixtureA.getBody().getUserData() instanceof Player && fixtureB.getBody().getUserData() instanceof Coin)
            collectCoin(fixtureA, fixtureB);
        if (fixtureB.getBody().getUserData() instanceof Player && fixtureA.getBody().getUserData() instanceof Coin)
            collectCoin(fixtureB, fixtureA);
        if (fixtureA.getBody().getUserData() instanceof Player && fixtureB.getBody().getUserData() instanceof KillBlock)
            collisionKillBlock(fixtureA);
        if (fixtureB.getBody().getUserData() instanceof Player && fixtureA.getBody().getUserData() instanceof KillBlock)
            collisionKillBlock(fixtureB);
        if (fixtureA.getBody().getUserData() instanceof Player && fixtureB.getBody().getUserData() instanceof Endpoint)
            endLevel(fixtureA, fixtureB);
        if (fixtureB.getBody().getUserData() instanceof Player && fixtureA.getBody().getUserData() instanceof Endpoint)
            endLevel(fixtureB, fixtureA);
    }

    private void setNewCheckpoint(Fixture fixtureA, Fixture fixtureB) {
        Player player = (Player) fixtureA.getBody().getUserData();
        Checkpoint checkpoint = (Checkpoint) fixtureB.getBody().getUserData();
        Vector2 checkpointPosition = new Vector2(checkpoint.getBody().getPosition().x - 2f, checkpoint.getBody().getPosition().y);
        if (!player.getRespawnPosition().equals(checkpointPosition) && !checkpoint.isCheckpointClaimed()) {
            player.setRespawnPosition(checkpointPosition);
            checkpoint.setCheckpointClaimed();
            preferencesHandler.setRespawnPosition(checkpointPosition);
        }
    }

    private void collectCoin(Fixture fixtureA, Fixture fixtureB) {
        Player player = (Player) fixtureA.getBody().getUserData();
        Coin coin = (Coin) fixtureB.getBody().getUserData();
        if (!coin.getIsCollected()) {
            coin.setIsCollected(true);
            MusicModule.getCollectCoinSound().play();
            player.setCollectedCoins(player.getCollectedCoins() + 1);
        }
    }

    private void collisionKillBlock(Fixture fixtureA) {
        Player player = (Player) fixtureA.getBody().getUserData();
        player.setPlayerState(Player.State.DYING);
        player.getPlayerAnimations().setCurrent("death");

    }

    private void endLevel(Fixture fixtureA, Fixture fixtureB) {
        Player player = (Player) fixtureA.getBody().getUserData();
        Endpoint endpoint = (Endpoint) fixtureB.getBody().getUserData();
        ((Game) Gdx.app.getApplicationListener()).setScreen(new LoadingScreen());
    }

}
