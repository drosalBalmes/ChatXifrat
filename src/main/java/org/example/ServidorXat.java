package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServidorXat {
    private int port;
    private List<GestorClient> clients;

    public ServidorXat(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public List<GestorClient> getClients() {
        return clients;
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Servidor de xat iniciat al port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                GestorClient gestorClient = new GestorClient(this, socket);
                clients.add(gestorClient);
                Thread thread = new Thread(gestorClient);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Error en iniciar el servidor: " + e.getMessage());
        }
    }

    public void broadcast(String missatge, GestorClient sender) {
        for (GestorClient client : clients) {
            if (client != sender) {
                client.enviaMissatge(missatge);
            }
        }
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat(3060);
        servidor.start();
    }
}