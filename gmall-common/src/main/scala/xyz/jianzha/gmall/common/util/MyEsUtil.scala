package xyz.jianzha.gmall.common.util

import java.util
import java.util.Objects

import io.searchbox.client.config.HttpClientConfig
import io.searchbox.client.{JestClient, JestClientFactory}
import io.searchbox.core.{Bulk, BulkResult, Index}

/**
 * @author Y_Kevin
 * @date 2020-06-17 15:46
 */
object MyEsUtil {
  private val ES_HOST = "http://hadoop101"
  private val ES_HTTP_PORT = 9200
  private var factory: JestClientFactory = null

  /**
   * 获取客户端
   *
   * @return jestclient
   */
  def getClient: JestClient = {
    if (factory == null) build()
    factory.getObject
  }

  /**
   * 关闭客户端
   */
  def close(client: JestClient): Unit = {
    if (!Objects.isNull(client)) try
      client.close()
    catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  /**
   * 建立连接
   */
  private def build(): Unit = {
    factory = new JestClientFactory
    factory.setHttpClientConfig(new HttpClientConfig.Builder(ES_HOST + ":" + ES_HTTP_PORT).multiThreaded(true)
      .maxTotalConnection(20) //连接总数
      .connTimeout(10000).readTimeout(10000).build)

  }

  /**
   * 插入一条
   */
  private def indexDoc(): Unit = {
    val jestClient: JestClient = getClient
    val source = "{\n \"name\":\"li4\",\n \"age\":456,\n \"amount\": 250.1,\n \"phone_num\":\"138***2123\"}"
    val index: Index = new Index.Builder(source).index("gmall_test").`type`("_doc").build()
    jestClient.execute(index)
    close(jestClient)
  }

  def main(args: Array[String]): Unit = {
    indexDoc()
  }

  /**
   * 批量插入ES
   *
   * @param indexName 相当于库名
   * @param list      待插入数据
   */
  def indexBulk(indexName: String, list: List[Any]): Unit = {
    val jestClient: JestClient = getClient
    val bulkBuilder: Bulk.Builder = new Bulk.Builder().defaultIndex(indexName).defaultType("_doc")
    for (doc <- list) {
      val index: Index = new Index.Builder(doc).build()
      bulkBuilder.addAction(index)
    }

    val items: util.List[BulkResult#BulkResultItem] = jestClient.execute(bulkBuilder.build()).getItems
    println("保存=" + items.size())
    close(jestClient)
  }

  //  def executeIndexBulk(indexName: String, list: List[Any], idColumn: String): Unit = {
  //    val bulkBuilder: Bulk.Builder = new Bulk.Builder().defaultIndex(indexName).defaultType("_doc")
  //    for (doc <- list) {
  //      val indexBuilder = new Index.Builder(doc)
  //      if (idColumn != null) {
  //        val id: String = BeanUtils.getProperty(doc, idColumn)
  //        indexBuilder.id(id)
  //      }
  //      val index: Index = indexBuilder.build()
  //      bulkBuilder.addAction(index)
  //    }
  //    val jestClient: JestClient = getClient
  //
  //    val result: BulkResult = jestClient.execute(bulkBuilder.build())
  //    if (result.isSucceeded) {
  //      println("保存成功:" + result.getItems.size())
  //    }
  //  }

}
