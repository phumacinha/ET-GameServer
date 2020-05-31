package cliente;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import servidor.Sala;
import servidor.Servidor;

/**
 *
 * @author raphael
 */
public class Cliente extends Thread {

    private final Socket socket;
    //Cria um canal para receber os dados
    private final ObjectInputStream in;
    //Cria um canal para enviar os dados
    private final ObjectOutputStream out;
    //Apontamento para o Servidor
    private final Servidor server;
    
    private Sala sala;

    public Cliente(Socket socket, Servidor server) throws IOException {
        this.socket = socket;
        this.server = server;
        //Cria um canal para receber os dados
        in = new ObjectInputStream(socket.getInputStream());
        //Cria um canal para enviar os dados
        out = new ObjectOutputStream(socket.getOutputStream());
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public Sala getSala() {
        return sala;
    }
    
    public void setSala (Sala sala) {
        this.sala = sala;
    }
    
    public boolean ehIgual(Socket outroSocket) {
        return socket.equals(outroSocket);
    }

    public void enviaMensagem(Object mensagem) throws IOException {
        System.out.println("Enviando mensagem:["+mensagem.toString()+"]");
        out.writeObject(mensagem);
    }

    public void desconectaCliente() throws IOException {
        //Desconecta o Cliente
        socket.close();
    }

    @Override
    public void run() {
        try {    
            while (true) {
                    Object mensagem = in.readObject();
                    System.out.println("Recebendo mensagem:["+mensagem+"]");
                    server.transmiteMensagem(mensagem, this, sala.getJogadores());
            }
        }
        catch (Exception e) {
            try {
                System.out.println("-> Socket fechado: "+socket.toString());
                server.removeCliente(this);

                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            } catch (IOException ex) {

            }
        }
    }
}
