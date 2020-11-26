package salas;

import java.util.ArrayList;

import cliente.Cliente;
import jogos.Jogo;
import servidor.Servidor;
/**Classe abstrata e genérica para modelar a sala de um jogo.
 *
 * @author Pedro Antônio de Souza
 * @param <J> Classe que herda de Jogo.
 */
public abstract class Sala<J extends Jogo> {
    protected final J jogo;
    protected final ArrayList<Cliente> jogadores;
    protected Integer jogadorAtual;
    protected final Servidor server;
    
    /**Construtor da classe.
     * 
     * @param jogo Jogo que pertence à sala.
     * @param server Servidor que a sala pertence.
     */
    public Sala (J jogo, Servidor server) {
        this.jogo = jogo;
        this.server = server;
        jogadores = new ArrayList<>(jogo.getMaxJogadores());
    }
    
    /** Getter do atributo jogo.
     * 
     * @return Jogo.
     */
    public J getJogo () {
        return jogo;
    }
    
    /** Getter do servidor.
     * 
     * @return Servidor.
     */
    protected Servidor getServidor() {
        return server;
    }
    
    /** Getter da lista de jogadores.
     * 
     * @return ArrayList de jogadores que estão na sala.
     */
    public ArrayList<Cliente> getJogadores () {
        return jogadores;
    }
    
    /** Getter turno.
     * 
     * @return Jogador atual.
     */
    public Integer getTurno () {
        return jogadorAtual;
    }    
    
    /** Setter turno.
     * 
     * @param id Identificador do jogador.
     */
    protected void setTurno (Integer id) {
        jogadorAtual = id;
    }
    
    /** Obtem identificador do jogador.
     * 
     * @param jogador Objeto Cliente que se busca o id.
     * @return Inteiro que representa o id do jogador.
     */
    public int getClienteId (Cliente jogador) {
        return jogadores.indexOf(jogador);
    }
    
    /** Método para verificar se a sala está cheia.
     * 
     * @return True se estiver cheia, False caso contrário.
     */
    public boolean estaCheia() {
        return jogadores.size() == jogo.getMaxJogadores();
    }
    
    /** Insere jogador na sala.
     * 
     * @param cliente Jogador a ser inserido.
     * @return True se a inserção ocorrer com sucesso, False caso contrário.
     */
    public boolean inserirCliente (Cliente cliente) {
        if (!estaCheia()) {
            jogadores.add(cliente);
            cliente.setSala(this);
            return true;
        }
        return false;
    }
    
    /**Método para iniciar o jogo da sala.
     * 
     */
    public void iniciarJogo () {}
    
    /** Método para finalizar o jogo da sala.
     * 
     */
    public void finalizarJogo () {}
    
    /**Método abstrato para remover cliente da sala.
     * 
     * @param cliente Cliente a ser removido.
     */
    public abstract void removeCliente (Cliente cliente);

    /**Método para passar a vez para próximo jogador.
     * 
     * @return Inteiro que representa o próximo jogador.
     */
    protected abstract Integer proximo();
    
    /**Método para enviar jogada.
     * 
     * @param jogador Jogador que executou a jogada.
     * @param param Paramêtros da jogada.
     */
    public abstract void jogar(Cliente jogador, Object param);
    
    /**Método para abandono de sala.
     * 
     * @param jogador Jogador que abandonou a sala.
     */
    public abstract void abandonar(Cliente jogador);
}
