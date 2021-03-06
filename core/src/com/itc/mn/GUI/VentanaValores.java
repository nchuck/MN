package com.itc.mn.GUI;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.itc.mn.Methods.*;
import com.itc.mn.Screens.RenderScreen;
import com.itc.mn.Things.FuncionX;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * This class generates the windows for the input
 */
public class VentanaValores extends VisWindow {

    private final Game game;
    private VisTextButton aceptar, cancelar;
    private FuncionX fx;
    private Method.Tipo tipo;
    private I18NBundle bundle;

    /**
     * Crea una v con los campos mandados.
     * Cada parte del arreglo tendra que ser de 2, el primero para el hint de la caja, el 2do para el nombre
     * a usar al momento de recuperar valores
     * @param title Titulo de la v
     * @param campos String[] de 2, [0] = hint, [1] = nombre variable
     */
    public VentanaValores(String title, String[][] campos, Game game, Method.Tipo tipo, I18NBundle bundle) {
        super(title);
        setName(title); // La ventana se llamara igual que el titulo que reciba
        this.game = game; // Una referencia a Game para poder intercambiar la pantalla
        this.tipo = tipo; // Guardamos referencia al tipo
        this.bundle = bundle; // Save a reference for the i18n bundle
        for(String[] campo: campos){
            VisTextField tmp = new VisTextField();
            tmp.setMessageText(campo[0]);
            tmp.setName(campo[1]);
            add(tmp).expandX().center().pad(1f).colspan(2).row();
            tmp.addListener(new AndroidInput(tmp));
        }
        // Creamos los botones
        aceptar = new VisTextButton(bundle.get("accept"));
        cancelar = new VisTextButton(bundle.get("close"));
        // Los agregamos a la v
        add(cancelar).expandX().pad(3f);
        add(aceptar).expandX().pad(3f).row();
        // Agregamos accion basica al boton cancelar
        cancelar.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
            }
        });
        // Asignamos una cadena vacia a los nombres de los actores que no son indispensables
        renombra();
        validacion(tipo);
        closeOnEscape();
        addCloseButton();
        pack();
        setPosition((Gdx.graphics.getWidth()-getWidth())/2f, (Gdx.graphics.getHeight()-getHeight())/2f);
    }

    /**
     * Asigna una cadena vacia a los nombres de los actores para evitar checar si es nulo o no
     */
    private void renombra(){
        for(Actor actor: getChildren())
            if(actor.getName() == null)
                actor.setName("");
    }

    private void validacion(Method.Tipo tipo){
        restringeEP();
        switch (tipo){
            case PUNTO_FIJO:
                break;
            case BISECCION: // Para validar que haya cambio de signo entre los puntos dados
                break;
            case NEWTON_RAPHSON:
                break;
            case REGLA_FALSA:
                break;
            case SECANTE:
                break;
        }
    }

    /**
     * Habilita una restriccion en el campo Error, para que sea de 0 a 100
     */
    private void restringeEP(){
        for(Actor textfield: getChildren())
            if (textfield.getName().equals("ep"))
                textfield.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (!((VisTextField) actor).getText().equals("")) {
                            try{
                                double value = Double.parseDouble(((VisTextField) actor).getText());
                                actor.setColor((value > 0 && value < 100) ? Color.GREEN: Color.RED);
                            }
                            catch (Exception ex){
                                actor.setColor(Color.RED);
                            }
                        }
                        else
                            actor.setColor(1, 1, 1, 1);
                    }
                });
    }

    /**
     * Regresa el valor contenido en alguno de los campos/variables declaradas, por defecto TextArea
     * @param variable Nombre de la variable asignado en su creacion
     * @return the current variable in use
     * @throws Exception No se encuentra la variable solicitada
     */
    public String getVariable(String variable) throws Exception{
        for(Actor textField: getChildren())
            if (textField.getName() != null)
                if (textField.getName().equals(variable))
                    return ((VisTextField)textField).getText();
        throw new Exception(bundle.get("undefined_variable"));
    }

    /**
     * Regresa un actor con un nombre determinado
     * @param nombre Nombre del actor a recuperar
     * @return Actor
     */
    public Actor getActor(String nombre){
        for(Actor actor: getChildren())
            if(actor.getName().equals(nombre))
                return actor;
        return null;
    }

    /**
     * Parpadea la ventana, para llamar la atencion
     */
    public void parpadear(){
        addAction(Actions.sequence(Actions.alpha(0.5f, 0.05f), Actions.alpha(1, 0.05f), Actions.alpha(0.5f, 0.05f), Actions.alpha(1, 0.05f)));
    }

    /**
     * Asigna un evento personalizado dependiendo del tipo de metodo a ejecutar
     * @param tipo Tipo de Method
     */
    public void asignaEvento(Method.Tipo tipo){
        aceptar.addListener(new Proceso(this, tipo));
    }

    public Method.Tipo getTipo() {
        return tipo;
    }

    private class Proceso extends ClickListener {

        private final Method.Tipo tipo;
        private final VentanaValores v;
        private double a, b;

        public Proceso(VentanaValores v, Method.Tipo tipo){
            this.v = v;
            this.tipo = tipo;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            try {
                if (Double.parseDouble(v.getVariable("ep")) > 0 && Double.parseDouble(v.getVariable("ep")) <= 100)
                    switch (tipo) {
                        case BISECCION:
                            //Para corroborar que haya cambio de signo entre los valores dados
                            fx = new FuncionX(v.getVariable("f"));
                            a = fx.obtenerValor(Double.parseDouble(v.getVariable("a")));
                            b = fx.obtenerValor(Double.parseDouble(v.getVariable("b")));
                            if((a*b) < 0) {
                                game.setScreen(new RenderScreen(game, new Bisection(v.getVariable("f"), Double.parseDouble(v.getVariable("a")), Double.parseDouble(v.getVariable("b")), Double.parseDouble(v.getVariable("ep") + "d") / 100d), false));
                            }
                            else{
                                getActor("a").setColor(1, 0, 0, 1);
                                getActor("b").setColor(1, 0, 0, 1);
                            }
                            break;
                        case REGLA_FALSA:
                            fx = new FuncionX(v.getVariable("f"));
                            a = fx.obtenerValor(Double.parseDouble(v.getVariable("a")));
                            b = fx.obtenerValor(Double.parseDouble(v.getVariable("b")));
                            if((a*b) < 0) {
                                game.getScreen().dispose();
                                game.setScreen(new RenderScreen(game, new ReglaFalsa(v.getVariable("f"), Double.parseDouble(v.getVariable("a")), Double.parseDouble(v.getVariable("b")), Double.parseDouble(v.getVariable("ep") + "d") / 100d), false));
                            }else{
                                getActor("a").setColor(1, 0, 0, 1);
                                getActor("b").setColor(1, 0, 0, 1);
                            }
                            break;
                        case PUNTO_FIJO:
                            game.getScreen().dispose();
                            game.setScreen(new RenderScreen(game, new PFijo(v.getVariable("f1"), v.getVariable("f2"), Double.parseDouble(v.getVariable("vi")), Double.parseDouble(v.getVariable("ep") + "d") / 100d), false));
                            break;
                        case NEWTON_RAPHSON:
                            game.getScreen().dispose();
                            game.setScreen(new RenderScreen(game, new NewtonRaphson(v.getVariable("fx"), v.getVariable("f'x"), Double.parseDouble(v.getVariable("vi")), Double.parseDouble(v.getVariable("ep") + "d") / 100d), false));
                            break;
                        case SECANTE:
                            game.getScreen().dispose();
                            game.setScreen(new RenderScreen(game, new Secante(v.getVariable("fx"), Double.parseDouble(v.getVariable("xi_1") + "d"), Double.parseDouble(v.getVariable("xi") + "d"), Double.parseDouble(v.getVariable("ep") + "d") / 100d), false));
                            break;
                    }
            } catch (Exception ex) {
                ex.printStackTrace();
                v.fadeOut();
            }
        }
    }

    /**
     * This class handles the native android dialog input to avoid VisUI input lag on Android
     */
    private class AndroidInput extends ClickListener implements Input.TextInputListener{

        private VisTextField field;

        public AndroidInput(VisTextField field){
            this.field = field;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            if(Gdx.app.getType().equals(Application.ApplicationType.Android))
                Gdx.input.getTextInput(new AndroidInput(field), field.getName(), field.getText(), field.getMessageText());
        }

        @Override
        public void input(String text) {
            field.setText(text);
        }

        @Override
        public void canceled() {

        }
    }
}
