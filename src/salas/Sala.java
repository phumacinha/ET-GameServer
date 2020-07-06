/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package salas;

import MensagemSocket.Acao;
import MensagemSocket.MensagemParaCliente;
import java.util.ArrayList;

import cliente.Cliente;
import java.util.List;
import jogos.Jogo;
import servidor.Servidor;
/**
 *
 * @author pedro
 * @param <J>
 */
public abstract class Sala<J extends Jogo> {
    protected final J jogo;
    protected final ArrayList<Cliente> jogadores;
    protected Integer jogadorAtual;
    protected final Servidor server;
    
    public Sala (J jogo, Servidor server) {
        this.jogo = jogo;
        this.server = server;
        jogadores = new ArrayList<>(jogo.getMaxJogadores());
    }
    
    public J getJogo () {
        return jogo;
    }
    
    protected Servidor getServidor() {
        return server;
    }
    
    public ArrayList<Cliente> getJogadores () {
        return jogadores;
    }
    
    public Integer getTurno () {
        return jogadorAtual;
    }    
    
    protected void setTurno (Integer id) {
        jogadorAtual = id;
    }
    
    public int getClienteId (Cliente jogador) {
        return jogadores.indexOf(jogador);
    }
    
    public boolean estaCheia() {
        return jogadores.size() == jogo.getMaxJogadores();
    }
    
    public boolean inserirCliente (Cliente cliente) {
        if (!estaCheia()) {
            jogadores.add(cliente);
            cliente.setSala(this);
            return true;
        }
        return false;
    }
        
    
    // MÉTODOS ABAIXO SERÃO ABSTRATOS
    public void iniciarJogo () {}
    
    public void finalizarJogo () {}
    
    public abstract void removeCliente (Cliente cliente);

    protected abstract Integer proximo();
    
    public abstract void jogar(Cliente jogador, Object param);
    
    public void abandonar(Cliente jogador) {
        
    }
}
