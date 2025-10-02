import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Terminal {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Uso: java ClienteTerminal <host_servidor> <porta_servidor>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        // try to create a new connection
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            out.writeObject("PLAY_GAME");
            out.flush();

            System.out.println(in.readObject()); 

            while (true) {
                Object received = in.readObject();

                System.out.println(received);

                if (received instanceof Question) {
                    // if receives a question, shows it and wait for the answer
                    Question question = (Question) received;
                    System.out.println("\n---------------------------------");
                    System.out.println("PERGUNTA: " + question.statement);
                    for (String option : question.options) {
                        System.out.println(option);
                    }
                    System.out.print("Sua resposta (1-4): ");
                    int answer = scanner.nextInt() - 1;
                    scanner.nextLine(); 

                    out.writeObject(answer);
                    out.flush();

                    System.out.println("Servidor: " + in.readObject()); 
                    System.out.println("Servidor: " + in.readObject());

                    System.out.print("> ");
                    String keepGoing = scanner.nextLine();
                    out.writeObject(keepGoing);
                    out.flush();

                } else if ("FIM".equals(received)) {
                    // if its the end, end the socket and the connection  
                    System.out.println("Obrigado por jogar!");
                    break;
                } else {
                    System.out.println("Servidor: " + received);
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("Erro de conexão com o servidor: " + e.getMessage());
        }
    }
}

