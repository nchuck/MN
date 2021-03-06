package com.itc.mn.Things;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import org.nfunk.jep.*;

/**
 *  Es una ecuacion en funcion de x por defecto.
 *  Se puede definir otra variable (para expresarse en terminos de otra, como en terminos de y o t)
 */
public strictfp class FuncionX {

    private JEP parser;
    private String ecuacion, variable;
    private double valorVariable;
    private double inicio, fin;
    private Const config = new Json().fromJson(Const.class, Gdx.app.getPreferences(Const.pref_name).getString(Const.id));

    {
        parser = new JEP();
        // Le decimos al parser que habilite las constantes y funciones basicas
        parser.addStandardConstants();
        parser.addStandardFunctions();
        parser.setAllowUndeclared(true);
        // Habilitamos la multiplicacion implicita
        parser.setImplicitMul(true);
        variable = "x";
        valorVariable = 0;
        inicio = -20;
        fin = 20;
    }

    /**
     * Crea una ecuacion en funcion de x (con valor de 0 por defecto)
     * @param ecuacion
     */
    public FuncionX(String ecuacion){
        this.ecuacion = ecuacion;
        // Le asignamos la funcion al parser
        parser.parseExpression(ecuacion);
        // Asignamos x con un valor por defecto
        parser.addVariable(variable, valorVariable);
//        Node topNode = parser.getTopNode();
//        System.out.println("Nodos");
//        if (topNode instanceof ASTConstant)
//            System.out.println(((ASTConstant)topNode).getValue());
//        if (topNode instanceof ASTVarNode)
//            System.out.println(((ASTVarNode)topNode).getName());
//        if (topNode instanceof ASTFunNode)
//            System.out.println(((ASTFunNode)topNode).getName());
//        System.out.println("Fin nodos");
    }

    /**
     * Le asigna un nuevo valor a la variable
     * @param valor Nuevo valor de la variable
     */
    public void valorVariable(double valor){
        valorVariable = valor;
        parser.addVariable(variable, valorVariable);
    }


    public void reemplazaVariable(String variable, double valor){
        parser.removeVariable(this.variable);
        this.variable = variable;
        parser.addVariable(variable, valor);
    }

    /**
     * Devuelve el valor de la funcion con el ultimo valor asignado
     * @return double
     */
    public double obtenerValor(){
        return parser.getValue();
    }

    /**
     * Regresa el valor de la funcion directamente en el valor asignado
     * @param valorVariable valor a poner en la variable actual
     * @return
     */
    public double obtenerValor(double valorVariable){
        valorVariable(valorVariable);
        return obtenerValor();
    }

    /**
     * Evalua la funcion en un rango dado y devuelve los valores para graficar
     * @param inicio Inicio del rango
     * @param fin Fin del rango
     * @param paso Medida del paso_r
     * @return double[][] valores
     */
    public double[][] obtenerRango(double inicio, double fin, double paso){
        // Guardamos los valores de inicio_r o fin_r, para uso en otras funciones
        this.inicio = inicio;
        this.fin = fin;
        int precision = (int)(Math.abs(fin - inicio) / paso);
        double[][] tmp = new double[precision + 1][2];
        // Lo usamos para evaluar del menor al mayor
        double menor = menor(inicio, fin);
        for (int i = 0; i <= precision; i++) {
            double valor_x = menor + (paso*i);
            tmp[i][0] = valor_x;
            tmp[i][1] = obtenerValor(valor_x);
        }
        return tmp;
    }

    /**
     * Devuelve el valor entre un rango dado, con un paso_r entre valores de 0.01
     * @param inicio Inicio del rango
     * @param fin Fin del rango
     * @return Valores de la expresion evaluada en el rango dado
     */
    public double[][] obtenerRango(float inicio, float fin){
        return obtenerRango(inicio, fin, config.currentPoints);
    }

    private double menor(double a, double b){
        return (a < b) ? a: b;
    }
}