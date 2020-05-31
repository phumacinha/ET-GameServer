/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogos;

/**
 *
 * @author pedro
 */
public abstract class Jogo {
    
    private int minJogadores;
    private int maxJogadores;
    
    public Jogo (int minJogadores, int maxJogadores) {
        this.minJogadores = minJogadores;
        this.maxJogadores = maxJogadores;
    }
    
    public int getMaxJogadores () {
        return maxJogadores;
    }
    
    public int getMinJogadores () {
        return minJogadores;
    }
    
    public abstract Object jogar (Object param);
    protected abstract void inicializarJogo();
}
