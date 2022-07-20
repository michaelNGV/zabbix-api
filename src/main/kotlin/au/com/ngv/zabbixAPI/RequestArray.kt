package au.com.ngv.zabbixAPI

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import java.util.LinkedList


class RequestArray(): Request
{
	private val params: MutableList<Any> = LinkedList<Any>()

	override fun paramCount() = params.size

	fun paramEntry(value:Any): RequestArray {
		params.add(value)
		return this
	}

	override fun serialise(): JSON = JSONArray(params)
}