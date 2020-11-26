package salas;

import MensagemSocket.Acao;
import MensagemSocket.MensagemParaCliente;
import cliente.Cliente;
import java.util.List;
import jogos.JogoDaVelha;
import servidor.Servidor;

/**Classe herdeira de Sala.
 *
 * @author Pedro Antônio de Souza.
 */
public class Sala_JogoDaVelha extends Sala<JogoDaVelha> {
    /**Construtor da classe.
     * 
     * @param server Servidor da sala.
     */
    public Sala_JogoDaVelha (Servidor server) {
        super(new JogoDaVelha(), server);
    }
    
    /**Sobrecarga do método iniciarJogo().
     * 
     */
    @Override
    public void iniciarJogo () {
        // Inicia o jogo.
        jogo.iniciar();
        
        // Transmite mensagem mensagem para todos os jogadores com seus
        // respectivos identificadores.
        getJogadores().forEach(jogador -> {
            server.transmiteMensagem(jogador, new MensagemParaCliente(Acao.SET_ID, getClienteId(jogador)));
        });
        
        // Define turno.
        setTurno(jogo.getTurno());
        
        // Envia mensagem para todos jogadores da sala com o jogador que irá
        // iniciar a partida.
        server.transmiteMensagem(this, new MensagemParaCliente(Acao.NOVO_TURNO, getTurno()));
    }
    
    /** Sobrecarga do método finalizarJogo().
     * 
     */
    @Override
    public void finalizarJogo () {}
    
    /** Método para remover cliente.
     * 
     * @param cliente Cliente a ser removido.
     */
    @Override
    public void removeCliente (Cliente cliente) {
        jogadores.remove(cliente);
        cliente.setSala(null);
        finalizarJogo();
    }

    /**Método para definir próximo jogador.
     * 
     * @return Identificador do próximo jogador ou null caso não haja mais
     * jogadores..
     */
    @Override
    protected Integer proximo() {
        // Calcula o id do próximo jogador.
        Integer idProximo = (getTurno()+1)%jogadores.size();
        // Obtém objeto Cliente do proximo jogador.
        Cliente proximo = jogadores.get(idProximo);
        
        int flag = jogadores.size();
        // Enquanto não encontrar próximo jogador, continua procurando.
        while (proximo == null && proximo != jogadores.get(getTurno()) && flag > 0) {
            idProximo = (idProximo+1)%jogadores.size();
            proximo = jogadores.get(idProximo);
            --flag;
        }
        
        // Se procurou na lista inteira e não encontrou um jogador diferente do
        // último que jogou.
        if (flag < 1) idProximo = null;
        
        setTurno(idProximo);
        return idProximo;
        
    }
    
    /** Método para executar jogada.
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
            if (resultado.get(0) == -1) {
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
    
    /** Método para executar abandono da sala.
     * 
     * @param emissor Jogador que solicitou o abandono.
     */
    @Override
    public void abandonar(Cliente emissor) {
        server.transmiteMensagem(this, new MensagemParaCliente(Acao.ABANDONO), emissor);
        
        jogadores.forEach(jogador -> {
            jogador.setSala(null);
        });
        
        jogadores.clear();
    }
}
