import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.Date;

public class Server {
    public static void main(String[] args) throws IOException {

        //open socket
        ServerSocket welcomeSocket = new ServerSocket(2345);
        System.out.println("ServerSocket: " + welcomeSocket);

        //number of threads check
        //int usr = 1;
        while (true) {
            Socket socket = null;
            try {
                //connect to user ip
                socket = welcomeSocket.accept();
                System.out.println("A new Client is connected! \n");

                //communicate with input and output stream
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                //thread error chek
                //System.out.println("Assigning thread " + usr);
                //usr++;

                //pass new thread with class
                Thread t = new MultipleClients(socket, dis, dos);
                t.start();
            } catch (Exception e) {
                socket.close();
                System.out.println(e);
            }
        }
    }
}

//class to handle clients
class MultipleClients extends Thread {
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket socket;

    private static long start, stop;

    //method to start timer
    static void StartTimer() {
        start = System.currentTimeMillis();
    }

    //method to stop timer
    static void StopTimer() {
        stop = System.currentTimeMillis();
    }

    //method to calculate elapsed time
    static void Elapsed(){
        long total = (stop - start) / 1000;
        System.out.println("Elapsed time: " + total);
    }

    //method to calculate current time when client is connected
    static void ConnectionTime(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        System.out.println("Current time: " + formatter.format(date));
    }

    public MultipleClients(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.socket = s;
        this.dis = dis;
        this.dos = dos;
    }

    public void run() {
        String input, name;

        StartTimer();
        try {
            //get name from client input
            name = dis.readUTF();
            //print official connection time after user has entered preferred client name
            ConnectionTime();
            //log it in server
            System.out.println("Connection was made with client: " + name + '\n');

            while (true) {
                try {
                    //loop to ask user for equation each time
                    input = dis.readUTF();

                    //if user types Exit, log it in the server and close the socket
                    if (input.equals("Exit")) {
                        System.out.println("Client " + name + " sends exit...");
                        System.out.println("Closing the connection...\n");

                        StopTimer();
                        Elapsed();
                        this.socket.close();
                        System.out.println("Connection Closed...\n");
                        break;
                    }
                    //if equation received from client, print log
                    System.out.println("From " + name);
                    System.out.println("Request from client: " + input);
                    int result;

                    //break equation in to numbers and operator and give variables
                    StringTokenizer st = new StringTokenizer(input);

                    int num1 = Integer.parseInt(st.nextToken());
                    String operator = st.nextToken();
                    int num2 = Integer.parseInt(st.nextToken());

                    //calculate result depending on which operator user picked
                    result = switch (operator) {
                        case "+" -> num1 + num2;
                        case "-" -> num1 - num2;
                        case "*" -> num1 * num2;
                        case "/" -> num1 / num2;
                        default -> 0;
                    };

                    //send result to client
                    System.out.println("Sending result...\n");
                    dos.writeUTF(Integer.toString(result));

                } catch (Exception FormatException) {
                    dos.writeUTF("INCORRECT FORMAT!!!");
                    System.out.println("FORMAT ERROR... \n");
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}