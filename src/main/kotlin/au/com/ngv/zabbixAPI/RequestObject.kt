package au.com.ngv.zabbixAPI

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject


class RequestObject() : Request
{
	val params = HashMap<String, Any>()

	override fun paramCount() = params.size

	fun paramEntry(key: String, value: Any): RequestObject {
		params[key] = value
		return this
	}

	override fun serialise(): JSON = JSONObject(params)
}