package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GestorClient implements Runnable {
    private ServidorXat servidor;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String nomClient;

    public GestorClient(ServidorXat servidor, Socket socket) {
        this.servidor = servidor;
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Demanem el nom del client i el guardem
            nomClient = in.readLine();
            System.out.println("S'ha connectat el client " + nomClient);

            // Enviem el nom del client a tots els altres clients
            servidor.broadcast(nomClient + " s'ha connectat", this);

            // Llegim els missatges que arriben del client i els enviem a tots els altres clients
            String missatge;
            while ((missatge = in.readLine()) != null) {
                System.out.println(nomClient + " (cifrat): " + missatge);

                // Desencriptar el mensaje usando ROT13
                String missatgeDesxifrat = desxifrarRot13(missatge);
                System.out.println(nomClient + " (desxifrat): " + missatgeDesxifrat);

                // Enviar el mensaje cifrado a todos los demás clientes
                servidor.broadcast(missatge, this);

                if (missatgeDesxifrat.equals("Sortir")) {
                    break;
                }
            }

            // Tanquem les connexions i enviem el missatge de desconnexió als altres clients
            socket.close();
            in.close();
            out.close();
            System.out.println("El client " + nomClient + " s'ha desconnectat");
            servidor.broadcast(nomClient + " s'ha desconnectat", this);

        } catch (IOException e) {
            System.err.println("Error en gestionar el client: " + e.getMessage());
        }
    }

    public void enviaMissatge(String missatge) {
        out.println(missatge);
    }

    private String desxifrarRot13(String missatge) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < missatge.length(); i++) {
            char c = missatge.charAt(i);
            if (c >= 'a' && c <= 'z') {
                c = (char) (((c - 'a' + 13) % 26) + 'a');
            } else if (c >= 'A' && c <= 'Z') {
                c = (char) (((c - 'A' + 13) % 26) + 'A');
            }
            sb.append(c);
        }
        return sb.toString();
    }
}