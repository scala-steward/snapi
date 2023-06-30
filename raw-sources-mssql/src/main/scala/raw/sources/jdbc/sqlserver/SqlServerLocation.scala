/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.sources.jdbc.sqlserver

import raw.sources.{CacheStrategy, RetryStrategy}

import java.io.Closeable
import raw.sources.jdbc._

class SqlServerLocation(
    cli: SqlServerClient,
    dbName: String,
    override val cacheStrategy: CacheStrategy,
    override val retryStrategy: RetryStrategy
) extends JdbcLocation(cli, "sqlserver", dbName) {

  override def rawUri: String = s"sqlserver:$dbName"

  override def listSchemas(): Iterator[String] with Closeable = {
    new Iterator[String] with Closeable {
      private val it = cli.listSchemas

      override def hasNext: Boolean = it.hasNext

      override def next(): String = s"sqlserver:$dbName/${it.next()}"

      override def close(): Unit = it.close()
    }
  }

}
