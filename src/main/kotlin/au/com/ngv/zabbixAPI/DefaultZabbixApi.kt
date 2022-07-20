package au.com.ngv.zabbixAPI

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URI


class DefaultZabbixApi(val uri: URI,
		user: String,
		password: String,
		val httpClient: CloseableHttpClient = HttpClients.custom().build(),
		val jsonrpc: String = "2.0",
		val Id: Int = 1) : ZabbixApi
{
	override fun destroy() =
		try {
			if (connected())
				call("user.logout", RequestObject())
			httpClient.close()
		} catch (e:Exception) {
			LoggerFactory.getLogger(DefaultZabbixApi::class.java).error("Close HTTPClient", e)
		}

	private val auth: String? = callNoAuth(
		"user.login",
		RequestObject()
			.paramEntry("user", user)
			.paramEntry("password", password)).getString(resultText)

	private fun callNoAuth(name: String, request: Request): JSONObject {
		val body = mapOf(
			"jsonrpc" to jsonrpc,
			"method" to name,
			"params" to request.serialise(),
			"id" to Id
		)
		return call0(JSONObject(body))
	}

	fun connected() = auth != null

	override fun apiVersion() = call("apiinfo.version", RequestObject()).getString(resultText)

	fun hostExists(name:String) = call("host.exists", RequestObject().paramEntry("name", name))
		.getBooleanValue(resultText)

	fun hostCreate(host: String, groupId: String): String{
		val groups = JSONArray()
		val group = JSONObject()
		group.put("groupid", groupId)
		groups.add(group)
		return call("host.create", RequestObject()
				.paramEntry("host", host)
				.paramEntry("groups", groups))
			.getJSONObject(resultText)
			.getJSONArray("hostids")
			.getString(0)
	}

	fun hostgroupExists(name:String) = call("hostgroup.exists", RequestObject().paramEntry("name", name))
		.getBooleanValue(resultText)

	/**
	*
	* @param name
	* @return groupId
	*/
	fun hostgroupCreate(name:String) = call("hostgroup.create", RequestObject().paramEntry("name", name))
		.getJSONObject(resultText)
		.getJSONArray("groupids")
		.getString(0)

	override fun call(name: String, request: Request): JSONObject {
		val body = mapOf(
			"jsonrpc" to jsonrpc,
			"method" to name,
			"params" to request.serialise(),
			"id" to Id,
			"auth" to auth)
		return call0(JSONObject(body))
	}

	private fun call0(body: JSONObject): JSONObject {
		try {
			val httpRequest = org.apache.http.client.methods.RequestBuilder.post().setUri(uri)
				.addHeader("Content-Type", "application/json")
				.setEntity(StringEntity(JSON.toJSONString(body), ContentType.APPLICATION_JSON)).build()
			val response = httpClient.execute(httpRequest)
			return JSON.parseObject(String(EntityUtils.toByteArray(response.entity)))
		} catch (e: IOException) {
			throw RuntimeException("DefaultZabbixApi call exception!", e)
		}
	}
}


/**
 * flattenMap -- convert multimap to map
 */
fun JSONObject.flattenMap(fieldName: String) {
	val replacement = JSONObject()
	this.getJSONArray(fieldName).forEach { q ->
		(q as JSONObject).forEach { t, u -> replacement[t] = u }
	}
	this[fieldName] = replacement
}


fun JSONObject.returnOK() = containsKey(resultText)


private const val resultText = "result"