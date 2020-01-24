import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.PrintStream;
import java.io.IOException;
import java.io.EOFException;
import java.lang.NullPointerException;
import java.lang.Thread;
import java.util.Vector;
import java.util.Iterator;

public class Servidor {
    private ServerSocket servidor; // socket do servidor
    private Socket conexao; // conexão com o cliente
    private int porta;
    private int contador = 1; // contador do número de conexões
    private Vector<TratarCliente> clientes = new Vector<TratarCliente>();

    public Servidor (int porta)
    {
        this.porta = porta;
    }

    public void rodarServidor()
    {
        try 
        {
            servidor = new ServerSocket(porta,100); // passo 1

            while (true)
            {
                try
                {
                    esperarPorConexao(); // passo 2
                    tratarConexao(); // passos 3 e 4 multithread
                }
                catch (EOFException eofException)
                {
                    System.out.println("\nServidor terminou conexão");
                }
            }
        }
        catch (IOException ioException)
        {   
            System.out.println("Erro ao criar servidor. Porta em uso.");
            ioException.printStackTrace();
        }
    }

    private void esperarPorConexao() throws IOException
    {
        System.out.println("Esperando por conexão...");
        conexao = servidor.accept();
        System.out.println("Conexão " + this.contador + " recebida de "
            + conexao.getInetAddress().getHostName());       
    }

    private void tratarConexao() throws IOException 
    {
        TratarCliente novoCliente = new TratarCliente(conexao, this.contador, this);
        clientes.add(novoCliente);
        Thread t = new Thread(novoCliente);
        t.start();

        this.contador++;
    }

    public void broadcastMensagem(String mensagem, TratarCliente origem)
    {
        TratarCliente clienteAux;
        Iterator<TratarCliente> iterator = this.clientes.iterator();
        
        while(iterator.hasNext())
        {
            clienteAux = iterator.next();
            if (clienteAux.getId() != origem.getId())
            {
                clienteAux.enviarDados(mensagem);
            }
        }
    }

    public void enviarParticipantes(TratarCliente destino)
    {
        TratarCliente clienteAux;
        Iterator<TratarCliente> iterator = this.clientes.iterator();

        String mensagem = "Estão participando desta conversa: ";
        
        while(iterator.hasNext())
        {
            clienteAux = iterator.next();
            mensagem = mensagem + clienteAux.getNome() + ", ";
        }

        mensagem = mensagem.substring(0, mensagem.length() - 2);

        destino.enviarDados(mensagem);
        
    }

    public void removerCliente (int id)
    {
        TratarCliente clienteAux;
        Iterator<TratarCliente> iterator = this.clientes.iterator();
        
        while(iterator.hasNext())
        {
            clienteAux = iterator.next();
            if (clienteAux.getId() == id)
            {
                iterator.remove();
            }
        }
    }

}
