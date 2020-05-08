/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author raphael
 */
public class Cliente extends Thread {

    private Socket socket;
    //Cria um canal para receber os dados
    private DataInputStream in;
    //Cria um canal para enviar os dados
    private DataOutputStream out;
    //Apontamento para o Servidor
    private Servidor main;

    public Cliente(Socket socket, Servidor main) throws IOException {
        this.socket = socket;
        this.main = main;
        //Cria um canal para receber os dados
        in = new DataInputStream(socket.getInputStream());
        //Cria um canal para enviar os dados
        out = new DataOutputStream(socket.getOutputStream());
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public boolean ehIgual(Socket outroSocket) {
        return socket.equals(outroSocket);
    }

    public void enviaMensagem(String mensagem) throws IOException {
        System.out.println("Enviando mensagem:["+mensagem+"]");
        out.writeUTF(mensagem);
    }

    public void desconectaCliente() throws IOException {
        //Desconecta o Cliente
        socket.close();
    }

    @Override
    public void run() {
        try {    
            while (true) {
                    String mensagem = in.readUTF();
                    System.out.println("Recebendo mensagem:["+mensagem+"]");
                    main.transmiteMensagem(mensagem, socket);
            }
        }
        catch (IOException e) {
            try {
                System.out.println("-> Socket fechado: "+socket.toString());
                main.removeCliente(this);

                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            } catch (IOException ex) {

            }
        }
    }
}
