// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/.
//
// Copyright (c) 2011-2019 ETH Zurich.

package viper.carbon

import ch.qos.logback.classic.Logger
import viper.silver.frontend.{SilFrontend, SilFrontendConfig}
import viper.silver.logger.ViperStdOutLogger
import viper.silver.plugin.PluginAwareReporter
import viper.silver.reporter.{Reporter, StdIOReporter}
import viper.silver.verifier.{Verifier => SilVerifier}

/**
 * The main object for Carbon containing the execution start-point.
 */
object Carbon extends CarbonFrontend(StdIOReporter("carbon_reporter"), ViperStdOutLogger("Carbon", "INFO").get) {
  def main(args: Array[String]) {
    execute(args)
    specifyAppExitCode()
    sys.exit(appExitCode)
  }
}

class CarbonFrontend(override val reporter: PluginAwareReporter,
                     override val logger: Logger) extends SilFrontend {

  def this(reporter: Reporter, logger: Logger) = this(PluginAwareReporter(reporter), logger)

  private var carbonInstance: CarbonVerifier = _

  def createVerifier(fullCmd: String) = {
    carbonInstance = CarbonVerifier(Seq("Arguments: " -> fullCmd))

    carbonInstance
  }

  def configureVerifier(args: Seq[String]) = {
  	carbonInstance.parseCommandLine(args)

    carbonInstance.config
  }

  override def init(verifier: SilVerifier): Unit = {
    verifier match {
      case carbon: CarbonVerifier =>
        carbonInstance = carbon
      case _ =>
        sys.error( "Expected verifier to be an instance of CarbonVerifier but got an instance " +
                  s"of ${verifier.getClass}")
    }

    super.init(verifier)

    _config = carbonInstance.config
  }
}

class CarbonConfig(args: Seq[String]) extends SilFrontendConfig(args, "Carbon") {
  val boogieProverLog = opt[String]("proverLog",
    descr = "Prover log file written by Boogie (default: none)",
    default = None,
    noshort = true
  )

  val boogieOut = opt[String]("print",
    descr = "Write the Boogie output file to the provided filename (default: none)",
    default = None,
    noshort = true
  )

  val boogieOpt = opt[String]("boogieOpt",
  descr = "Option(s) to pass-through as options to Boogie (changing the output generated by Boogie is not supported) (default: none)",
  default = None,
  noshort = true
  )

  val boogieExecutable = opt[String]("boogieExe",
    descr = "Manually-specified full path to Boogie.exe executable (default: ${BOOGIE_EXE})",
    default = None,
    noshort = true
  )

  val Z3executable = opt[String]("z3Exe",
    descr = "Manually-specified full path to Z3.exe executable (default: ${Z3_EXE})",
    default = None,
    noshort = true
  )

  val disableAllocEncoding = opt[Boolean]("disableAllocEncoding",
    descr = "Disable Allocation-related assumptions (default: enabled)",
    default = None,
    noshort = true
  )

  val staticInlining = opt[Int]("SI",
    descr = "Static inlining up to a depth (default: disabled)",
    default = None,
    noshort = true
  )

  val maxInl = opt[Int]("maxInl",
    descr = "Maximum number of method calls or loop iterations inlined (default: unlimited)",
    default = None,
    noshort = true
  )

  val entry = opt[String]("entry",
    descr = "Entry point for static inlining (default: first method)",
    default = None,
    noshort = true
  )

  val noCheckSC = opt[Boolean]("noCheckSC",
    descr = "Check soundness condition for inlining (default: enabled)",
    default = None,
    noshort = true
  )

  val printSC = opt[Boolean]("printSC",
    descr = "Print the code when checking mono and framing (default: disabled)",
    default = None,
    noshort = true
  )

  val closureSC = opt[Boolean]("closureSC",
    descr = "Computes the soundness condition with closure for sequences of statements (default: disabled)",
    default = None,
    noshort = true
  )

  val pureFunctionsSC = opt[Boolean]("noFunPreSC",
    descr = "Deactivates a feature that helps checking the consistency of function preconditions in the structural soundness check (default: feature activated)",
    default = None,
    noshort = true
  )

  val modularSC = opt[Boolean]("modularSC",
    descr = "Uses a modular conservative approximation for the soundness condition (default: disabled)",
    default = None,
    noshort = true
  )

  /*
  val simpleWFM = opt[Boolean]("simpleWFM",
    descr = "Computes a simpler (bounded) WFM, but potentially unsound (default: disabled)",
    default = None,
    noshort = true
  )
   */

  val noSyntacticCheck = opt[Boolean]("disableSyntacticSC",
    descr = "Disable syntactic check to speed up checking soundness condition for inlining (default: enabled)",
    default = None,
    noshort = true
  )

  val ignoreAnnotations = opt[Boolean]("ignoreAnnotations",
    descr = "Disable syntactic check to speed up checking soundness condition for inlining (default: enabled)",
    default = None,
    noshort = true
  )

  verify()
}
