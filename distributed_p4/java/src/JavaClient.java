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

// Generated code

import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import java.nio.file.Files;                                                                                             import java.nio.file.Path;                                                                                              import java.nio.file.Paths;                                                                                             import java.io.*; 
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.TSerializer;

public class JavaClient {
  public static void main(String [] args){
      

    try {
      TTransport transport;
      if (args[0].contains("simple")) {
	transport = new TSocket(args[1], Integer.valueOf(args[2]));
        transport.open();
      }
      else {
        /*
         * Similar to the server, you can use the parameters to setup client parameters or
         * use the default settings. On the client side, you will need a TrustStore which
         * contains the trusted certificate along with the public key. 
         * For this example it's a self-signed cert. 
         */
        TSSLTransportParameters params = new TSSLTransportParameters();
        params.setTrustStore("/home/cs557-inst/thrift-0.13.0/lib/java/test/.truststore", "thrift", "SunX509", "JKS");
        /*
         * Get a client transport instead of a server transport. The connection is opened on
         * invocation of the factory method, no need to specifically call open()
         */
        transport = TSSLTransportFactory.getClientSocket(args[1], Integer.valueOf(args[2]), 0, params);
      }

      TProtocol protocol = new  TBinaryProtocol(transport);
      KeyValueStore.Client client = new KeyValueStore.Client(protocol);
      if(args[3].equals("get")){
      performRead(client, Integer.parseInt(args[4]), Integer.parseInt(args[5]));
      }else if(args[3].equals("put")){
       performWrite(client, Integer.parseInt(args[4]), args[5],Integer.parseInt(args[6]));
      }else{
       System.out.println("Please mention read/write");
      }

      transport.close();
    } catch (TException x) {
      x.printStackTrace();
    } 
  }

  private static void performRead(KeyValueStore.Client client, int key, int consistency_level) throws TException
 {
  try{	 
       String value = client.getKey(key,consistency_level);
       System.out.println(value);
  }catch(SystemException e){	
  	System.out.println(e);
  }
 }
  private static void performWrite(KeyValueStore.Client client, int key, String value, int consistency_level) throws TException
 {
       KeyValuePair keyValuePair = new KeyValuePair();
       keyValuePair.key = 101;
       keyValuePair.value = "Hello World";
       System.out.println(keyValuePair.key);
       try{
       client.putKey(key,value , consistency_level);
       }
       catch(SystemException e){
       	System.out.println(e);
       }
 }
}
