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
import java.io.*;
// Generated code

public class KeyValueStoreHandler implements KeyValueStore.Iface {

  public void putKey(KeyValuePair keyValuePair, int consistency_level) throws SystemException, org.apache.thrift.TException{  
	System.out.println("Put value called");
  }

  public String getKey(int key, int consistency_level) throws SystemException, org.apache.thrift.TException {
	System.out.println("Get Value Called");
	return "Success";
}

}
