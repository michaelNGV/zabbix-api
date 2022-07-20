package au.com.ngv.zabbixAPI

import com.alibaba.fastjson.JSON

public interface ZabbixApi
{
	fun destroy()
	fun apiVersion(): String
	fun call(name: String, request: Request): JSON
}
