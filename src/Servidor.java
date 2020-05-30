/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author raphael
 */
public class Servidor {

    private final List clientes;
    private ServerSocket serverSocket;

    public Servidor() throws IOException {
        //Criando Lista de Clientes
        clientes = new ArrayList();
        
        //Cria um Socket na porta 4545
        serverSocket = new ServerSocket(4545);
        
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
                
                if (clientes.size() < 2) {
                    Cliente novoCliente = new Cliente(socket, this);
                    clientes.add(novoCliente);
                    novoCliente.start();
                    
                    novoCliente.enviaMensagem("procurando_adversario");
                    
                    if (clientes.size() == 2) {
                        Thread.sleep(1000);
                        transmiteMensagem("adversario_encontrado", socket);
                        novoCliente.enviaMensagem("adversario_encontrado");
                    }
                }
                else {
                    socket.close();
                }
                
            }
            catch (IOException | InterruptedException ex) {
                System.out.println("Socket fechado");
            }
        }
    }

    public void recebeMensagem(String mensagem) throws IOException {
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = (Cliente)clientes.get(i);
            cliente.enviaMensagem(mensagem);
        }
    }
    
    public void transmiteMensagemGeral(String mensagem) {
        transmiteMensagem(mensagem, null);
    }
    
    public void transmiteMensagem(String mensagem, Socket socket) {
        try {
            for (int i = 0; i < clientes.size(); i++) {
                Cliente cliente = (Cliente)clientes.get(i);
                if (!cliente.getSocket().isClosed()) System.out.println("/-/-/-/-/-/- Socket fechado: "+ socketString(cliente.getSocket()));
                if (!cliente.ehIgual(socket)) {
                    cliente.enviaMensagem(mensagem);
                    System.out.println("Enviando msg de "+ socketString(socket) +" para "+socketString(cliente.getSocket()));
                }
                else
                    System.out.println("NÃO enviando msg de "+ socketString(socket) +" para "+socketString(cliente.getSocket()));
                
            }
        }        
        catch (IOException ioe) {
            
        }
    }
    
    public void removeCliente (Cliente cliente) {
        clientes.remove(cliente);
    }
    
    private String socketString (Socket socket) {
        String r = socket.getLocalAddress() + ":" + socket.getPort();
        return r;
    }
}
