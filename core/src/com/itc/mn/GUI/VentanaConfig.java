package com.itc.mn.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.itc.mn.Cosas.Const;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;

/**
 * This is the configuration window, it will manage all the basic settings of the app.
 */
public class VentanaConfig extends VisWindow {

    private VisSlider decimales;
    private VisLabel l_decimales, l_decimales_valor, l_bgcolor, l_sgcolor, l_axiscolor;
    private VisTextButton aceptar, cancelar, bg_cambiar, sg_cambiar, axis_cambiar;
    private Json json = new Json();
    private Preferences prefReader = Gdx.app.getPreferences(Const.pref_name); // Load preferences from file
    private String generalPrefs;
    private ColorPicker picker;

    public VentanaConfig(FrontEnd gui) {
        super("Ajustes");
        // Default config
        closeOnEscape();
        addCloseButton();
        generalPrefs = prefReader.getString(Const.id); // Retrieve the general preferences, that is a stored object in json format
        Const prefs = json.fromJson(Const.class, generalPrefs); // Create the object instance
        construct(prefs, gui);
        pack();
    }

    private VisWindow getWindow(){ return this; }

    private void construct(final Const prefs, final FrontEnd gui){
        // Load the decimal amount
        String dec = prefs.getFormat().substring(prefs.getFormat().indexOf('.')+1);
        // Initialize tags
        l_decimales = new VisLabel("Decimales");
        l_bgcolor = new VisLabel("Color fondo:");
        l_sgcolor = new VisLabel("Color grafica:");
        l_axiscolor = new VisLabel("Color ejes:");
        decimales = new VisSlider(1, 16, 1, false);
        // Set the current value
        decimales.setValue(dec.length());
        l_decimales_valor = new VisLabel(String.valueOf((int)decimales.getValue()));
        // Buttons
        bg_cambiar = new VisTextButton("Cambiar...");
        sg_cambiar = new VisTextButton("Cambiar...");
        axis_cambiar = new VisTextButton("Cambiar...");
        bg_cambiar.setColor(prefs.backgroundColor[0], prefs.backgroundColor[1], prefs.backgroundColor[2], prefs.backgroundColor[3]);
        sg_cambiar.setColor(prefs.singleGraphic);
        axis_cambiar.setColor(prefs.axisColor);
        aceptar = new VisTextButton("Aceptar");
        cancelar = new VisTextButton("Cancelar");
        // Adjust listeners
        decimales.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                l_decimales_valor.setText(String.valueOf((int)decimales.getValue()));
            }
        });
        aceptar.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String format = "#.";
                for(int i = 0; i < decimales.getValue(); i++)
                    format+="#";
                prefs.setFormat(format);
                updatePrefs(prefs);
                gui.reloadConfig();
                fadeOut();
            }
        });
        cancelar.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getWindow().fadeOut();
            }
        });
        bg_cambiar.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                picker = new ColorPicker(new ColorPickerAdapter(){
                    @Override
                    public void finished(Color newColor) {
                        prefs.setBgColor(newColor.r, newColor.g, newColor.b, newColor.a);
                        bg_cambiar.setColor(newColor);
                        saveAndReload(prefs, gui);
                    }
                });
                getParent().addActor(picker.fadeIn());
            }
        });
        sg_cambiar.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                picker = new ColorPicker(new ColorPickerAdapter(){
                    @Override
                    public void finished(Color newColor) {
                        prefs.setSGColor(newColor);
                        sg_cambiar.setColor(newColor);
                        saveAndReload(prefs, gui);
                    }
                });
                getParent().addActor(picker.fadeIn());
            }
        });
        axis_cambiar.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                picker = new ColorPicker(new ColorPickerAdapter(){
                    @Override
                    public void finished(Color newColor) {
                        prefs.setAxisColor(newColor);
                        axis_cambiar.setColor(newColor);
                        saveAndReload(prefs, gui);
                    }
                });
                getParent().addActor(picker.fadeIn());
            }
        });
        // Adding to Window
        add(l_decimales).pad(3f).left();
        add(decimales).pad(3f).left().expandX();
        add(l_decimales_valor).pad(5f).left().row();
        add(l_bgcolor).pad(3f).left();
        add(bg_cambiar).pad(3f).row();
        add(l_sgcolor).pad(3f).left();
        add(sg_cambiar).pad(3f).row();
        add(l_axiscolor).pad(3f).left();
        add(axis_cambiar).pad(3f).row();
        add(cancelar).right().pad(1f);
        add(aceptar).right().pad(3f).row();
    }

    private void saveAndReload(Const prefs, FrontEnd gui){
        updatePrefs(prefs);
        gui.reloadConfig();
    }

    public void updatePrefs(Const prefs){
        String newPrefs = json.prettyPrint(prefs);
        if(!newPrefs.equals(generalPrefs)){
            prefReader.putString(Const.id, newPrefs);
            prefReader.flush();
        }
    }

    @Override
    public VisWindow fadeIn() {
        setPosition((Gdx.graphics.getWidth()-getWidth())/2f, (Gdx.graphics.getHeight()-getHeight())/2f);
        return super.fadeIn();
    }
}