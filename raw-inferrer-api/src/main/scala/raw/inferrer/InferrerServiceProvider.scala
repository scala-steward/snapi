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

package raw.inferrer

import java.util.ServiceLoader
import scala.collection.JavaConverters._
import raw.sources.SourceContext

object InferrerServiceProvider {

  private val services = ServiceLoader.load(classOf[InferrerServiceBuilder]).asScala.toArray

  def apply()(implicit sourceContext: SourceContext): InferrerService = {
    build()
  }

  private def build()(implicit sourceContext: SourceContext): InferrerService = {
    if (services.isEmpty) {
      throw new InferrerException("no inferrer service available")
    } else if (services.size > 1) {
      val implClassName = sourceContext.settings.getString("raw.inferrer.impl")
      services.find(p => p.name == implClassName) match {
        case Some(builder) => builder.build
        case None => throw new InferrerException(s"cannot find inferrer service: $implClassName")
      }
    } else {
      services.head.build
    }
  }

}
