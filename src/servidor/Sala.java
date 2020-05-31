/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.util.List;
import java.util.ArrayList;

import cliente.Cliente;
import jogos.Jogo;

/**
 *
 * @author pedro
 */
public class Sala {
    private final Jogo jogo;
    private final List<Cliente> clientes;
    
    public Sala (Jogo jogo) {
        this.jogo = jogo;
        clientes = new ArrayList<>(jogo.getMaxJogadores());
    }
    
    public Jogo getJogo () {
        return jogo;
    }
    
    public int idCliente (Cliente jogador) {
        return clientes.indexOf(jogador);
    }
    
    public List<Cliente> getJogadores () {
        return clientes;
    }
    
    public void inserirCliente (Cliente cliente) {
        if (clientes.size() < jogo.getMaxJogadores())
            clientes.add(cliente);
    }
    
    public void removeCliente (Cliente cliente) {
        clientes.remove(cliente);
    }
}
