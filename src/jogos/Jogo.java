package jogos;

/** Classe abstrata para modelar jogos.
 *
 * @author Pedro Atônio de Souza.
 */
public abstract class Jogo {
    // Quantidade mínima de jogadores para que possa ser jogado.
    private int minJogadores;
    // Quantidade máxima de jogadores para que possa ser jogado.
    private int maxJogadores;
    // Nome do jogo.
    protected String nome;
    
    /**Construtor da classe.
     * 
     * @param nome Nome do jogo.
     * @param minJogadores Quantidade mínima de jogadores para que possa ser jogado.
     * @param maxJogadores Quantidade máxima de jogadores para que possa ser jogado.
     */
    public Jogo (String nome, int minJogadores, int maxJogadores) {
        this.nome = nome;
        this.minJogadores = minJogadores;
        this.maxJogadores = maxJogadores;
    }
    
    /**Getter do atributo maxJogadores.
     * 
     * @return Quantidade máxima de jogadores para que possa ser jogado.
     */
    public int getMaxJogadores () {
        return maxJogadores;
    }
    
    /**Getter do atributo minJogadores.
     * 
     * @return Quantidade mínima de jogadores para que possa ser jogado.
     */
    public int getMinJogadores () {
        return minJogadores;
    }
    
    /**Getter do atributo nome.
     * 
     * @return Nome do jogo.
     */
    public String getNome() {
        return nome;
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
