# rpc-core
轻量级分布式 RPC 框架


使用如下技术选型：

Spring：它是最强大的依赖注入框架，也是业界的权威标准。
Netty：它使 NIO 编程更加容易，屏蔽了 Java 底层的 NIO 细节。
序列化：hessian和Protostuff框架，两个可选其一，默认使用hessian。
ZooKeeper：提供服务注册与发现功能，开发分布式系统的必备选择，同时它也具备天生的集群能力。
