package jogos;


/**
 *
 * @author pedro
 */
public abstract class Jogo {
    
    private int minJogadores;
    private int maxJogadores;
    protected String nomeDoJogo;
    
    public Jogo (String nomeDoJogo, int minJogadores, int maxJogadores) {
        this.nomeDoJogo = nomeDoJogo;
        this.minJogadores = minJogadores;
        this.maxJogadores = maxJogadores;
    }
    
    public int getMaxJogadores () {
        return maxJogadores;
    }
    
    public int getMinJogadores () {
        return minJogadores;
    }
    
    public String getNome() {
        return nomeDoJogo;
    }
    
    public abstract Object jogar (int jogador, Object param);
    public abstract void iniciar();
    public abstract int getTurno();
}
