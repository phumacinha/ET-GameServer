package cliente;

import MensagemSocket.Acao;
import MensagemSocket.MensagemParaCliente;
import MensagemSocket.MensagemParaServidor;
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import salas.Sala;
import servidor.Servidor;

/**Classe para objetos do tipo Cliente, onde serão contidos atributos e métodos
 * para o mesmo.
 * @author Pedro Antônio de Souza
 */

public class Cliente extends Thread {
    //Socket de comunicação com o cliente.
    private final Socket socket;
    //Cria um canal para receber os dados
    private final ObjectInputStream in;
    //Cria um canal para enviar os dados
    private final ObjectOutputStream out;
    //Apontamento para o Servidor
    private final Servidor server;
    //Sala de jogo em que o cliente está alocado.
    private Sala sala;

    /**Construtor da classe.
     * @param socket Socket de comunicação com o cliente.
     * @param server Objeto Servidor que o cliente pertence.
     * @throws IOException 
     */
    public Cliente(Socket socket, Servidor server) throws IOException {
        this.socket = socket;
        this.server = server;
        //Cria um canal para receber os dados
        in = new ObjectInputStream(socket.getInputStream());
        //Cria um canal para enviar os dados
        out = new ObjectOutputStream(socket.getOutputStream());
    }
    
    /**Getter do atributo socket.
     * 
     * @return Objeto Socket do objeto atual.
     */
    public Socket getSocket() {
        return socket;
    }
    
    /**Getter do atributo sala.
     * 
     * @return Objeto Sala.
     */
    public Sala getSala() {
        return sala;
    }
    
    /**Setter do atributo sala.
     * 
     * @param sala Objeto sala que será atribuido ao cliente.
     */
    public void setSala (Sala sala) {
        this.sala = sala;
    }
    
    /**Verifica equidade entre o socket do cliente e outro socket.
     * 
     * @param outroSocket Socket a ser comparado com o do cliente.
     * @return True se forem os sockets forem iguais e False caso contrário.
     */
    public boolean ehIgual(Socket outroSocket) {
        return socket.equals(outroSocket);
    }

    /**Envia mensagem para o cliente.
     * 
     * @param mensagem Objeto do tipo MensagemParaCliente a ser enviada.
     * @throws IOException 
     */
    public void enviaMensagem(MensagemParaCliente mensagem) throws IOException {
        System.out.println("> Enviando mensagem ["+mensagem+"]\n");
        out.writeObject(mensagem);
    }

    /**Desconeta o cliente do servidor, ou seja, fecha seu socket.
     * 
     * @throws IOException 
     */
    public void desconectaCliente() throws IOException {
        //Desconecta o Cliente
        socket.close();
    }

    /** Método sobrecarregado da superclasse Thread para rodar um looping
     * infinito para escutar mensagens recebidas *do* cliente.
     */
    @Override
    public void run() {
        // Bloco try se faz necessário pois caso o cliente seja desconectado,
        // uma exceção será lançada.
        try {
            // Analisa mensagens recebidas
            while (true) {
                // Converte e armazena o objeto recebido do cliente.
                MensagemParaServidor mensagem = (MensagemParaServidor) in.readObject();
                // Atualiza o remetente.
                mensagem.setRemetente(this);
                // Imprime mensagem no console.
                System.out.println("> Recebendo mensagem ["+mensagem+"]\n");
                // Envia mensagem para o servidor tratar.
                server.trataMensagem(mensagem);
            }
        }
        
       // Cliente está desconectado
        catch (IOException | ClassNotFoundException e) {
            try {
                // Imprime que o socket foi fecahdo.
                System.out.println("> Cliente saiu do servidor ["+socket+"]\n");
                // Envia mensagem de Acao.ABANDONO para o servidor, com esse
                // cliente como remetente.
                server.trataMensagem(new MensagemParaServidor(this, Acao.ABANDONO));
                // Remove esse cliente da lista de clientes do servidor.
                server.removeCliente(this);
                // Fecha canais de comunicação.
                socket.shutdownInput();
                socket.shutdownOutput();
                // Fecha Socket.
                socket.close();
            } catch (IOException ex) {
                // Caso haja erro, imprime no console.
                System.out.println("> Erro ao fechar socket ["+socket+"]\n");
            }
        }
    }
}
