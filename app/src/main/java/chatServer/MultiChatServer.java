package chatServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiChatServer {
    private static final int PORT = 20111;

    private List<ClientInfo> mClientList;        // Client List
    private ServerSocket mServerSocket;          // Server Socket

    public MultiChatServer() {
        mClientList = Collections.synchronizedList(new ArrayList<ClientInfo>());
    }

    public void start() {
        Socket socket;

        try {
            mServerSocket = new ServerSocket(PORT);
            System.out.println("Server Start");

            while (true) {
                socket = mServerSocket.accept();
                System.out.println("[ACCESS INFO] Client IP : " + socket.getInetAddress());

                new ServerReciver(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addClient(ClientInfo client) {
        mClientList.add(client);
        sendToAll("Hello, " + client.getNickName() + ". " + mClientList.size() + " people/person remains now.");
    }

    private void removeClient(ClientInfo client) {
        mClientList.remove(client);
        sendToAll("BYE, " + client.getNickName() + ". " + mClientList.size() + " people/person remains now.");
    }

    private void sendToAll(String message) {
        System.out.println(message);

        // Handle multi thread
        synchronized (mClientList) {
            //for (int i = 0; i < mClientList.size(); i++)
            for (ClientInfo client : mClientList) {
                try {
                    client.getOutput().writeUTF(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ServerReciver extends Thread {
        private DataInputStream mInputStream;
        private DataOutputStream mOutputStream;

        private ClientInfo mClientInfo;

        public ServerReciver(Socket socket) {
            try {
                mInputStream = new DataInputStream(socket.getInputStream());
                mOutputStream = new DataOutputStream(socket.getOutputStream());

                String nickName = mInputStream.readUTF();

                mClientInfo = new ClientInfo(nickName, mOutputStream);

                addClient(mClientInfo);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // Always Listen
                while (mInputStream != null) {
                    sendToAll(mInputStream.readUTF());
                }
            } catch (IOException e) {

            } finally {
                // Connection out
                removeClient(mClientInfo);
            }
        }
    }
}
