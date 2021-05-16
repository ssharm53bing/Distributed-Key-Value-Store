/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.thrift.TException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol; 
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress;
import java.net.UnknownHostException;
// Generated code


public class KeyValueStoreHandler implements KeyValueStore.Iface {
  private static List<ReplicaInfo> rep_List;
  private List<KeyValuePair> keyValueList;
  private String ip_address;
  private int port;
  private String remote_call_ip;
  private int remote_call_port;

  public KeyValueStoreHandler(int port){
	  this.port = port;
   try{
     keyValueList = new ArrayList<KeyValuePair>();
     getReplicas("Replicas.txt");
   }catch(IOException e){
   	
   }	  
  }
  public void putKey(int key, java.lang.String value, int consistency_level) throws SystemException, org.apache.thrift.TException{  
        System.out.println("Put Key Called");
	int i = 0 ;
	int j = 0;
	int count = 0;
	int test_count = 0;
	int total_active_replicas = 0 ;

	int total_success = 0;
	TTransport transport;
	try{
	   InetAddress ia = InetAddress.getLocalHost();
	   ip_address = ia.getHostAddress();
	} catch(UnknownHostException e){

	}
	while(i < rep_List.size()){
	 if(key >= rep_List.get(i).start_key && key <=rep_List.get(i).end_key){
         	System.out.println("Got Replica Server");
	      	 break;
	 }
	 i++;
	} 
	
	j = i;
	while(test_count < 3 ){
	 if( rep_List.get(j).ip == ip_address && rep_List.get(j).port == port){
          total_active_replicas++;					               
	  j++;
	 }
	 else{
	     remote_call_ip = rep_List.get(j).ip;
	     remote_call_port = rep_List.get(j).port;
	     System.out.println(remote_call_ip);
	     System.out.println(remote_call_port);
	     transport = new TSocket(remote_call_ip, Integer.valueOf(remote_call_port));
	     transport.open();
	     TProtocol protocol = new  TBinaryProtocol(transport);
	     KeyValueStore.Client client = new KeyValueStore.Client(protocol);
	      try{
	           client.testConnection();
	           total_active_replicas++;
	      }
	           catch(Exception e){
                    System.out.println("Cannot connect to replicas");
               }
            	j++;
            	test_count++;
            	if(j > 3 ){
                 j = 0;
           	}
         transport.close();
	}
	}
	System.out.println("Hello From Here");
	if(total_active_replicas < consistency_level){
	   SystemException systemException = new SystemException();
	   systemException.message = "Not enough server is active";
	   throw systemException;

	}

	while(count < 3){
		if( rep_List.get(i).ip == ip_address && rep_List.get(i).port == port){
			put_replica_key(key, value);
		}
		else{
			remote_call_ip = rep_List.get(i).ip;
			remote_call_port = rep_List.get(i).port;
			System.out.println("Storing the Value");
			System.out.println(remote_call_ip);
			System.out.println(remote_call_port);
			transport = new TSocket(remote_call_ip, Integer.valueOf(remote_call_port));
			transport.open();
		        TProtocol protocol = new  TBinaryProtocol(transport);
			KeyValueStore.Client client = new KeyValueStore.Client(protocol);
			try{
		          client.put_replica_key(key , value);
			  total_success++;
			}
			catch(Exception e){
			}
			i++;
			count++;
			if(i > 3 ){
			  i = 0;
			}
	                transport.close();	       
			//remote procedure call to replicas
			
	  	 }	
	
	}
	System.out.println("Put Value Called1");	
  }

  public String getKey(int key, int consistency_level) throws SystemException, org.apache.thrift.TException {
	System.out.println("Get Value Called");
	System.out.println(key);
	int i = 0;
	while(i< keyValueList.size()){
	System.out.println(keyValueList.get(i).key);
	if(keyValueList.get(i).key == key){
	       return keyValueList.get(i).value;
         }    
	    else{
	    	i++;
	    }
	}
	return "Cannot Find the key";
}


// pre-configured replicas
  public static void getReplicas(String filename) throws IOException{
    File file = new File(filename);
    BufferedReader fileReader = null;
    List<ReplicaInfo> replica_list = new ArrayList<ReplicaInfo>();
   
    try {
         fileReader = new BufferedReader(new FileReader(file));
         String line = null;
         while ((line = fileReader.readLine()) != null) {
             String[] replicas = line.split(",");
	     String ip = replicas[0].split(":")[0];
	     String port = replicas[0].split(":")[1];
         replica_list.add(create_replica(ip, port, replicas[1], replicas[2]));
            }
    } catch (FileNotFoundException e) {
                e.printStackTrace();
    }
                          
    rep_List = replica_list;
            
   }
    
  public static ReplicaInfo create_replica(String ip, String port, String start_key, String end_key){
    ReplicaInfo replica = new ReplicaInfo();
    replica.ip = ip;
    replica.port = Integer.parseInt(port);
    replica.start_key = Integer.parseInt(start_key);
    replica.end_key = Integer.parseInt(end_key);
    return replica;
  }
  
  public void put_replica_key(int key, String value){
  	System.out.println("Put Value Called1");
        System.out.println(key);
        int i = 0;
        while(i < keyValueList.size()){
	       if(keyValueList.get(i).key == key){
		     break;
	       }
	       else{
		     i++;
		}
           }
        if(i == 0 || i == keyValueList.size()){
	         KeyValuePair keyValuePair = new KeyValuePair();
	          keyValuePair.key = key;
	          keyValuePair.value = value;
	         keyValueList.add(keyValuePair);
               }
        else{
	         keyValueList.get(i).key = key;
	          keyValueList.get(i).value = value;
	         }	  
  
  }
  public int testConnection(){
	return 1;
  }

}
