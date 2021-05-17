exception SystemException {
  1: optional string message;
}

struct KeyValuePair {
  1: optional i32 key;
  2: optional string value;
}

struct ReplicaInfo{
  1: optional string ip;
  2: optional i32 port;
  3: optional i32 start_key;
  4: optional i32 end_key;
}




service KeyValueStore {
  void putKey(1: i32 key, 2: string value, 3: i32 consistency_level )
    throws (1: SystemException systemException),
  
  string getKey(1: i32 key, 2: i32 consistency_level)
    throws (1: SystemException systemException),

  void put_replica_key(1: i32 key, 2: string value),

  void restore_replica_key(1: i32 key, 2: string value),
  
  void write_commit_log(1: i32 key, 2: string value),

  void replay_commit_log(1: string filename),

  i32 testConnection(),
}
