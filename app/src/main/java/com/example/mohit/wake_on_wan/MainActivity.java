package com.example.mohit.wake_on_wan;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {

    EditText txtMac;
    EditText txtHost;
    EditText txtPort;
    CheckBox chkProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMac = (EditText) findViewById(R.id.mac);
        txtHost = (EditText) findViewById(R.id.host);
        txtPort = (EditText) findViewById(R.id.port);
        chkProxy = (CheckBox) findViewById(R.id.proxy);
        /*WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiManager.WifiLock lock = wifi.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "test");
        lock.acquire();*/
    }

    public void onProxyClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if (checked) {
            txtHost.setText(""); //TODO: Add proxy address
        } else {
            txtHost.setText(""); //TODO: Add pc address
        }
    }

    public void sendPacket(View view) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                sendMagicPacket();
            }

        });
        t.start();
    }

    public void hibernate(View view) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                sendHibernationPacket();
            }

        });
        t.start();
    }

    private void sendMagicPacket() {
        try {
            InetAddress address = InetAddress.getByName(txtHost.getText().toString());
            int port = Integer.parseInt(txtPort.getText().toString());
            String[] macData = txtMac.getText().toString().split("\\:");
            byte[] mac = new byte[6];
            for (int i = 0; i < 6; i++) {
                mac[i] = (byte)Short.parseShort(macData[i], 16);
            }
            byte[] magic;
            magic = new byte [102];
            for (int i = 0; i < 6; i++) {
                magic[i] = (byte)0xff;
            }
            for (int i = 6; i < 102; i++) {
                magic[i] = mac[i%6];
            }

            DatagramPacket packet = new DatagramPacket(magic, magic.length, address, port);

            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.send(packet);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "Sent packet", Toast.LENGTH_SHORT).show();
                }
            });
            socket.close();
        } catch (final SocketException e) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (final IOException e) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendHibernationPacket() {
        byte[] hibernate = "HIBERNATE".getBytes();

        try {
            InetAddress address = InetAddress.getByName(txtHost.getText().toString());
            int port = Integer.parseInt(txtPort.getText().toString());
            DatagramPacket hibernatePacket = new DatagramPacket(hibernate, hibernate.length, address, port);
            DatagramSocket sendSocket = new DatagramSocket();
            sendSocket.send(hibernatePacket);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "Sent hibernation packet", Toast.LENGTH_SHORT).show();
                }
            });
            sendSocket.close();


        } catch (final SocketException e) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (final IOException e) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
