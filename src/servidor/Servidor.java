package servidor;

import salas.Sala;
import cliente.Cliente;
import etgames.mensagens.Acao;
import etgames.mensagens.MensagemParaCliente;
import etgames.mensagens.MensagemParaServidor;
import etgames.jogos.TipoDeJogo;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**Classe servidor.
 *
 * @author Raphael Winckler
 * @author Pedro Antônio de Souza
 */
public class Servidor {

    private final List<Cliente> CLIENTES;
   // private final Queue<Cliente> FILA_DE_ESPERA;
    private final Map<TipoDeJogo, Queue<Cliente>> FILAS_DE_ESPERA;
    private final ServerSocket SERVER_SOCKET;

    /**Construtor da classe.
     * 
     * @throws IOException 
     */
    public Servidor() throws IOException {
        SERVER_SOCKET = new ServerSocket(4545);
        
        CLIENTES = new ArrayList<>();
        FILAS_DE_ESPERA = new ConcurrentHashMap<>();
        //FILA_DE_ESPERA = new ConcurrentLinkedDeque<>();
        
        iniciaServidor();
    }
    
    /**Método para iniciar o servidor.
     * O método fica em laço infinito esperando conexões.
     */
    private void iniciaServidor () {
        //O serviço fica indefinidamente recebendo conexões
        while (true) {
            try{
                //Espera por conexões de clientes
                System.out.println("Esperando conexões ...");
                // Recebe conexão.
                Socket socket = SERVER_SOCKET.accept();
                System.out.println("Recebendo conexão de " + socket.toString());
                // Instancia cliente
                Cliente novoCliente = new Cliente(socket, this);
                // Adiciona cliente na lista
                CLIENTES.add(novoCliente);
                // Inicia thread do novo cliente
                novoCliente.start();
                // Avisa ao cliente que ele esta' conectado
                transmiteMensagem(novoCliente, new MensagemParaCliente(Acao.CONECTADO));
            }
            catch (IOException ex) {
                // Não foi possível criar nova instancia de Cliente
                System.out.println("Socket fechado.");
            }
        }
    }
    
    /**
     * Procura sala para um cliente.
     * @param cliente Cliente que precisa ser alocado em uma sala.
     * @throws IOException Exceção pode ser lançada no envio da mensagem
     * ao cliente.
     */
    /*private boolean procuraSala (Cliente cliente) {
        //Envia mensagem para o cliente informando que o servidor esta procurando
        //uma sala para ele
        transmiteMensagem(cliente, new MensagemParaCliente(Acao.PROCURANDO_SALA));
        
        boolean sucesso;
        // Retorna a cabeça da fila.
        Cliente adversario = FILA_DE_ESPERA.poll();
        //Verifica se há clientes na sala de espera.
        if (adversario != null) {
            
            Sala sala = adversario.getSala();
            sucesso = sala.inserirCliente(cliente);
            
            // Ocorreu erro ao adicionar novo jogador a sala           
            if (!sucesso) {
                // Retorna adversário à fila de espera
                ((ConcurrentLinkedDeque) FILA_DE_ESPERA).addFirst(cliente);
            }
            else {
                // Transmite aos jogadores que a sala foi encontrada
                transmiteMensagem(sala, new MensagemParaCliente(Acao.SALA_ENCONTRADA)); 
                sala.iniciarJogo();
            }
        }
        else {
            Sala sala = new Sala_JogoDaVelha(this);
            sucesso = sala.inserirCliente(cliente);
            FILA_DE_ESPERA.add(cliente);
        }
        
        return sucesso;
    }*/
    
    private boolean procuraSala (Cliente cliente, TipoDeJogo jogo) {
        //Envia mensagem para o cliente informando que o servidor esta procurando
        //uma sala para ele
        transmiteMensagem(cliente, new MensagemParaCliente(Acao.PROCURANDO_SALA));
        
        boolean sucesso;
        
        // Verifica se já existe fila de espera para o jogo
        if (!FILAS_DE_ESPERA.containsKey(jogo)) {
            // Se não existir, cria nova fila
            FILAS_DE_ESPERA.put(jogo, new ConcurrentLinkedDeque<Cliente>());
        }
        
        // Fila de espera específica do jogo buscado
        Queue<Cliente> filaDeEspera = FILAS_DE_ESPERA.get(jogo);

        // Retorna a cabeça da fila.
        Cliente adversario = filaDeEspera.poll();

        //Verifica se há clientes na sala de espera.
        if (adversario != null) {
            Sala sala = adversario.getSala();
            sucesso = sala.inserirCliente(cliente);

            // Ocorreu erro ao adicionar novo jogador a sala           
            if (!sucesso) {
                // Retorna adversário à fila de espera
                ((ConcurrentLinkedDeque) filaDeEspera).addFirst(cliente);
            }
            else {
                // Transmite aos jogadores que a sala foi encontrada
                transmiteMensagem(sala, new MensagemParaCliente(Acao.SALA_ENCONTRADA)); 
                sala.iniciarJogo();
            }
        }
        else {
            Sala sala = Sala.novaSala(jogo, this);
            sucesso = sala.inserirCliente(cliente);
            filaDeEspera.add(cliente);
        }
        
        return sucesso;
    }
    
    /**
     * Transmite mensagem de um cliente emissor para outro cliente receptor.
     * @param mensagem Mensagem a ser enviada.
     * @param emissor Cliente que está enviando a mensagem.
     * @param receptor Clientes que devem receber a mensagem.
     */
    public void transmiteMensagem(Cliente receptor, MensagemParaCliente mensagem, Cliente emissor) {
        if (!receptor.equals(emissor)) {
            try {
                receptor.receptaMensagem(mensagem);
            }        
            catch (IOException ioe) {
                System.out.println("Exceção ao transmitir mensagem.");
            }
        }
    }
    
    /**
     * Transmite mensagem para um cliente.
     * @param mensagem Mensagem a ser enviada.
     * @param receptor Cliente que deve receber a mensagem.
     */
    public void transmiteMensagem(Cliente receptor, MensagemParaCliente mensagem) {
        transmiteMensagem(receptor, mensagem, null);        
    }
    
    /**
     * Transmite mensagem de um emissor para um ou mais receptores diferentes do emissor.
     * @param mensagem Mensagem a ser enviada.
     * @param emissor Cliente que está enviando a mensagem.
     * @param receptores Clientes que devem receber a mensagem.
     */
    public void transmiteMensagem(List<Cliente> receptores, MensagemParaCliente mensagem, Cliente emissor) {
        receptores.forEach(receptor -> {
            transmiteMensagem(receptor, mensagem, emissor);
        });
    }
    
    /**
     * Transmite mensagem para uma lista de clientes.
     * @param mensagem Mensagem a ser enviada.
     * @param receptores Clientes que devem receber a mensagem.
     */
    public void transmiteMensagem(List<Cliente> receptores, MensagemParaCliente mensagem) {
        transmiteMensagem(receptores, mensagem, null);
    }
       
    /**
     * Transmite mensagem de um cliente para os demais jogadores de sua sala,
     * desde que o emissor também esteja na sala.
     * @param mensagem Mensagem a ser enviada.
     * @param emissor Cliente que está enviando a mensagem.
     * @param sala Sala onde estão os clientes que devem receber a mensagem.
     */
    public void transmiteMensagem(Sala sala, MensagemParaCliente mensagem, Cliente emissor) {
        if (sala.equals(emissor.getSala()))
            transmiteMensagem(sala.getJogadores(), mensagem, emissor);
    }
    
    /**
     * Transmite mensagem para os clientes de uma sala.
     * @param mensagem Mensagem a ser enviada.
     * @param sala Sala onde estão os clientes que devem receber a mensagem.
     */
    public void transmiteMensagem(Sala sala, MensagemParaCliente mensagem) {
        transmiteMensagem(sala.getJogadores(), mensagem, null);
    }
    
    /** Remove cliente da lista de clientes.
     * 
     * @param cliente Cliente a ser removido.
     */
    public void removeCliente (Cliente cliente) {
        CLIENTES.remove(cliente);
    }
    
    /** Tratador de mensagens recebidas.
     * 
     * @param mensagem Mensagem recebida.
     */
    public void trataMensagem(MensagemParaServidor mensagem) {
        Cliente remetente = (Cliente) mensagem.getRemetente();
        
        switch (mensagem.getAcao()) {
            // transmite para todos clientes do getServidor
            // a mensagem a ser transmitida deve ser o parametro da mensagem recebida
            case BROADCAST:
                transmiteMensagem(CLIENTES, mensagem.getMensagemParaCliente());
                break;
            
            // broadcast exclusivo: transmite para todos clientes do getServidor
            // exceto para o cliente remetente
            case BROADCAST_X:
                transmiteMensagem(CLIENTES, mensagem.getMensagemParaCliente(), remetente);
                break;
                
            // transmite para todos clientes da sala
            // a mensagem a ser transmitida deve ser o parametro da mensagem recebida
            case BROADCAST_SALA:
                transmiteMensagem(remetente.getSala(), mensagem.getMensagemParaCliente());
                break;
            
            // broadcast exclusivo dentro da sala: transmite para todos clientes da sala
            // exceto para o cliente remetente
            case BROADCAST_X_SALA:
                transmiteMensagem(remetente.getSala().getJogadores(), mensagem.getMensagemParaCliente(), remetente);
                break;
            
            // envia mensagem para todos os destinatarios especificados na mensagem
            case MENSAGEM_COM_DESTINATARIO:
                ArrayList<Cliente> destinatarios = (ArrayList<Cliente>) mensagem.getDestinatarios();
                transmiteMensagem(destinatarios, mensagem.getMensagemParaCliente());
                break;
            
            case PROCURANDO_SALA:
                TipoDeJogo tipoJogo = (TipoDeJogo) mensagem.getParametro();
                procuraSala(remetente, tipoJogo);
                break;
            
            case JOGADA:
                Object parametros = mensagem.getParametro();
                remetente.getSala().jogar(remetente, parametros);
                break;
            
            case ABANDONO:
                Sala sala = remetente.getSala();
                if (sala != null) {
                    FILAS_DE_ESPERA.get(sala.getTipoDeJogo()).remove(remetente);
                    sala.abandonar(remetente);
                }
                break;
                
            default:
                System.out.println("Ação inválida.");
                break;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.SERVER_SOCKET);
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
        final Servidor other = (Servidor) obj;
        if (!Objects.equals(this.SERVER_SOCKET, other.SERVER_SOCKET)) {
            return false;
        }
        return true;
    }    
}
