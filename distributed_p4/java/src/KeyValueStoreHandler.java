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
<<<<<<< HEAD
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
=======
import java.sql.Timestamp;
>>>>>>> 0903b92a86ab8997f61c22bb1845adca5d594c2d
// Generated code


public class KeyValueStoreHandler implements KeyValueStore.Iface {
  private static List<ReplicaInfo> rep_List;
  private List<KeyValuePair> keyValueList;
  private String ip_address;
  private int port;
  private String remote_call_ip;
  private int remote_call_port;
  private List<Hint> hints;

  public KeyValueStoreHandler(int port){
	  this.port = port;
	   try{
	        keyValueList = new ArrayList<KeyValuePair>();
	        getReplicas("Replicas.txt");
	     }catch(IOException e){

	}
	  replay_commit_log(String.valueOf(port));	  
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
	System.out.println(rep_List.get(j).ip);
	System.out.println(rep_List.get(j).port);
        System.out.println(ip_address);
	System.out.println(port);

	 if( rep_List.get(j).port == port){
          System.out.println("Inside Replica Server");
          total_active_replicas++;					               
	  j++;
	  test_count++;
	  if(j > 3 ){
	  	j = 0;
	  }
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
		if( rep_List.get(i).port == port){
			put_replica_key(key, value);
			i++;
			count++;
			if(i > 3){
			  i = 0 ;
			}
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
				System.out.println(e);
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
      TTransport transport;
      try{
         InetAddress ia = InetAddress.getLocalHost();
         ip_address = ia.getHostAddress();
      } catch(UnknownHostException e){

      }
      String result = "";
      int i =0;
      List<KeyValuePair> read_list = new ArrayList<KeyValuePair>();
      
      while(i<rep_List.size()){
          if(rep_List.get(i).ip == ip_address && rep_List.get(i).port==port){
              break;
          }
          i++;
      }
      KeyValuePair p = get_value(key);
      if(p.value != null) {
        read_list.add(p);
          }
      int count=0;
      
      while(count<3){
          if(read_list.size()<consistency_level){
              i++;
              i = i%4;
              remote_call_ip = rep_List.get(i).ip;
              remote_call_port = rep_List.get(i).port;
              System.out.println("calling next replica");
              System.out.println(remote_call_ip);
              System.out.println(remote_call_port);
              transport = new TSocket(remote_call_ip, Integer.valueOf(remote_call_port));
              transport.open();
              TProtocol protocol = new  TBinaryProtocol(transport);
              KeyValueStore.Client client = new KeyValueStore.Client(protocol);
              try{
                  KeyValuePair pair = client.get_value(key);
                  if(pair.value != null) {
                    read_list.add(pair);
                    //System.out.println(pair.value);
              }
                  }
              catch(Exception e){
                  System.out.println(e);
              }
              transport.close();
          }
          else if (read_list.size() == consistency_level) {
              System.out.println("Success");
              KeyValuePair resVal = read_list.get(0);
                for(KeyValuePair pair: read_list){
                    if(resVal.time < pair.time){
                        resVal = pair;
                    }
                }
                result = resVal.value;
              System.out.println(result);
              break;
              }
          count++;
      }
   
    if(read_list.size() < consistency_level){
         SystemException systemException = new SystemException();
         systemException.message = "Not enough server is active";
         throw systemException;

    }
    if(result == ""){
        SystemException systemException = new SystemException();
        systemException.message = "key not in system";
        throw systemException;
      }
      
      //perform Hinted_Handoff
      hintedHandoff(consistency_level);
      
	return result;
}
    
    public void hintedHandoff(int consistency_level){
        List<Hint> hinted_list = hints; //temporary data structure
        for(Hint hint: hinted_list){
            //send hint to the server
            remote_call_ip = hint.ip;
            remote_call_port = hint.port;
            try{
            TTransport transport = new TSocket(remote_call_ip, Integer.valueOf(remote_call_port));
            transport.open();
            TProtocol protocol = new  TBinaryProtocol(transport);
            KeyValueStore.Client client = new KeyValueStore.Client(protocol);
            client.putKey(hint.key, hint.value, consistency_level);
            transport.close();
         }
         catch(Exception e){
         	System.out.println(e);
         }
            for(int i=0; i<hints.size();i++){
                System.out.println("Performed Hinted Handoff");
                if(hints.get(i)==hint){
                hints.remove(i);
                    }
            }
        }
        
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
	write_commit_log(key,value);
        int i = 0;
        while(i < keyValueList.size()){
	       if(keyValueList.get(i).key == key){
		     break;
	       }
	       else{
		     i++;
		}
           }
        Timestamp t = new Timestamp(System.currentTimeMillis());
        if(i == 0 || i == keyValueList.size()){
	         KeyValuePair keyValuePair = new KeyValuePair();
	          keyValuePair.key = key;
	          keyValuePair.value = value;
              keyValuePair.time = t.getTime();
	         keyValueList.add(keyValuePair);
               }
        else{
	         keyValueList.get(i).key = key;
	          keyValueList.get(i).value = value;
            keyValueList.get(i).time = t.getTime();
	         }
  }
    
  public KeyValuePair get_value(int key){
    
    int i = 0;
    while(i< keyValueList.size()){
    if(keyValueList.get(i).key == key){
         return keyValueList.get(i);
       }
      else{
          i++;
      }
   }
   KeyValuePair pair = new KeyValuePair();
    //System.out.println("key not found");
    return pair;
  }
<<<<<<< HEAD

 public void restore_replica_key(int key, String value){
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

  public void write_commit_log(int key, String value){		
	String filepath = "diskcommitlog/"+String.valueOf(port);
	try{
        File myFile = new File(filepath);
	FileWriter fw = new FileWriter(myFile.getAbsoluteFile(),true);
	BufferedWriter bw = new BufferedWriter(fw);
	bw.write(String.valueOf(key));
	bw.write(",");
	bw.write(value);
	bw.write("\n");
	bw.close();
	}
	catch(IOException e){
	
	}
  }
  public void replay_commit_log(String f){
	 String path = "diskcommitlog/"+f; 
    	 System.out.println("Replaying Log from file"+f);
	 File file = new File(path);

    BufferedReader fileReader = null;
    try {                                                                                                                        fileReader = new BufferedReader(new FileReader(file));
	String line = null;
	while ((line = fileReader.readLine()) != null) {
	   System.out.println("Inside While Loop");
	   String[] replicas = line.split(",");
	   int key = Integer.valueOf(replicas[0]);
	   String value = replicas[1];
	   restore_replica_key(key,value);
	}
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch(IOException e){
	    e.printStackTrace();
	}
  }

=======
      
>>>>>>> 0903b92a86ab8997f61c22bb1845adca5d594c2d
  public int testConnection(){
	return 1;
  }

}
