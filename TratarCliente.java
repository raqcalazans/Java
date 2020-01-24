import java.lang.Runnable;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.PrintStream;
import java.io.IOException;
import java.io.EOFException;

public class TratarCliente implements Runnable
{
    private int id;
    private Socket conexao;
    private Scanner entrada;
    private PrintStream saida;
    private String nome;
    private Servidor servidor;

    public TratarCliente (Socket conexaoCliente, int idCliente, Servidor servidor)
    {
        this.conexao = conexaoCliente;
        this.id = idCliente;
        this.servidor = servidor;
    }

    public int getId ()
    {
        return this.id;
    }

    public String getNome ()
    {
        return this.nome;
    }

    public void run()
    {
        System.out.println("Thread para tratar cliente " + this.id + " iniciada.");
        
        try
        {
            getStreams(); // passo 3
            processarConexao(); // passo 4
        }
        catch (IOException ioException)
        {
            System.out.println("\nServidor terminou conexão");
        }
        finally
        {
            fecharConexao(); // passo 5
        }
    }


    private void getStreams() throws IOException
    {
        entrada = new Scanner(conexao.getInputStream());
        saida = new PrintStream(conexao.getOutputStream());
    }

    private void processarConexao() throws IOException
    {
        String mensagem = "Conexao bem-sucedida!";
        enviarDados(mensagem);

        enviarDados("Qual seu nome?");
        mensagem = entrada.nextLine();
        mensagem = mensagem.replaceFirst("CLIENTE>>> ", "");
        this.nome = mensagem;
        
        enviarDados("Bem-vindo(a), " + this.nome);
        servidor.enviarParticipantes(this);

        servidor.broadcastMensagem(this.nome + " entrou na conversa.", this);

        do
        {
            try
            {
                mensagem = entrada.nextLine();
                
                mensagem = mensagem.replaceFirst("CLIENTE>>> ", "");
                System.out.println(this.getNome() + " disse: " + mensagem);

                if (!mensagem.equals("FECHAR"))
                {
                    servidor.broadcastMensagem(this.getNome() + " disse: " + mensagem, this);
                }
                else
                {
                    servidor.broadcastMensagem(this.getNome() + " saiu da conversa.", this);
                    enviarDados("FECHAR");
                }
            }
            catch (NoSuchElementException e)
            {
                System.out.println("Cliente interrompeu conexao");
                mensagem = "FECHAR";
            }
        } while (!mensagem.equals("FECHAR"));

    }

    public void enviarDados (String mensagem)
    {
        saida.println("SERVIDOR>>> " + mensagem);
    }

    private void fecharConexao() 
    {
        System.out.println("Terminando conexão com cliente " + this.id);

        try
        {
            servidor.removerCliente(this.id);
            entrada.close();
            saida.close();
            conexao.close();
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }

}