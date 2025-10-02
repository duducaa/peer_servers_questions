import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Peer implements Runnable {
    private final Socket socket;
    private final QuestionsDB questionsDB;

    public Peer(Socket socket, QuestionsDB database) {
        this.socket = socket;
        this.questionsDB = database;
    }

    @Override
    public void run() {
        // create the serializing tools
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            Object request = in.readObject();

            // get questions from the server
            if ("GET_QUESTIONS".equals(request)) {
                System.out.println("[PEER HANDLER] Requisição de sincronização recebida. Enviando perguntas...");
                out.writeObject(questionsDB.getAllQuestions());
                out.flush();
            // start the game and create client
            } else if ("PLAY_GAME".equals(request)) {
                System.out.println("[PEER HANDLER] Requisição de jogo recebida. Iniciando jogo...");
                new Client(socket, questionsDB, in, out).play();
            } else {
                System.out.println("[PEER HANDLER] Requisição desconhecida: " + request);
            }

        } catch (IOException | ClassNotFoundException e) {
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}

