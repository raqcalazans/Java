import java.util.Scanner;

public class ClienteTeste
{
    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        System.out.println("Entre com o endereço do servidor: ");
        String host = teclado.nextLine();
        System.out.println("Entre com o número da porta para o servidor: ");
        int porta = teclado.nextInt();

        Cliente aplicacao = new Cliente(host, porta);
        aplicacao.rodarCliente();
    }
}