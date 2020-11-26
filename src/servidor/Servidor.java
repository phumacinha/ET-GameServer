package servidor;

import salas.Sala;
import MensagemSocket.Acao;
import MensagemSocket.MensagemParaCliente;
import MensagemSocket.MensagemParaServidor;
import cliente.Cliente;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import salas.Sala_JogoDaVelha;

/**Classe servidor.
 *
 * @author Raphael Winckler
 * @author Pedro Antônio de Souza
 */
public class Servidor {

    private final ArrayList<Cliente> clientes;
    private final ArrayList<Cliente> salaDeEspera;
    private final ServerSocket serverSocket;

    /**Construtor da classe.
     * 
     * @throws IOException 
     */
    public Servidor() throws IOException {
        serverSocket = new ServerSocket(4545);
        
        clientes = new ArrayList<>();
        salaDeEspera = new ArrayList<>();
        
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
                Socket socket = serverSocket.accept();
                System.out.println("Recebendo conexão de "+socket.toString());
                
                Cliente novoCliente = new Cliente(socket, this);
                clientes.add(novoCliente);
                novoCliente.start();
                transmiteMensagem(novoCliente, new MensagemParaCliente(Acao.CONECTADO));
            }
            catch (IOException ex) {
                System.out.println("Socket fechado");
            }
        }
    }
    
    /**
     * Procura sala para um cliente.
     * @param cliente Cliente que precisa ser alocado em uma sala.
     * @throws IOException Exceção pode ser lançada no envio da mensagem
     * ao cliente.
     */
    private boolean procuraSala (Cliente cliente) {
        //Envia mensagem para o cliente informando que o servidor esta procurando
        //uma sala para ele
        transmiteMensagem(cliente, new MensagemParaCliente(Acao.PROCURANDO_SALA));
        
        boolean sucesso;
        
        //Verifica se há clientes na sala de espera.
        if (salaDeEspera.size() > 0) {
            Cliente adversario = salaDeEspera.get(0);
            salaDeEspera.remove(0);
            
            Sala sala = adversario.getSala();
            sucesso = sala.inserirCliente(cliente);
            
            if (!sucesso && !sala.estaCheia())
                salaDeEspera.add(0, adversario);
            else if (sucesso) {
                transmiteMensagem(sala, new MensagemParaCliente(Acao.SALA_ENCONTRADA)); 
                sala.iniciarJogo();
            }
        }
        else {
            Sala sala = new Sala_JogoDaVelha(this);
            sucesso = sala.inserirCliente(cliente);
            salaDeEspera.add(cliente);
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
            try { receptor.enviaMensagem(mensagem); }        
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
    public void transmiteMensagem(ArrayList<Cliente> receptores, MensagemParaCliente mensagem, Cliente emissor) {
        receptores.forEach(receptor -> {
            transmiteMensagem(receptor, mensagem, emissor);
        });
    }
    
    /**
     * Transmite mensagem para um conjunto de clientes.
     * @param mensagem Mensagem a ser enviada.
     * @param receptores Clientes que devem receber a mensagem.
     */
    public void transmiteMensagem(ArrayList<Cliente> receptores, MensagemParaCliente mensagem) {
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
        clientes.remove(cliente);
    }
    
    /** Tratador de mensagens recebidas.
     * 
     * @param mensagem Mensagem recebida.
     */
    public void trataMensagem(MensagemParaServidor mensagem) {
        Cliente emissor = (Cliente) mensagem.getRemetente();
        
        switch (mensagem.getAcao()) {
            // transmite para todos clientes do getServidor
            // a mensagem a ser transmitida deve ser o parametro da mensagem recebida
            case BROADCAST:
                transmiteMensagem(clientes, mensagem.getMensagemParaCliente());
                break;
            
            // broadcast exclusivo: transmite para todos clientes do getServidor
            // exceto para o cliente remetente
            case BROADCAST_X:
                transmiteMensagem(clientes, mensagem.getMensagemParaCliente(), emissor);
                break;
            
            // transmite para todos clientes da sala
            // a mensagem a ser transmitida deve ser o parametro da mensagem recebida
            case BROADCAST_SALA:
                transmiteMensagem(emissor.getSala(), mensagem.getMensagemParaCliente());
                break;
            
            // broadcast exclusivo dentro da sala: transmite para todos clientes da sala
            // exceto para o cliente remetente
            case BROADCAST_X_SALA:
                transmiteMensagem(emissor.getSala().getJogadores(), mensagem.getMensagemParaCliente(), emissor);
                break;
            
            // envia mensagem para todos os destinatarios especificados na mensagem
            case MENSAGEM_COM_DESTINATARIO:
                ArrayList<Cliente> destinatarios = (ArrayList<Cliente>) mensagem.getDestinatarios();
                transmiteMensagem(destinatarios, mensagem.getMensagemParaCliente());
                break;
            
            case PROCURANDO_SALA:
                //TipoDeJogo tipoJogo = (TipoDeJogo) mensagem.getParametro();
                System.out.println(salaDeEspera);
                procuraSala(emissor);
                break;
            
            case JOGADA:
                Object parametros = mensagem.getParametro();
                emissor.getSala().jogar(emissor, parametros);
                break;
            
            case ABANDONO:
                Sala sala = emissor.getSala();
                if (sala != null) {
                    sala.abandonar(emissor);
                }
                salaDeEspera.remove(emissor);
                break;
                
            default:
                System.out.println("Ação inválida.");
                break;

                
        }
    }
    
    /**Método que converte socket em string.
     * 
     * @param socket Socket a ser convertido.
     * @return String no formato IP:PORTA.
     */
    private String socketString (Socket socket) {
        String r = socket.getLocalAddress() + ":" + socket.getPort();
        return r;
    }
}
