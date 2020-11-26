package jogos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Classe herdeira da classe Jogo que modela um jogo da velha.
 *
 * @author Pedro Antônio de Souza
 */
public final class JogoDaVelha extends Jogo {
    private ArrayList<Integer> tabuleiro;
    private int jogadorAtual = -1;
    
    /**Construtor da classe. */
    public JogoDaVelha() {
        super("Jogo da Velha", 2, 2);
    }
    
    /**Getter do tabuleiro.
     * 
     * @return ArrayList do tabuleiro.
     */
    public ArrayList<Integer> getTabuleiro() {
        return tabuleiro;
    }
    
    /**Retorna status de algum campo específico do tabuleiro.
     * 
     * @param pos Posição do campo.
     * @return Integer que está armazenado na posição "pos" do tabuleiro.
     */
    public Integer getCampo(int pos) {
        return tabuleiro.get(pos);
    }
    
    /**Método para definir, aleatoriamente, o jogador que irá iniciar a partida.
     */
    private void definirJogadorInicial () {
        jogadorAtual = jogadorAtual == -1 ? new Random().nextInt(1) : jogadorAtual;
    }
    
    /** Método que retorna o status atual do jogo.
     * O status é definido por um objeto List com dois valores.
     * O primeiro valor será 0 ou 1, caso o jogador 0 ou o jogador 1 ganhem a
     * partida respectivamente. Em caso de vitória, o segundo número indicará o 
     * tipo de vitória, podendo ser de 1 a 8 (no jogo da velha há 8 formas de
     * ganhar: 3 horizontais, 3 verticais e 2 diagonais). O primeiro valor da
     * lista de retorno também poderá ser -1, quando ainda não há ganhadores. 
     * Nos casos em que não há ganhadores, o segundo número será 0 quando ainda
     * há jogadas ou -1 quando não houver mais jogadas (deu velha).
     * 
     * @return List com dois valores.  
     */
    private List<Integer> resultadoAtual() {
        // 8 formas de ganhar ()
        for (int i = 1; i < 9; i++) {
            Integer resultado = null;
            
            switch (i) {
                case 1: //primeira linha
                    resultado = checaGanhador(0, 1, 2);
                    break;
                case 2: //segunda linha
                    resultado = checaGanhador(3, 4, 5);
                    break;
                case 3: //terceira linha
                    resultado = checaGanhador(6, 7, 8);
                    break;
                case 4: //primeira coluna
                    resultado = checaGanhador(0, 3, 6);
                    break;
                case 5: //segunda coluna
                    resultado = checaGanhador(1, 4, 7);
                    break;
                case 6: //terceira coluna
                    resultado = checaGanhador(2, 5, 8);
                    break;
                case 7: //diagonal principal
                    resultado = checaGanhador(0, 4, 8);
                    break;
                case 8: //diagonal secundaria
                    resultado = checaGanhador(2, 4, 6);
                    break;
            }
            
            if (Integer.valueOf(3).equals(resultado)) {
                //retorna que o jogador 1 venceu no caso i
                return List.of(1, i);
            }
            else if (Integer.valueOf(0).equals(resultado)) {
                //retorna que o jogador 0 venceu no caso i
                return List.of(0, i);
            }
        }
        
        if (!tabuleiro.contains(null)) {
            //retorna nenhum jogador venceu -1 e deu "velha" -1
            return List.of(-1, -1);
        }

        //retorna nenhum jogador venceu 0 e ainda há jogadas
        return List.of(-1, 0);
    }
    
    /**Checa se a soma de três campos passados como argumento resultam em 0 ou 
     * 3, verificando, assim, se há algum ganhador..
     * 
     * @param campo1 Primeiro campo.
     * @param campo2 Segundo campo.
     * @param campo3 Terceiro campo.
     * @return Resultado da soma dos campos ou null caso algum dos 3 campos seja
     * null.
     */
    private Integer checaGanhador(int campo1, int campo2, int campo3) {
        try {
            return tabuleiro.get(campo1) + tabuleiro.get(campo2) + tabuleiro.get(campo3);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    /**Sobrecarga do método jogar().
     * 
     * @param jogador Inteiro que identifica o jogador.
     * @param param Parametro da jogada. No caso do jogo da velha é um inteiro
     * que representa a posição do campo selecionado pelo jogador.
     * 
     * @return List de dois inteiros obtido através do método resultadoAtual() 
     * que representa o status do jogo após a jogada.
     */
    @Override
    public Object jogar (int jogador, Object param) {
        // Converte o param para Integer.
        Integer campo = (Integer) param;
        
        // Verifica se o campo selecionado está vazio (null).
        if (tabuleiro.get(campo) == null) {
            tabuleiro.set(campo, jogador);
            List<Integer> resultado = resultadoAtual();
            
            if (!resultado.equals(List.of(-1, 0))) {
                System.out.println("Iniciando novo jogo");
                iniciar();
                System.out.println(tabuleiro);
            }
            
            return resultado;
        }
        
        // Retorna null caso o jogador tenha selecionado um campo inválido.
        return null;
    }
    
    /** Sobrecarga do método iniciar().
     * Inicializa o tabuleiro com um ArrayList com 9 posições preenchidas com 
     * null e define o jogador inicial através do método definirJogadorInicial().
     */
    @Override
    public void iniciar () {
        tabuleiro = new ArrayList<>();
        
        for (int i = 0; i < 9; i++) {
            tabuleiro.add(null);
        }
        definirJogadorInicial();
    }
    
    /**Sobrecarga do método getTurno().
     * 
     * @return Inteiro com o valor armazenado no atributo jogadorAtual.
     */
    @Override
    public int getTurno() {
        return jogadorAtual;
    }
}