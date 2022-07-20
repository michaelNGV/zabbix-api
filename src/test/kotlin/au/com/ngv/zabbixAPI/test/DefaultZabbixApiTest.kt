package au.com.ngv.zabbixAPI.test

import au.com.ngv.zabbixAPI.ZabbixApi
import org.junit.After
import org.junit.Before
import org.junit.Test


class DefaultZabbixApiTest {
     private var zabbixApi: ZabbixApi? = null

     @Before
     fun before() {
     }

     @After
     fun after() {
          zabbixApi?.destroy()
     }

     @Test
     fun testVersion() {
     }

     @Test
     fun testLogin() {
     }

     @Test
     fun testHostGet() {
     }

     @Test
     fun testItemCreate() {
     }

     @Test
     fun testGetTrigger() {
     }
}