package com.pharbers.spark.session

/**
  * Created by clock on 18-2-27.
  */
object spark_conn_obj {
    def apply(_applicationName: String): spark_conn_obj = new spark_conn_obj(_applicationName)
}

class spark_conn_obj(override val applicationName: String = "") extends spark_conn_instance
