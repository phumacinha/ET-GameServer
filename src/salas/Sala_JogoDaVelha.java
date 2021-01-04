package salas;

import comunicacao.mensagens.Acao;
import comunicacao.mensagens.MensagemParaCliente;
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
        JOGO.iniciar();
        
        // Transmite mensagem mensagem para todos os jogadores com seus
        // respectivos identificadores.
        getJogadores().forEach(jogador -> {
            SERVER.transmiteMensagem(jogador, new MensagemParaCliente(Acao.SET_ID, getClienteId(jogador)));
        });
        
        // Define turno.
        setTurno(JOGO.getTurno());
        
        // Envia mensagem para todos jogadores da sala com o jogador que irá
        // iniciar a partida.
        SERVER.transmiteMensagem(this, new MensagemParaCliente(Acao.NOVO_TURNO, getTurno()));
    }
    
    /** Sobrecarga do método finalizarJogo().
     * 
     */
    @Override
    public void finalizarJogo () {}
    
    /** Insere jogador na sala.
     * 
     * @param cliente Jogador a ser inserido.
     * @return True se a inserção ocorrer com sucesso, False caso contrário.
     */
    @Override
    public boolean inserirCliente (Cliente cliente) {
        if (!estaCheia()) {
            CLIENTES.add(cliente);
            cliente.setSala(this);
            return true;
        }
        return false;
    }
    
    /** Método para remover cliente.
     * 
     * @param cliente Cliente a ser removido.
     */
    @Override
    public void removeCliente (Cliente cliente) {
        CLIENTES.remove(cliente);
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
        Integer idProximo = (getTurno()+1)%CLIENTES.size();
        // Obtém objeto Cliente do proximo jogador.
        Cliente proximo = CLIENTES.get(idProximo);
        
        int flag = CLIENTES.size();
        // Enquanto não encontrar próximo jogador, continua procurando.
        while (proximo == null && proximo != CLIENTES.get(getTurno()) && flag > 0) {
            idProximo = (idProximo+1)%CLIENTES.size();
            proximo = CLIENTES.get(idProximo);
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
        List<Integer> resultado = (List<Integer>) JOGO.jogar(getTurno(), param);
        
        // Resultado é null se o jogador selecionar um campo já preenchido.
        // A interface é capaz de cuidar disso, porém há nova verificação no
        // servidor para evitar que jogares burlem.
        if (resultado == null) {
            MensagemParaCliente jogueNovamente = new MensagemParaCliente(Acao.JOGADA_INVALIDA, "Selecione um campo vazio!");
            SERVER.transmiteMensagem(jogador, jogueNovamente);
        }
        // Caso seja selecionado um campo válido, continua.
        else {
            // Envia jogada para demais jogadores em uma lista contendo:
            //  > O jogador que efetuou a jogada
            //  > O campo (param) jogado
            //  > O resultado do jogo após a jogada
            List parametros = List.of(getTurno(), param, resultado);
            SERVER.transmiteMensagem(this, new MensagemParaCliente(Acao.JOGADA, parametros));
            
            // Verifica se o jogo não terminou.
            if (resultado.get(0) == -1) {
                // Define jogador para próxima rodada (o metodo proximo() define
                // e retorna o proximo jogador, alem de atualizar o turno.
                // Caso não haja próximo jogador, finaliza o jogo.
                if(proximo() == null) finalizarJogo();
                else {
                    // Transmite mensagem de NOVO_TURNO para todos jogadores com o turno atualizado.
                    SERVER.transmiteMensagem(this, new MensagemParaCliente(Acao.NOVO_TURNO, getTurno()));
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
        SERVER.transmiteMensagem(this, new MensagemParaCliente(Acao.ABANDONO), emissor);
        
        CLIENTES.forEach(cliente -> {
            cliente.setSala(null);
        });
        
        CLIENTES.clear();
    }
}
