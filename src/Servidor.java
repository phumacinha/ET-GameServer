/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author raphael
 */
public class Servidor {

    private final List clientes;

    public Servidor() throws IOException {
        //Criando Lista de Clientes
        clientes = new ArrayList();
        //Cria um Socket na porta 4545
        ServerSocket serverSocket = new ServerSocket(4545);
        //O serviço fica indefinidamente recebendo conexões
        while (true) {
            //Espera por conexões de clientes
            System.out.println("Esperando conexões ...");
            Socket socket = serverSocket.accept();
            System.out.println("Recebendo conexão de "+socket.toString());
            Cliente novoCliente = new Cliente(socket, this);
            clientes.add(novoCliente);
            novoCliente.start();
        }
    }

    public void recebeMensagem(String mensagem) throws IOException {
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = (Cliente)clientes.get(i);
            cliente.enviaMensagem(mensagem);
        }
    }
    
    public void transmiteMensagem(String mensagem, Socket socket) {
        try {
            for (int i = 0; i < clientes.size(); i++) {
                Cliente cliente = (Cliente)clientes.get(i);
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
    
    public void removeCliente (Socket cliente) {
        clientes.remove(cliente);
    }
    
    private String socketString (Socket socket) {
        String r = socket.getLocalAddress() + ":" + socket.getPort();
        return r;
    }
}
