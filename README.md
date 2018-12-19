# store_client
# 1. Introduce
## 1.1 Future
* 模块间互相独立。
* 预留接口，具备扩展性。
# 2. User Guide
## 2.1 hbase
### 2.1.1 Hbase Install
1.2.6 版本 hbase 测试通过，2.0.3 测试未通过。
### 2.1.2 修改配置文件
修改 hbase.properties
```
hbase.zookeeper.quorum=localhost
```
### 2.1.3 运行测试用例

# 3. Developer Guide
本项目分为两个模块
## 3.1 general.util

## 3.2 store
### 3.2.1 store.hbase.client
### 3.2.2 store.kafka.client
### 3.2.3 store.mongo.client
### 3.2.4 store.redis.client
### 3.2.5 store.zmq.client
# 4. 测试模块


