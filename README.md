```sh
docker-compose up -d
```

```sh
docker exec -it configsvr bash
```

```sh
mongo --port 27019 --eval 'rs.initiate({_id: "rsConfig", configsvr: true, members: [{ _id : 0, host : "configsvr:27019" }]})'
```

```sh
exit
```

```sh
docker exec -it shard1 bash
```

```sh
mongo --port 27018 --eval 'rs.initiate({_id: "rsShard1", members: [{ _id : 0, host : "shard1:27018" }]})'
```

```sh
exit
```

```sh
docker exec -it shard2 bash
```

```sh
mongo --port 27018 --eval 'rs.initiate({_id: "rsShard2", members: [{ _id : 0, host : "shard2:27018" }]})'
```

```sh
exit
```

```sh
docker exec -it shard3 bash
```

```
mongo --port 27018 --eval 'rs.initiate({_id: "rsShard3", members: [{ _id : 0, host : "shard3:27018" }]})'
```

```sh
exit
```

```sh
docker exec -it shard4 bash
```

```sh
mongo --port 27018 --eval 'rs.initiate({_id: "rsShard4", members: [{ _id : 0, host : "shard4:27018" }]})'
```

```sh
exit
```

```sh
docker exec -it mongos bash
```

```sh
mongo --port 27017 --eval 'sh.addShard("rsShard1/shard1:27018")'
```

```sh
mongo --port 27017 --eval 'sh.addShard("rsShard2/shard2:27018")'
```

```sh
mongo --port 27017 --eval 'sh.addShard("rsShard3/shard3:27018")'
```

```sh
mongo --port 27017 --eval 'sh.addShard("rsShard4/shard4:27018")'
```

```sh
mongo --port 27017 --eval 'sh.enableSharding("myDatabase")'
```

```sh
mongo --port 27017 --eval 'sh.shardCollection("myDatabase.myCollection", { "shardKey": 1 })'
```

```sh
exit
```