package com.mpt.handlers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.mpt.objects.interactableObjects.Box;
import com.mpt.objects.checkpoint.Checkpoint;
import com.mpt.objects.enemy.Centipede;
import com.mpt.objects.player.Player;
import com.mpt.platform.GameScreen;

import static com.mpt.constants.Constants.PPM;

public class MapHandler {
    private TiledMap tiledMap;
    private GameScreen gameScreen;

    public MapHandler(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public OrthogonalBleedingHandler setup(float unitScale, SpriteBatch batch, String mapName) {
        tiledMap = new TmxMapLoader().load("maps/" + mapName + "/Platform.tmx");
        parseMapObjects(tiledMap.getLayers().get("Objects").getObjects());
        return new OrthogonalBleedingHandler(tiledMap, unitScale, batch);
    }

    private void parseMapObjects(MapObjects mapObjects) {
        
        if(mapObjects.get("Spawnpoint") != null && mapObjects.get("Spawnpoint") instanceof RectangleMapObject) {
            Rectangle rectangle = (((RectangleMapObject) mapObjects.get("Spawnpoint")).getRectangle());
            gameScreen.getPreferencesHandler().setDefaultSpawn(new Vector2((rectangle.getX() + rectangle.getWidth() / 2) /  PPM, (rectangle.getY() + rectangle.getHeight() / 2) / PPM));
        }

        for(MapObject mapObject : mapObjects) {
            if(mapObject instanceof PolygonMapObject)
                createStaticObject((PolygonMapObject) mapObject);
            if(mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                String rectangleName;
                if(mapObject.getName() != null)
                    rectangleName = mapObject.getName();
                else
                    throw new NullPointerException("There is a rectangle object with a null name.");

                if(rectangleName.equals("Player")) {
                    Body body = BodyHandler.createBody(
                            rectangle.getX() + rectangle.getWidth() / 2,
                            rectangle.getY() + rectangle.getHeight() / 2,
                            rectangle.getWidth(),
                            rectangle.getHeight(),
                            false,
                            false,
                            0f,
                            0f,
                            gameScreen.getWorld()
                    );
                    gameScreen.setPlayer(new Player(rectangle.getWidth(), rectangle.getHeight(), body));
                    body.setTransform(gameScreen.getPreferencesHandler().getRespawnPosition(), body.getAngle());
                }
                if(rectangleName.equals("Centipede")) {
                    Body body = BodyHandler.createBody(
                            rectangle.getX() + rectangle.getWidth() / 2,
                            rectangle.getY() + rectangle.getHeight() / 2,
                            rectangle.getWidth(),
                            rectangle.getHeight(),
                            false,
                            false,
                            0f,
                            0f,
                            gameScreen.getWorld()
                    );
                    gameScreen.addEnemy("Centipede", new Centipede(rectangle.getWidth(), rectangle.getHeight(), body, gameScreen));
                }
                if(rectangleName.equals("Checkpoint")) {
                    Body body = BodyHandler.createBody(
                            rectangle.getX() + rectangle.getWidth() / 2,
                            rectangle.getY() + rectangle.getHeight() / 2,
                            rectangle.getWidth(),
                            rectangle.getHeight(),
                            true,
                            true,
                            0f,
                            0f,
                            gameScreen.getWorld()
                    );
                    gameScreen.addCheckpoint(new Checkpoint(rectangle.getWidth(), rectangle.getHeight(), body));
                }
                if(rectangleName.equals("Box")) {
                    Body body = BodyHandler.createBody(
                            rectangle.getX() + rectangle.getWidth() / 2,
                            rectangle.getY() + rectangle.getHeight() / 2,
                            rectangle.getWidth(),
                            rectangle.getHeight(),
                            false,
                            false,
                            1000,
                            0f,
                            gameScreen.getWorld()
                    );
                    gameScreen.addBox(new Box(rectangle.getWidth(), rectangle.getHeight(), body));
                }
            }
        }
    }

    private void createStaticObject(PolygonMapObject polygonMapObject) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = gameScreen.getWorld().createBody(bodyDef);
        Shape shape = createPolygonShape(polygonMapObject);
        body.createFixture(shape, 1000);
        shape.dispose();
    }

    private Shape createPolygonShape(PolygonMapObject polygonMapObject) {
        float[] vertices = polygonMapObject.getPolygon().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for(int i = 0; i < vertices.length / 2; i++) {
            Vector2 currentVector = new Vector2(vertices[i * 2] / PPM, vertices[(i * 2) + 1]/PPM);
            worldVertices[i] = currentVector;
        }

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(worldVertices);
        return polygonShape;
    }

}