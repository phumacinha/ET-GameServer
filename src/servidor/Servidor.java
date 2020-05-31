package servidor;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import cliente.Cliente;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import jogos.Jogo;
import jogos.JogoDaVelha;

/**
 *
 * @author raphael
 */
public class Servidor {

    private final List clientes;
    private final List salaDeEspera;
    private ServerSocket serverSocket;

    public Servidor() throws IOException {
        serverSocket = new ServerSocket(4545);
        
        clientes = new ArrayList();
        salaDeEspera = new ArrayList();
        
        iniciaServidor();
    }
    
    private void iniciaServidor () {
        //O serviço fica indefinidamente recebendo conexões
        while (true) {
            try{
                //Espera por conexões de clientes
                System.out.println("Esperando conexões ...");
                Socket socket = serverSocket.accept();
                System.out.println("Recebendo conexão de "+socket.toString());
                
                Cliente novoCliente = new Cliente(socket, this);
                clientes.add(novoCliente);
                novoCliente.start();
                procuraSala(novoCliente);
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
    private void procuraSala (Cliente cliente) throws IOException {
        cliente.enviaMensagem("procurando-sala");
        
        if (salaDeEspera.size() > 0) {
            Cliente adversario = (Cliente) salaDeEspera.get(0);
            salaDeEspera.remove(0);
            
            Sala sala = adversario.getSala();
            cliente.setSala(sala);
            sala.inserirCliente(cliente);
            
            transmiteMensagem("sala-encontrada", sala.getJogadores());
        }
        else {
            Jogo jogo = new JogoDaVelha();
            Sala sala = new Sala(jogo);
            
            cliente.setSala(sala);
            sala.inserirCliente(cliente);
            
            salaDeEspera.add(cliente);
        }
        
    }
    
    /**
     * Transmite mensagem de um cliente emissor para outro cliente receptor.
     * @param mensagem Mensagem a ser enviada.
     * @param emissor Cliente que está enviando a mensagem.
     * @param receptor Clientes que devem receber a mensagem.
     */
    public void transmiteMensagem(Object mensagem, Cliente emissor, Cliente receptor) {
        if (!receptor.equals(emissor)) {
            try { receptor.enviaMensagem(mensagem); }        
            catch (IOException ioe) {
                System.out.println("Exceção ao transmitir mensagem.");
            }
        }
    }
    
    /**
     * Transmite mensagem de um emissor para um ou mais receptores diferentes do emissor.
     * @param mensagem Mensagem a ser enviada.
     * @param emissor Cliente que está enviando a mensagem.
     * @param receptores Clientes que devem receber a mensagem.
     */
    public void transmiteMensagem(Object mensagem, Cliente emissor, List<Cliente> receptores) {
        receptores.forEach((receptor) -> {
            transmiteMensagem(mensagem, emissor, receptor);
        });
    }
    
    /**
     * Transmite mensagem para um conjunto de clientes.
     * @param mensagem Mensagem a ser enviada.
     * @param receptores Clientes que devem receber a mensagem.
     */
    public void transmiteMensagem(Object mensagem, List<Cliente> receptores) {
        transmiteMensagem(mensagem, null, receptores);
    }
    
    /**
     * Transmite mensagem para todos os clientes.
     * @param mensagem Mensagem a ser enviada.
     */
    public void transmiteMensagem(Object mensagem) {
        transmiteMensagem(mensagem, clientes);
    }
    
    public void removeCliente (Cliente cliente) {
        transmiteMensagem("adversario-abandonou", cliente, cliente.getSala().getJogadores());
        cliente.getSala().removeCliente(cliente);
        salaDeEspera.remove(cliente);
        clientes.remove(cliente);
    }
    
    private String socketString (Socket socket) {
        String r = socket.getLocalAddress() + ":" + socket.getPort();
        return r;
    }
}
