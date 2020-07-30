/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package salas;

import MensagemSocket.Acao;
import MensagemSocket.MensagemParaCliente;
import cliente.Cliente;
import java.util.List;
import jogos.JogoDaVelha;
import servidor.Servidor;

/**
 *
 * @author pedro
 */
public class Sala_JogoDaVelha extends Sala<JogoDaVelha> {
    public Sala_JogoDaVelha (Servidor server) {
        super(new JogoDaVelha(), server);
    }
    
    @Override
    public void iniciarJogo () {
        jogo.iniciar();
        
        for (Cliente jogador : getJogadores()) {
            server.transmiteMensagem(jogador, new MensagemParaCliente(Acao.SET_ID, getClienteId(jogador)));
        }
        setTurno(jogo.getTurno());
        server.transmiteMensagem(this, new MensagemParaCliente(Acao.NOVO_TURNO, getTurno()));
    }
    
    @Override
    public void finalizarJogo () {}
    
    @Override
    public void removeCliente (Cliente cliente) {
        jogadores.set(jogadores.indexOf(cliente), null);
        finalizarJogo();
    }

    @Override
    protected Integer proximo() {
        Integer idAtual = (getTurno()+1)%jogadores.size();
        Cliente proximo = jogadores.get(idAtual);
        
        int flag = jogadores.size();
        while (proximo == null && proximo != jogadores.get(getTurno()) && flag > 0) {
            idAtual = (idAtual+1)%jogadores.size();
            proximo = jogadores.get(idAtual);
            --flag;
        }
        
        if (flag < 1) idAtual = null;
        
        setTurno(idAtual);
        return idAtual;
        
    }
    
    /**
     *
     * @param jogador Cliente que fez a jogada
     * @param param Objeto do tipo Integer que indica o campo do tabuleiro.
     */
    @Override
    public void jogar(Cliente jogador, Object param) {
        List<Integer> resultado = (List<Integer>) jogo.jogar(getTurno(), param);
        
        // Resultado é null se o jogador selecionar um campo já preenchido.
        // A interface é capaz de cuidar disso, porém há nova verificação no
        // servidor para evitar que jogares burlem.
        if (resultado == null) {
            MensagemParaCliente jogueNovamente = new MensagemParaCliente(Acao.JOGADA_INVALIDA, "Selecione um campo vazio!");
            server.transmiteMensagem(jogador, jogueNovamente);
        }
        // Caso seja selecionado um campo válido, continua.
        else {
            // Envia jogada para demais jogadores em uma lista contendo:
            //  > O jogador que efetuou a jogada
            //  > O campo (param) jogado
            //  > O resultado do jogo após a jogada
            List parametros = List.of(getTurno(), param, resultado);
            server.transmiteMensagem(this, new MensagemParaCliente(Acao.JOGADA, parametros));
            
            // Verifica se o jogo não terminou.
            if (resultado.get(0) == 0) {
                // Define jogador para próxima rodada (o metodo proximo() define
                // e retorna o proximo jogador, alem de atualizar o turno.
                // Caso não haja próximo jogador, finaliza o jogo.
                if(proximo() == null) finalizarJogo();
                else {
                    // Transmite mensagem de NOVO_TURNO para todos jogadores com o turno atualizado.
                    server.transmiteMensagem(this, new MensagemParaCliente(Acao.NOVO_TURNO, getTurno()));
                }
            }
        }
    }
    
    @Override
    public void abandonar(Cliente jogador) {
        
    }
}
