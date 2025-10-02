import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    private final int port;
    private final QuestionsDB questionsDB;
    private final List<String> peers = new ArrayList<>(); 

    public Server(int port) {
        this.port = port;
        this.questionsDB = new QuestionsDB();
    }

    // create a thread with a socket to the server
    public void start() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("[SERVER] Server iniciado na port " + port + ". Aguardando conexões...");
                // accept outside connections
                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("[SERVER] Nova conexão recebida de " + socket.getInetAddress());
                    new Thread(new Peer(socket, questionsDB)).start();
                }
            } catch (IOException e) {
                System.err.println("[ERRO] Erro ao iniciar o server: " + e.getMessage());
            }
        }).start();

        startConsoleAdmin();
    }

    // choose the action to do
    private void startConsoleAdmin() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Console de Administração do Server ---");
        System.out.println("Comandos disponíveis:");
        System.out.println("  addpeer <host>:<port>  - Adiciona um novo server peer.");
        System.out.println("  sync <host>:<port>     - Sincroniza as perguntas de um peer.");
        System.out.println("  list                      - Lista as perguntas atuais.");
        System.out.println("  exit                      - Desliga o server.");
        System.out.println("------------------------------------------\n");

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine();
            String[] parts = command.split(" ");

            if (parts[0].equalsIgnoreCase("addpeer") && parts.length == 2) {
                peers.add(parts[1]);
                System.out.println("[ADMIN] Peer " + parts[1] + " adicionado à lista.");
            } else if (parts[0].equalsIgnoreCase("sync") && parts.length == 2) {
                synchronizePeer(parts[1]);
            } else if (parts[0].equalsIgnoreCase("list")) {
                System.out.println("[ADMIN] Listando " + questionsDB.getAllQuestions().size() + " perguntas:");
                for (Question p : questionsDB.getAllQuestions()) {
                    System.out.println("  - " + p.statement);
                }
            } else if (parts[0].equalsIgnoreCase("exit")) {
                System.out.println("[ADMIN] Desligando o server...");
                System.exit(0);
            } else {
                System.out.println("[ADMIN] Comando inválido.");
            }
        }
    }

    @SuppressWarnings("unchecked")
    // add another server to peers, so they can share questions
    private void synchronizePeer(String peerAddress) {
        try {
            String[] parts = peerAddress.split(":");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);

            System.out.println("[SYNC] Conectando ao peer " + peerAddress + " para sincronizar...");
            try (Socket socket = new Socket(host, port);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.writeObject("GET_QUESTIONS");
                out.flush();

                Object answer = in.readObject();
                if (answer instanceof List) {
                    List<Question> receivedQuestions = (List<Question>) answer;
                    System.out.println("[SYNC] Recebidas " + receivedQuestions.size() + " perguntas do peer.");
                    questionsDB.addQuestions(receivedQuestions);
                } else {
                    System.err.println("[SYNC] Resposta inesperada do peer.");
                }
            }
        } catch (IOException | ClassNotFoundException | NumberFormatException e) {
            System.err.println("[ERRO SYNC] Falha ao sincronizar com o peer " + peerAddress + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java Server <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        Server server = new Server(port);
        server.start();
    }
}

