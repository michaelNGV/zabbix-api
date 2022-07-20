package au.com.ngv.zabbixAPI

import com.alibaba.fastjson.JSON

interface Request
{
	fun paramCount(): Int

	fun serialise(): JSON
}