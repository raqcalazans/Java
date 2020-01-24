import java.net.Socket;
import java.net.InetAddress;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.PrintStream;
import java.io.IOException;
import java.io.EOFException;
import java.lang.Thread;

public class Cliente
{
    private Socket cliente;
    private final String chatServidor;
    private final int porta;
    private Scanner entrada;
    private PrintStream saida;

    public Cliente(final String host, final int porta) {
        this.chatServidor = host;
        this.porta = porta;
    }

    public void rodarCliente() {
        try // tentar conectar-se ao servidor
        {
            conectarAoServidor(); // passo 1
            getStreams(); // passo 2
            processarConexao(); // passo 3
        } catch (final EOFException eofException) {
            System.out.println("\nCliente terminou conexão.");
        } catch (final IOException ioException) {
            ioException.printStackTrace();
        } finally {
            fecharConexao();
        }
    }

    private void conectarAoServidor() throws IOException {
        System.out.println("Tentando conexão com servidor...");
        cliente = new Socket(InetAddress.getByName(this.chatServidor), this.porta);

        System.out.println("Conectado ao servidor: " + cliente.getInetAddress().getHostName());
    }

    private void getStreams() throws IOException 
    {
        entrada = new Scanner(cliente.getInputStream());
        saida = new PrintStream(cliente.getOutputStream());
    }

    private void processarConexao() throws IOException 
    {
        String mensagem = "";

        mensagem = entrada.nextLine();
        System.out.println(mensagem);

        DespachoTeclado despacho = new DespachoTeclado(this);
        Thread t = new Thread(despacho);
        t.start();

        do
        {
            //enviarDados(teclado.nextLine());
            try
            {
                mensagem = entrada.nextLine();
                System.out.println(mensagem);
            }
            catch (NoSuchElementException e)
            {
                System.out.println("\nServidor terminou conexão");
                mensagem = "SERVIDOR>>> FECHAR";
            }
            

        } while (!mensagem.equals("SERVIDOR>>> FECHAR"));

        despacho.close();
    }

    private void fecharConexao() {
        System.out.println("Terminando conexão.");

        try 
        {
            entrada.close();
            saida.close();
            cliente.close();
        } 
        catch (final IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void enviarDados(final String mensagem)
    {
        saida.println("CLIENTE>>> " + mensagem);
        //System.out.println("CLIENTE>>> " + mensagem);
    }


}