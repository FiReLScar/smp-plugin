package smp.smp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public final class Smp extends JavaPlugin {

    private ServerSocket sock;
    private Socket client_socket;
    private PrintWriter output;
    private BufferedReader input;
    public boolean running = true;

    @Override
    public void onEnable() {
        Thread server_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sock = new ServerSocket();
                    sock.setReuseAddress(true);
                    sock.bind(new InetSocketAddress(25560));
                    while (running) {
                        client_socket = sock.accept();
                        output = new PrintWriter(client_socket.getOutputStream(), true);
                        input = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
                        String message = input.readLine();
                        if (Bukkit.getOfflinePlayer(message).isWhitelisted())
                            output.println('"' + message + "\" has already been whitelisted!");
                        else {
                            Bukkit.getOfflinePlayer(message).setWhitelisted(true);
                            output.println('"' + message + "\" has been whitelisted!");
                        }
                        input.close();
                        output.close();
                        client_socket.close();
                        Bukkit.getLogger().info(message);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        server_thread.start();
    }

    @Override
    public void onDisable() {
        running = false;
        try {
            input.close();
            output.close();
            client_socket.close();
            sock.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
