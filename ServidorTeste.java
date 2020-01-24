import java.util.Scanner;

public class ServidorTeste
{
    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        System.out.println("Entre com o número da porta para o servidor: ");
        int porta = teclado.nextInt();

        Servidor aplicacao = new Servidor(porta);
        aplicacao.rodarServidor();
    }
}