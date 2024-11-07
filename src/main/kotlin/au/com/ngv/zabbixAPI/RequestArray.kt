package au.com.ngv.zabbixAPI

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonArray
import java.util.LinkedList


class RequestArray(): Request
{
	private val params: MutableList<Any> = LinkedList<Any>()

	override fun paramCount() = params.size

	fun paramEntry(value:Any): RequestArray {
		params.add(value)
		return this
	}

	override fun serialise(): JsonArray = buildJsonArray { params }
}