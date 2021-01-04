package salas;

import java.util.ArrayList;

import cliente.Cliente;
import java.util.Objects;
import jogos.Jogo;
import comunicacao.jogos.TipoDeJogo;
import java.util.List;
import servidor.Servidor;
/**Classe abstrata e genérica para modelar a sala de um jogo.
 *
 * @author Pedro Antônio de Souza
 * @param <J> Classe que herda de Jogo.
 */
public abstract class Sala<J extends Jogo> {
    protected final J JOGO;
    protected final List<Cliente> CLIENTES;
    protected Integer jogadorAtual;
    protected final Servidor SERVER;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.JOGO);
        hash = 23 * hash + Objects.hashCode(this.CLIENTES);
        hash = 23 * hash + Objects.hashCode(this.SERVER);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Sala<?> other = (Sala<?>) obj;
        if (!Objects.equals(this.JOGO, other.JOGO)) {
            return false;
        }
        if (!Objects.equals(this.CLIENTES, other.CLIENTES)) {
            return false;
        }
        if (!Objects.equals(this.SERVER, other.SERVER)) {
            return false;
        }
        return true;
    }
    
    
    
    /**Construtor protegido da classe.
     * 
     * @param jogo Jogo que pertence à sala.
     * @param server Servidor que a sala pertence.
     */
    protected Sala (J jogo, Servidor server) {
        this.JOGO = jogo;
        this.SERVER = server;
        this.CLIENTES = new ArrayList<>(jogo.getMaxJogadores());
    }    
    
    /**Cria novas instâncias de sala.
     * 
     * @param tipoDeJogo Tipo de jogo da sala.
     * @param server Servidor que a sala pertence.
     * @return Sala específica para o tipo de jogo.
     */
    public static Sala novaSala(TipoDeJogo tipoDeJogo, Servidor server) {
        switch (tipoDeJogo) {
            case JOGO_DA_VELHA:
                return new Sala_JogoDaVelha(server);
            default:
                return null;
        }
    }
    
    /** Getter do servidor.
     * 
     * @return Servidor.
     */
    protected Servidor getServidor() {
        return SERVER;
    }
    
    /** Getter da lista de jogadores.
     * 
     * @return ArrayList de jogadores que estão na sala.
     */
    public List<Cliente> getJogadores () {
        return CLIENTES;
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
        return CLIENTES.indexOf(jogador);
    }
    
    /** Método para verificar se a sala está cheia.
     * 
     * @return True se estiver cheia, False caso contrário.
     */
    public boolean estaCheia() {
        return CLIENTES.size() == JOGO.getMaxJogadores();
    }
    
    /**Método para iniciar o jogo da sala.
     * 
     */
    public abstract void iniciarJogo ();
    
    /** Método para finalizar o jogo da sala.
     * 
     */
    public abstract void finalizarJogo ();
    
    /** Insere jogador na sala.
     * 
     * @param cliente Jogador a ser inserido.
     * @return True se a inserção ocorrer com sucesso, False caso contrário.
     */
    public abstract boolean inserirCliente (Cliente cliente);
    
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
    
    /** Getter do atributo tipoDeJogo.
     * 
     * @return Tipo de jogo.
     */
    public TipoDeJogo getTipoDeJogo () {
        return JOGO.getTipoDeJogo();
    }
}
