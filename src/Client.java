import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args){
        try {
            Scanner sc = new Scanner(System.in);

            //get ip address from the local host
            InetAddress ip = InetAddress.getByName("localhost");
            //pick a port
            Socket s = new Socket(ip,2345);

            //get info with input and output stream
            DataInputStream dis= new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            //log successful communication to server
            System.out.println("Communicating with " + s + '\n');

            //user official starts when they enter a name
            System.out.println("Enter client name: ");
            String name = sc.nextLine();
            System.out.println();

            //write name to server
            dos.writeUTF(name);

            while(true) {
                //prompt user to enter an equation
                System.out.println("Type the equation in the order: NUMBER OPERATOR NUMBER... (Type 'Exit' end the connection)");
                String toSend=sc.nextLine();
                dos.writeUTF(toSend);

                //if user types Exit, log the exit in client
                if(toSend.equals("Exit")) { //if user sends exit close connection
                    s.close();
                    System.out.println("Connection Closed");
                    break;
                }
                //send to client side from server
                String ans = dis.readUTF();
                System.out.println("Answer: "+ ans);
                System.out.println();
            }
            sc.close();
            dis.close();
            dos.close();
        }
        catch(Exception e) {
            System.out.println("Client Error"+e);
        }
    }
}