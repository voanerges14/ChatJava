package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Pavlo on 14-08,Aug-16.
 */
public class Client {
    private BufferedReader in;
    private PrintWriter out;
    Socket socket;

    public Client(){
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter IP ");
        System.out.println("format: xxxx.xxxx.xxxx.xxxx");

        String ip = scan.nextLine();

        try {

            socket = new Socket(ip, 8283);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Enter your nick");
            out.println(scan.nextLine());

      //запуск виводу всіх вхідних повідомлень на консоль

        Resender resender = new Resender();
            resender.start();





            while (true){
                out.println(scan.nextLine());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            closeStream();
        }

    }

    private void closeStream(){
        try {
            in.close();
            out.close();
            socket.close();
        }catch (Exception e){
            System.err.println("Streams do not closed");
        }
    }

    /**
     * Клас пересилає повідомлення з сервака в консоль і пахатиме поки не викличуть setClose()
     */

    private class Resender extends Thread{

        private boolean stoped;

        //Завершує пересилання
        public void setStop(){
            stoped =true;
        }

        @Override
        public void run(){
            try {
                while (!stoped){
                    System.out.println(in.readLine());
                }
            }catch (IOException e){
                System.err.println("Error to try catch a message");
                e.printStackTrace();
            }
        }
    }
}
