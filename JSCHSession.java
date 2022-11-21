package com.laien.cms;

import com.jcraft.jsch.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

public class JSCHSession {

    private Session session;

    private ChannelExec channel;

    private InputStream is;
    private OutputStream os;

    public JSCHSession(String targetIp, int targetPort, String priKeyFilePath, String userName, int connectTimeout) throws JSchException {
        //jsch.setKnownHosts("/home/foo/.ssh/known_hosts");
        JSch jsch = new JSch();
        jsch.addIdentity(priKeyFilePath);
        // A Session represents a connection to a SSH server
        session = jsch.getSession(userName, targetIp, targetPort);
//        session.setPassword(password);

        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");//To skip host-key check
        session.setConfig(sshConfig);

        // this socket factory is used to create a socket to the target host,
        // and also create the streams of this socket used by us
        session.setSocketFactory(new SocketFactory() {
            @Override
            public OutputStream getOutputStream(Socket socket)
                    throws IOException {
                return socket.getOutputStream();
            }

            @Override
            public InputStream getInputStream(Socket socket) throws IOException {
                return socket.getInputStream();
            }

            @Override
            public Socket createSocket(String host, int port)
                    throws IOException, UnknownHostException {
                Socket socket = new Socket();
//                if (localIp != null) {
//                    socket.bind(new InetSocketAddress(InetAddress
//                            .getByName(localIp), localPort));
//                }
                socket.connect(
                        new InetSocketAddress(InetAddress.getByName(host), port),
                        connectTimeout);
                return socket;
            }
        });
        session.connect(connectTimeout);
    }

    public void close() {
        if (session != null) {
            session.disconnect();
        }
        if (channel != null) {
            channel.disconnect();
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String commondCmd(String cmd) throws IOException {
        if (channel == null) {
            try {
                channel = (ChannelExec) session.openChannel("exec");
                channel.setPty(true);
                channel.connect();
            } catch (JSchException e) {
                e.printStackTrace();
            }
        }
        InputStream is = channel.getInputStream();
        OutputStream os = channel.getOutputStream();
        return sendCommand(is, os, cmd);
    }

    private String sendCommand(InputStream is, OutputStream os,
                               String cmd) throws IOException {
        os.write(cmd.getBytes());
        os.flush();
        StringBuffer sb = new StringBuffer();
        int beat = 0;
        while (true) {
            if (beat > 3) {
                break;
            }
            if (is.available() > 0) {
                byte[] b = new byte[is.available()];
                is.read(b);
                String str = new String(b);
                if (!StringUtils.equals(cmd, str)) {
                    sb.append(new String(b));
                }
                beat = 0;
            } else {
                if (sb.length() > 0) {
                    beat++;
                }

                try {
                    Thread.sleep(sb.toString().trim().length() == 0 ? 1000
                            : 300);
                } catch (InterruptedException e) {
                }
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String ip = "ssh.runpod.io";
        int port = 22;
        String userName = "nfgsd3kjw0rqb6-64410b0d";
        int timeOut = 3000;
        JSCHSession jschSession = null;
        try {
            jschSession = new JSCHSession(ip, port, "/Users/apple/Documents/ssh/id_ed25519", userName, timeOut);
            String s = jschSession.commondCmd("source /etc/profile\n");
            System.out.println(s);
            s = jschSession.commondCmd("java\n");
            System.out.println(s);
            s = jschSession.commondCmd("pwd\n");
            System.out.println(s);
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.close();
            }
        }


//        String localIp = null;
//        String cmd = "ls\n";
//        String result = null;
//        try {
//            result = execShellCmdBySSH(ip, port, localIp, localPort, timeOut,
//                    userName, cmd);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(result);
    }
}
