package com.itc.mn.Metodos;

import com.itc.mn.Cosas.FuncionX;

/**
 * Created by zero_ on 11/09/2015.
 */
public class Biseccion extends Metodo {

    private FuncionX fa, fb, fx;
    private float xr, xranterior;

    public Biseccion(String funcion, float a, float b, float ep){
        this.funcion = funcion;
        // Aqu� v_inicial y v_final fungen como a y b
        this.v_inicial = a;
        this.v_final = b;
        this.ep = ep;
        // Inicializamos las funciones
        fa = new FuncionX(funcion);
        fb = new FuncionX(funcion);
        fx = new FuncionX(funcion);
        //Creamos los encabezados para la tabla de iteraciones
        encabezados = new String[]{"Iteracion", "a", "b", "f(a)", "f(b)", "xr", "f(xr)", "ep"};
        calculaRaiz();
        ep_porcentual = String.valueOf(ep/100)+"%";
        // Creamos el titulo para la ventana
        titulo_ventana = "Biseccion | Funcion: "+funcion+"| Raiz: "+raiz+" | ep: "+ep_porcentual;
    }

    public void calculaRaiz(){
        xr = (v_inicial + v_final)/2;
        resultados.add(new double[]{contador, v_inicial, v_final, fa.obtenerValor(v_inicial), fb.obtenerValor(v_final), xr, fx.obtenerValor(xr), 1});
        while (error > ep){
            if((fx.obtenerValor()*fa.obtenerValor()) > 0)
                v_inicial = xr;
            else if((fx.obtenerValor()*fa.obtenerValor()) < 0)
                v_final = xr;
            xranterior = xr;
            xr = (v_inicial + v_final)/2;
            error = Math.abs((xr-xranterior)/xr);
            contador++;
            resultados.add(new double[]{contador, v_inicial, v_final, fa.obtenerValor(v_inicial), fb.obtenerValor(v_final), xr, fx.obtenerValor(xr),error*100});
        }
        raiz = xr;
    }
}
