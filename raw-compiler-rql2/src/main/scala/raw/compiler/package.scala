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

package raw

import raw.sources.Location

import java.nio.file.Path

// TODO (msb): Move this elsewhere. It's so reduced now that probably can be made available in Executor?
package object compiler {

  /**
   * An interpolator for RQL code.
   */
  implicit class RQLInterpolator(val sc: StringContext) extends AnyVal {

    def rql(args: Any*): String = {
      val strings = sc.parts.iterator
      val expressions = args.iterator
      val buf = new StringBuffer(strings.next)

      while (strings.hasNext) {
        buf.append(doLookup(expressions.next))
        buf.append(strings.next)
      }
      buf.toString
    }

    private def doLookup(arg: Any): String = {
      arg match {
        case l: Location => l.rawUri
        case p: Path => "file:" + p.toAbsolutePath.toString.replace("\\", "\\\\")
        case s: String => s
        case _ => arg.toString
      }
    }
  }

}
