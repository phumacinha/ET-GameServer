package cliente;

import comunicacao.mensagens.Acao;
import comunicacao.mensagens.MensagemParaCliente;
import comunicacao.mensagens.MensagemParaServidor;
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;
import salas.Sala;
import servidor.Servidor;

/**Classe para objetos do tipo Cliente, onde serão contidos atributos e métodos
 * para o mesmo.
 * @author Pedro Antônio de Souza
 */

public class Cliente extends Thread {

    //Socket de comunicação com o cliente.
    private final Socket SOCKET;
    //Cria um canal para receber os dados
    private final ObjectInputStream IN;
    //Cria um canal para enviar os dados
    private final ObjectOutputStream OUT;
    //Apontamento para o Servidor
    private final Servidor SERVER;
    //Sala de jogo em que o cliente está alocado.
    private Sala sala;
    
    /**Construtor da classe.
     * @param socket Socket de comunicação com o cliente.
     * @param server Objeto Servidor que o cliente pertence.
     * @throws IOException 
     */
    public Cliente(Socket socket, Servidor server) throws IOException {
        SOCKET = socket;
        SERVER = server;
        //Cria um canal para receber os dados
        IN = new ObjectInputStream(socket.getInputStream());
        //Cria um canal para enviar os dados
        OUT = new ObjectOutputStream(socket.getOutputStream());
    }
    
    /**Getter do atributo socket.
     * 
     * @return Objeto Socket do objeto atual.
     */
    public Socket getSocket() {
        return SOCKET;
    }
    
    /**Getter do atributo sala.
     * 
     * @return Objeto Sala.
     */
    public Sala getSala() {
        return this.sala;
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
        return SOCKET.equals(outroSocket);
    }

    /**Mensagem a ser receptada pelo cliente.
     * 
     * @param mensagem Objeto do tipo MensagemParaCliente receptado.
     * @throws IOException 
     */
    public void receptaMensagem(MensagemParaCliente mensagem) throws IOException {
        System.out.println("[CLIENTE/" + SOCKET.getLocalAddress() + ":" +
                            SOCKET.getLocalPort() + 
                            "] > Recebendo mensagem ["+mensagem+"]\n");
        OUT.writeObject(mensagem);
    }

    /**Desconeta o cliente do servidor, ou seja, fecha seu socket.
     * 
     * @throws IOException 
     */
    public void desconectaCliente() throws IOException {
        //Desconecta o Cliente
        SOCKET.close();
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
        final Cliente other = (Cliente) obj;
        if (!Objects.equals(this.SOCKET, other.SOCKET)) {
            return false;
        }
        if (!Objects.equals(this.SERVER, other.SERVER)) {
            return false;
        }
        if (!Objects.equals(this.sala, other.sala)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.SOCKET);
        hash = 47 * hash + Objects.hashCode(this.SERVER);
        hash = 47 * hash + Objects.hashCode(this.sala);
        return hash;
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
                MensagemParaServidor mensagem = (MensagemParaServidor) IN.readObject();
                // Atualiza o remetente.
                mensagem.setRemetente(this);
                // Imprime mensagem no console.
                System.out.println("[SERVIDOR] > Recebendo mensagem ["+mensagem+"]\n");
                // Envia mensagem para o servidor tratar.
                SERVER.trataMensagem(mensagem);
            }
        }
        
       // Cliente está desconectado
        catch (IOException | ClassNotFoundException e) {
            try {
                // Imprime que o socket foi fecahdo.
                System.out.println("> Cliente saiu do servidor ["+SOCKET+"]\n");
                // Envia mensagem de Acao.ABANDONO para o servidor, com esse
                // cliente como remetente.
                SERVER.trataMensagem(new MensagemParaServidor(this, Acao.ABANDONO));
                // Remove esse cliente da lista de clientes do servidor.
                SERVER.removeCliente(this);
                // Fecha canais de comunicação.
                SOCKET.shutdownInput();
                SOCKET.shutdownOutput();
                // Fecha Socket.
                SOCKET.close();
            } catch (IOException ex) {
                // Caso haja erro, imprime no console.
                System.out.println("> Erro ao fechar socket ["+SOCKET+"]\n");
            }
        }
    }
}
