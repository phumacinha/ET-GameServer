package jogos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 *
 * @author Pedro Antônio de Souza
 */


public final class JogoDaVelha extends Jogo {
    private ArrayList<Integer> tabuleiro;
    private int jogadorAtual;
    
    public JogoDaVelha() {
        super("Jogo da Velha", 2, 2);
    }
    
    public ArrayList<Integer> getTabuleiro() {
        return tabuleiro;
    }
    
    public Integer getCampo(int pos) {
        return tabuleiro.get(pos);
    }
    
    private void definirJogadorInicial () {
        jogadorAtual = new Random().nextInt(1);
    }
    
    private List<Integer> resultadoAtual() {
        // 8 formas de ganhar (3 horizontais, 3 verticais, 2 diagonais)
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
    
    private Integer checaGanhador(int campo1, int campo2, int campo3) {
        try {
            return tabuleiro.get(campo1) + tabuleiro.get(campo2) + tabuleiro.get(campo3);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public Object jogar (int jogador, Object param) {
        Integer campo = (Integer) param;
        
        if (tabuleiro.get(campo) == null) {
            tabuleiro.set(campo, jogador);
            List<Integer> resultado = resultadoAtual();
            
            return resultado;
        }
        
        return null;
    }
    
    @Override
    public void iniciar () {
        tabuleiro = new ArrayList<>();
        
        for (int i = 0; i < 9; i++) {
            tabuleiro.add(null);
        }
        definirJogadorInicial();
    }
    
    @Override
    public int getTurno() {
        return jogadorAtual;
    }
}