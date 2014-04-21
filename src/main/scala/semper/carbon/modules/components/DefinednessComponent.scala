package semper.carbon.modules.components

import semper.carbon.boogie.{Statements, Stmt}
import semper.sil.{ast => sil}
import semper.sil.verifier.PartialVerificationError

/**
 * Takes care of determining whether expressions are well-formed.
 */
trait DefinednessComponent extends Component {

  /**
   * Free assumptions about an expression.
   */
  def freeAssumptions(e: sil.Exp): Stmt = Statements.EmptyStmt

  /**
   * Proof obligations for a given expression. See below for "makeChecks" description
   */
  def simplePartialCheckDefinedness(e: sil.Exp, error: PartialVerificationError, makeChecks: Boolean): Stmt = Statements.EmptyStmt

  /**
   * Proof obligations for a given expression.  The first part of the result is used before
   * visiting all subexpressions, then all subexpressions are checked for definedness, and finally
   * the second part of the result is used.
   *
   * The makeChecks argument can be set to false to cause the expression to be explored (and 
   * corresponding unfoldings to be executed), but no other checks to actually be made. Note that this method
   * must be overridden for this parameter to be used.
   */
  def partialCheckDefinedness(e: sil.Exp, error: PartialVerificationError, makeChecks: Boolean): (() => Stmt, () => Stmt) =
    (() => simplePartialCheckDefinedness(e, error, makeChecks), () => Statements.EmptyStmt)
}
