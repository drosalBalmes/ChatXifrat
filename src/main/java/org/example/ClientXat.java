package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientXat {
    private final String nom;
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientXat(String nom, Socket socket) {
        this.nom = nom;
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNom() {
        return nom;
    }

    public void enviaMissatge(String missatge) {
        out.println(xifrarRot13(missatge));
    }

    public String repMissatge() {
        String missatge = null;
        try {
            missatge = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return missatge;
    }

    public void tanca() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String xifrarRot13(String missatge) {
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

    private String desxifrarRot13(String missatgeXifrat) {
        return xifrarRot13(missatgeXifrat); // El cifrado Rot13 es su propio descifrado
    }

    public static void main(String[] args) {
        String servidorIP = "localhost";
        int servidorPort = 3060;

        try {
            Socket socket = new Socket(servidorIP, servidorPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Introduce tu nombre de usuario: ");
            String nom = in.readLine();

            ClientXat clientXat = new ClientXat(nom, socket);

            // Thread para recibir y mostrar mensajes del servidor
            Thread threadReceptor = new Thread(() -> {
                try {
                    String missatge;
                    while ((missatge = clientXat.repMissatge()) != null) {
                        System.out.println("Mensaje cifrado: " + missatge);
                        System.out.println("Mensaje descifrado: " + clientXat.desxifrarRot13(missatge));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            threadReceptor.start();

            // Loop para enviar mensajes al servidor
            String missatge;
            while ((missatge = in.readLine()) != null) {
                clientXat.enviaMissatge(missatge);
            }

            // Cerrar conexiones
            clientXat.tanca();
            in.close();
            socket.close();

        } catch (IOException e) {
            System.err.println("Error en la comunicación con el servidor: " + e.getMessage());
        }
    }
}