# zabbix-api
zabbix-api for Kotlin and hopefully Java too.

https://www.zabbix.com/wiki/doc/api

https://www.zabbix.com/documentation/5.0/manual/api/reference/user/login

##Info
This package is based on hengyunabc's but:
- Rewritten in Kotlin.
- Removed superfluous layers of builders, factories and incremental construction that for some reason always go with Java.
- Crucially, API requests that taken a `"param"` *array* value are supported.

- Bug reports are welcome.
- PRs for extra functionality that doesn't detract from the functional value-based style will be seriously considered.
- Requests/PRs for rewrites to accommodate builders, incremental construction etc. are not welcome.

Although the package is written in Kotlin, it's intended to be useable by Java;  hence the style of request construction.
We at NGV don't use Java so we haven't tested that aspect.

##Example
You can set your own ```HttpClient```.

```kotlin
    val zabbixAPI=DefaultZabbixApi(URI(zabbixHost),
        zabbixUser,
        zabbixPassword,
        HttpClients
            .custom()
            .setConnectionManager(PoolingHttpClientConnectionManager())
            .setDefaultRequestConfig(
                RequestConfig.custom()
                .setConnectTimeout(5 * 1000)
                .setConnectionRequestTimeout(5 * 1000)
                .setSocketTimeout(5 * 1000)
                .build())
            .build())
    if (zabbixAPI.connected()) {
	    val hostJSON=zabbixAPI.call("host.get",
            RequestObject().paramEntry("filter",HostSpec(zabbixMonitorHost))).getJSONArray("result")
        logger.info("HOSTS: $hostJSON")
    }
    zabbixAPI.destroy()
```

##Maven dependency
```xml
<dependency>
	<groupId>au.com.ngv.zabbixAPI</groupId>
	<artifactId>zabbix-api</artifactId>
	<version>1.0.0</version>
</dependency>
```

##Licence
Apache Licence V2
