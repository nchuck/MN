package com.itc.mn.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.itc.mn.Misc.Const;

/**
 * Created by zero_ on 10/09/2015.
 */
public class ExampleScreen implements Screen {

    private OrthographicCamera camera;
    private Stage stage;
    private Game game;
    private FitViewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Sprite sprite;
    private double[][] valores;

    public ExampleScreen(Game game, double[][] valores){
        // S�lo para tener una referencia al manejador de pantallas
        this.game = game;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        this.valores = valores;
        // Definimos el viewport
        viewport = new FitViewport(Const.WIDTH, Const.HEIGHT);
        // Creamos las cosas para renderizar
        camera = new OrthographicCamera(Const.WIDTH, Const.HEIGHT);
        camera.setToOrtho(false);
        camera.position.set(0, 0, 0);
        camera.update();
        // Definimos el Stage para entradas t�ctiles
        stage = new Stage();
        stage.setViewport(viewport);
        // Test
        sprite = new Sprite(new Texture(Gdx.files.internal("badlogic.jpg")));
        sprite.setPosition(0, 0);
        // For moving
        Gdx.input.setInputProcessor(stage);
        stage.addListener(new ActorGestureListener(){
            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                super.pan(event, x, y, deltaX, deltaY);
                camera.position.set(camera.position.x - deltaX, camera.position.y - deltaY, 0);
            }
        });
        stage.addListener(new InputListener(){
            @Override
            public boolean keyTyped(InputEvent event, char character) {
                if(character == Input.Keys.PLUS) {
                    camera.zoom += 1;
                    System.out.println("Zoom!");
                    return true;
                }
                else{
                    if(character == Input.Keys.MINUS){
                        camera.zoom -= 1;
                        return true;
                    }
                }
                System.out.println(character);
                return super.keyTyped(event, character);
            }
        });
    }

    private void renderArreglo(){
        // Para que se renderize con la c�mara
        shapeRenderer.setProjectionMatrix(camera.combined);
        // Para comenzar el renderizado
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // Obviamente define el color
        shapeRenderer.setColor(Color.BLUE);
        // Procesando arreglo
        for (int i = 0; i < valores.length; i++)
            shapeRenderer.circle((float) valores[i][0], (float) valores[i][1], 0.5f);
        // Para finalizar el renderizado
        shapeRenderer.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Actualizamos la c�mara
        camera.update();
        // Batch
        batch.setProjectionMatrix(camera.combined);
        // Actualizamos el Stage
        stage.act();
        stage.draw();
        // Renderizamos con el shapeRenderer
        renderArreglo();

        // Renderizamos los ejes X e Y
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        shapeRenderer.line(0, -camera.viewportHeight + camera.position.y , 0, camera.viewportHeight + camera.position.y);
        shapeRenderer.line(-camera.viewportWidth + camera.position.x, 0, camera.viewportWidth + camera.position.x, 0);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
