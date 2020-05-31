package jogos;

import java.util.Arrays;
import java.util.List;
/**
 *
 * @author Pedro Antônio de Souza
 */


public final class JogoDaVelha extends Jogo {
    private int[] tabuleiro;
    private int jogadorAtual;
    
    public JogoDaVelha() {
        super(2, 2);
        inicializarJogo();
    }
    
    public int[] getTabuleiro() {
        return tabuleiro;
    }
    
    public int getCampo(int pos) {
        return tabuleiro[pos];
    }
    
    public int getTurno() {
        return jogadorAtual;
    }
    
    public void iniciarNovoJogo() {
        tabuleiro = new int[9];
        
        for (int i = 0; i < 9; i++) {
            tabuleiro[i] = 0;
        }
    }    
    
    public int definirJogadorInicial (int i) {
        int jogador = -1;
        /*if (new Random().nextInt(2) == 1) {
        jogador = 1;
        }*/
        return jogador;
    }
    
    private List<Integer> checarGanhador() {
        // 8 formas de ganhar (3 horizontais, 3 verticais, 2 diagonais)
        for (int i = 1; i < 9; i++) {
            int resultado = 0;
            
            switch (i) {
                case 1:
                    //primeira linha
                    resultado = tabuleiro[0] + tabuleiro[1] + tabuleiro[2];
                    break;
                case 2:
                    //segunda linha
                    resultado = tabuleiro[3] + tabuleiro[4] + tabuleiro[5];
                    break;
                case 3:
                    //terceira linha
                    resultado = tabuleiro[6] + tabuleiro[7] + tabuleiro[8];
                    break;
                case 4:
                    //primeira coluna
                    resultado = tabuleiro[0] + tabuleiro[3] + tabuleiro[6];
                    break;
                case 5:
                    //primeira coluna
                    resultado = tabuleiro[1] + tabuleiro[4] + tabuleiro[7];
                    break;
                case 6:
                    //primeira coluna
                    resultado = tabuleiro[2] + tabuleiro[5] + tabuleiro[8];
                    break;
                case 7:
                    //diagonal principal
                    resultado = tabuleiro[0] + tabuleiro[4] + tabuleiro[8];
                    break;
                case 8:
                    //diagonal secundaria
                    resultado = tabuleiro[2] + tabuleiro[4] + tabuleiro[6];
                    break;
            }
            
            if (resultado == 3) {
                //retorna que o jogador 1 venceu no caso i
                return Arrays.asList(1, i);
            }
            else if (resultado == -3) {
                //retorna que o jogador -1 venceu no caso i
                return Arrays.asList(-1, i);
            }
        }
        
        if (!Arrays.asList(tabuleiro).contains(0)) {
            //retorna nenhum jogador venceu (0) e deu "velha" (-1)
            return Arrays.asList(0, -1);
        }

        //retorna nenhum jogador venceu (0) e ainda há jogadas
        return Arrays.asList(0, 0);
    }
    
    
    @Override
    public Object jogar (Object param) {
        Integer campo = (Integer) param;
        
        if (tabuleiro[campo] == 0) {
            tabuleiro[campo] = jogadorAtual;
            List<Integer> resultado = checarGanhador();
            if (resultado.get(0) == 0) jogadorAtual *= -1;
            
            return resultado;
        }
        
        return null;
    }
    
    @Override
    protected void inicializarJogo () {
        iniciarNovoJogo();
    }
    
}