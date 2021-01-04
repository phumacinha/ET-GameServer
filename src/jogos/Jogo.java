package jogos;

import etgames.jogos.TipoDeJogo;

/** Classe abstrata para modelar jogos.
 *
 * @author Pedro Atônio de Souza.
 */
public abstract class Jogo {
    // Tipo de jogo
    private final TipoDeJogo TIPO_DE_JOGO;
    // Quantidade mínima de jogadores para que possa ser jogado.
    private final int MIN_JOGADORES;
    // Quantidade máxima de jogadores para que possa ser jogado.
    private final int MAX_JOGAODRES;
    // Nome do jogo.
    protected final String NOME;
    
    /**Construtor da classe.
     * 
     * @param nome Nome do jogo.
     * @param tipoDeJogo Tipo de jogo.
     * @param minJogadores Quantidade mínima de jogadores para que possa ser jogado.
     * @param maxJogadores Quantidade máxima de jogadores para que possa ser jogado.
     */
    public Jogo (String nome, TipoDeJogo tipoDeJogo, int minJogadores, int maxJogadores) {
        this.TIPO_DE_JOGO = tipoDeJogo;
        this.NOME = nome;
        this.MIN_JOGADORES = minJogadores;
        this.MAX_JOGAODRES = maxJogadores;
    }
    
    
    /**Getter do atributo tipoDeJogo.
     * 
     * @return Tipo do jogo.
     */
    public TipoDeJogo getTipoDeJogo() {
        return TIPO_DE_JOGO;
    }
    
    /**Getter da constante MAX_JOGAODRES.
     * 
     * @return Quantidade máxima de jogadores para que possa ser jogado.
     */
    public int getMaxJogadores () {
        return MAX_JOGAODRES;
    }
    
    /**Getter da constante MIN_JOGADORES.
     * 
     * @return Quantidade mínima de jogadores para que possa ser jogado.
     */
    public int getMinJogadores () {
        return MIN_JOGADORES;
    }
    
    /**Getter da constante NOME.
     * 
     * @return Nome do jogo.
     */
    public String getNome() {
        return NOME;
    }
    
    /**Método abstrado para a ação "jogar".
     * 
     * @param jogador Jogador que executou a jogada.
     * @param param Parametros da jogada.
     * @return Object do resultado da jogada.
     */
    public abstract Object jogar (int jogador, Object param);
    
    /**Método abstrato para iniciar o jogo. */
    public abstract void iniciar();
    
    /**Método para retornar de quem é o turno da jogada.
     * 
     * @return Inteiro que identifica o jogador.
     */ 
    public abstract int getTurno();
}
