package com.bsb.games.zoo.example.leader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import com.google.common.io.Closeables;

public class LeaderRunner
{

    private static final int NUM_CLIENTS = 10;
    private static final String LEADER_PATH = "/test/leader";
    
    public static void main(String[] args) throws Exception {
        List<CuratorFramework> frameworks = new ArrayList<CuratorFramework>();
        List<LeaderClient> clients = new ArrayList<LeaderClient>();
        TestingServer server = new TestingServer();
        
        try {
            for(int i = 0; i < NUM_CLIENTS; i++) {
                CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
                LeaderClient zlc = new LeaderClient(client, LEADER_PATH, "ZCL " + i);
                frameworks.add(client);
                clients.add(zlc);
                client.start();
                zlc.start();
            }
            
            System.out.println("Press enter/return to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } finally {
            for(CuratorFramework cf: frameworks) {
                if(cf != null) {
                    try {
                        cf.close();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            for(LeaderClient lc: clients) {
                try {
                    lc.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
