package au.com.ngv.zabbixAPI

import kotlinx.serialization.json.JsonElement

interface ZabbixApi
{
	fun destroy()
	fun apiVersion(): String
	fun call(name: String, request: Request): JsonElement?
}
