import java.lang.Runnable;
import java.io.PrintStream;
import java.util.Scanner;
import java.lang.Runnable;

public class DespachoTeclado implements Runnable
{
    private Cliente cliente;
    private Scanner teclado = new Scanner(System.in);
    private boolean fechar = false;

    public DespachoTeclado(Cliente cliente)
    {
        this.cliente = cliente;
    }

    public void run ()
    {
        while(!this.fechar)
        {
            this.cliente.enviarDados(teclado.nextLine());
        }
    }

    public void close ()
    {
        this.fechar = true;
    }

}