# ElasticSearch-Demo

基于canal同步mysql增量到ElasticSearch，并提供查询支持

# 配置说明
```yaml
# canal部分
canal:
  client:
    instances:
      example: # 实例名，可配置多个该节点 String
        host: 127.0.0.1 # ip或域名 String
        port: 11111 # 端口 Integer
        batch-size: 1000 # 最大并发消息数量 Integer
        cluster-enabled: false # 集群模式 boolean
        zookeeper-address: # 当集群模式开启时，需要填写该地址,多个使用逗号分隔
# 自定义配置
sync:
  elasticsearch: # Elasticsearch配置
    host: 127.0.0.1 #ES地址
    port: 9200 # ES端口
    schema: http # ES使用协议(使用Rest-client使用http)
  config:
    mapping: # 下面包含第一层List，是单表的配置
      - enabled: true # 是否启用 boolean
        table-name: pt_petition_case # 表名 String
        fields: # 下面包含第二层list
          - field-name: id # 表字段 String 
            mapping-name: idMappingName # 映射为字段 String 
            show: true # 是否显示 boolean
            enable: false # 是否启用该映射 boolean
```

# 依赖中间件

- 依赖Canal，版本：1.1.2
- 依赖ElasticSearch，版本：6.5.2

# 现有功能
- 使用canal解析MySQL的binlog并存入ElasticSearch
- 定制化字段名称与是否显示（TODO）
- 提供简单查询接口

# sql文件夹
日志支持存入数据库，sql文件夹下为结构初始化脚本
参数值参考JDBC

# TODO
[ ] 后期有计划将各部分解耦，使用消息队列（Kafka/RabbitMQ等）进行通信。
[ ] 将分页改为scroll Api
[ ] 表字段名映射