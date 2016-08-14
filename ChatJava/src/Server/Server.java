package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Server {

    /**
     * Спеціальна конструкція ArrayLis що забезпечує доступ до масиву з різних віток
     */
    private List<ServerConnection> connections =
            Collections.synchronizedList(new ArrayList<>());
    private ServerSocket serverSocket;

    public Server(){
        try {
            serverSocket = new ServerSocket(8283);
            while (true){
                Socket socket = serverSocket.accept();
                ServerConnection con = new ServerConnection(socket);
                connections.add(con);

                //запуск
                con.start();
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }



    private class ServerConnection extends  Thread{
        private BufferedReader in;
        private PrintWriter out;
        private Socket socket;

        private String name = "";


        public ServerConnection(Socket socket){
            this.socket = socket;

            try {
                in = new BufferedReader(new InputStreamReader((socket.getInputStream())));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
                closeStreams();
            }
        }
        @Override
        public void run(){
            try {

                name = in.readLine();
                //Відправлення всім що зайшов нививй юзверь
                synchronized (connections) {
                    Iterator<ServerConnection> iter = connections.iterator();
                    while (iter.hasNext()) {
                        ((ServerConnection) iter.next()).out.println(name + " just came");
                    }
                }

                    String str = "";
                    while (true){
                        str = in.readLine();
                        if(str.equals(null)) break;

                        synchronized (connections){
                            Iterator<ServerConnection> iter = connections.iterator();
                            while (iter.hasNext()){
                                ((ServerConnection) iter.next()).out.println(name + ": " + str);
                            }
                        }
                    }

                synchronized (connections) {
                    Iterator<ServerConnection> iter = connections.iterator();
                    while (iter.hasNext()) {
                        ((ServerConnection) iter.next()).out.println(name + " has left");
                    }
                }
        } catch (IOException e) {
                e.printStackTrace();
            }finally {
                closeStreams();
            }
        }

        public void closeStreams(){
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

}
