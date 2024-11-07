package au.com.ngv.zabbixAPI

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
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

	private val auth: String? =
		callNoAuth("user.login",
			RequestObject().paramEntry("user", user).paramEntry("password", password))
			.jsonObject[resultText]?.jsonPrimitive?.content


	private fun callNoAuth(name: String, request: Request): JsonObject =
		call0(buildJsonObject {
			put("jsonrpc", jsonrpc)
			put("method", name)
			put("params", request.serialise())
			put("id", Id)
		}).jsonObject

	fun connected() = auth != null

	override fun apiVersion(): String =
		call("apiinfo.version", RequestObject()).jsonObject[resultText]!!.jsonPrimitive.content

	fun hostExists(name:String) = call("host.exists", RequestObject().paramEntry("name", name))
		.jsonObject[resultText]!!.jsonPrimitive.boolean

	fun hostCreate(host: String, groupId: String): String =
		call("host.create",
			RequestObject()
				.paramEntry("host", host)
				.paramEntry("groups", buildJsonObject {
					put("host", host)
					put("groups", buildJsonArray {
						add(buildJsonObject {
							put("groupid", groupId)
						})
					})
				}))
			.jsonObject[resultText]!!.jsonObject["hostids"]?.jsonArray?.get(0)!!.jsonPrimitive.content

	fun hostgroupExists(name:String) =
		call("hostgroup.exists", RequestObject().paramEntry("name", name))
			.jsonObject[resultText]!!.jsonPrimitive.boolean

	/**
	*
	* @param name
	* @return groupId
	*/
	fun hostgroupCreate(name:String) =
		call("hostgroup.create", RequestObject().paramEntry("name", name))
			.jsonObject[resultText]!!
			.jsonObject["groupids"]?.jsonArray?.get(0)

	override fun call(name: String, request: Request): JsonElement =
		call0(buildJsonObject {
			put("jsonrpc", jsonrpc)
			put("method", name)
			put("params", request.serialise())
			put("id", Id)
			put("auth", auth)
		})

	private fun call0(body: JsonObject): JsonElement {
		try {
			val httpRequest = org.apache.http.client.methods.RequestBuilder.post().setUri(uri)
				.addHeader("Content-Type", "application/json")
				.setEntity(StringEntity(body.jsonPrimitive.content, ContentType.APPLICATION_JSON)).build()
			val response = httpClient.execute(httpRequest)
			return Json.parseToJsonElement(String(EntityUtils.toByteArray(response.entity)))
		} catch (e: IOException) {
			throw RuntimeException("DefaultZabbixApi call exception!", e)
		}
	}
}


fun JsonObject.returnOK() = containsKey(resultText)


private const val resultText = "result"