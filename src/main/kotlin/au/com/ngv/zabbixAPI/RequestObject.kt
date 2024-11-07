package au.com.ngv.zabbixAPI

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject


class RequestObject() : Request
{
	val params = HashMap<String, Any>()

	override fun paramCount() = params.size

	fun paramEntry(key: String, value: Any): RequestObject {
		params[key] = value
		return this
	}

	override fun serialise(): JsonObject = buildJsonObject { params }
}