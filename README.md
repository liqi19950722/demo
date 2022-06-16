# 整合nacos-spring-project 实现 spring-cloud-alibaba的单条配置刷新

## 利用SpringCloud `@RefreshScope`实现

```yaml
foo:
  value: foo
```

```
curl localhost:8080/foo

foo
```

修改配置

```yaml
foo:
  value: foo_refreshed
```

```
curl localhost:8080/foo

foo_refreshed
```

## 利用nacos-spring-project 实现

```yaml
foo:
  nacos:
    value: nacos_foo
```

```
curl localhost:8080/nacos/foo

nacos_foo
```

修改配置

```yaml
foo:
  nacos:
    value: nacos_foo_refreshed
```

```
curl localhost:8080/nacos/foo

nacos_foo_refreshed
```

| 依赖 |  版本  |
|----|----|
|java|17|
|spring-boot |2.7.0  |
|spring-cloud  |2021.0.3 |
|spring-alibaba |2021.0.1.0 |

[nacos-spring-project](https://github.com/nacos-group/nacos-spring-project)

[spring-cloud-alibaba](https://github.com/alibaba/spring-cloud-alibaba)

## 实现思路

`ConfigService#addListener()`

Spring的代理类`EventPublishingConfigService`，发布Spring事件`NacosConfigReceivedEvent`，
达到刷新Spring`Environment`对象目的，从`Environment`中取出对应的值，利用反射修改目标值