import ammonite.ops._
import coursier.maven.MavenRepository
import mill._
import mill.scalajslib._
import mill.scalalib._

object Config {
  def scalaVersion       = "2.12.5"
  def scalaJSVersion     = "0.6.22"
  def bindingVersion     = "11.0.1"
  def mongoDriverVersion = "2.2.1"
  def akkaHttpVersion    = "10.1.1"
  def akkaStreamVersion  = "2.5.12"

  def sharedDependencies = Agg(
    ivy"com.lihaoyi::upickle::0.6.5",
    ivy"com.thoughtworks.binding::binding::$bindingVersion",
    ivy"com.lihaoyi::autowire::0.2.6"
  )

  def jvmDependencies = Agg(
    ivy"org.mongodb.scala::mongo-scala-driver:$mongoDriverVersion",
    ivy"com.typesafe.akka::akka-http:$akkaHttpVersion",
    ivy"com.typesafe.akka::akka-stream:$akkaStreamVersion"
  )

  def jsDependencies = Agg(
    ivy"com.thoughtworks.binding::dom::$bindingVersion",
    ivy"com.thoughtworks.binding::futurebinding::$bindingVersion",
    ivy"com.thoughtworks.binding::route::$bindingVersion"
  )
}

trait CommonWeb extends ScalaModule {
  def scalaVersion = Config.scalaVersion

  def scalacPluginIvyDeps = super.scalacPluginIvyDeps() ++ Agg(
    ivy"org.scalamacros:::paradise:2.1.1"
  )

  def ivyDeps = super.ivyDeps() ++ Config.sharedDependencies

  def sources = T.sources(
    millSourcePath / "src",
    millSourcePath / up / "shared" / "src"
  )

  /*def scalacOptions = super.scalacOptions() ++ Seq(
    "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
    "-encoding", "utf-8",                // Specify character encoding used by source files.
    "-explaintypes",                     // Explain type errors in more detail.
    "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
    "-language:higherKinds",             // Allow higher-kinded types
    "-language:implicitConversions",     // Allow definition of implicit functions called views
    "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
    "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
    "-Xfuture",                          // Turn on future language features.
    "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
    "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
    "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
    "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
    "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
    "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
    "-Xlint:option-implicit",            // Option.apply used implicit view.
    "-Xlint:package-object-classes",     // Class or object defined in package object.
    "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
    "-Xlint:unsound-match",              // Pattern match may not be typesafe.
    "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
    "-Ypartial-unification",             // Enable partial unification in type constructor inference
    "-Ywarn-dead-code",                  // Warn when dead code is identified.
    "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
    "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
    "-Ywarn-numeric-widen",              // Warn when numerics are widened.
    "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals",              // Warn if a local definition is unused.
    "-Ywarn-unused:params",              // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates",            // Warn if a private member is unused.
    "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
  )*/
}
object shared extends CommonWeb //needed for intellij

object jvm extends CommonWeb {
  def moduleDeps = Seq(shared)

  def ivyDeps = super.ivyDeps() ++ Config.jvmDependencies
}

object js extends CommonWeb with ScalaJSModule {
  def moduleDeps = Seq(shared)

  def bundle = T {
    val path            = fastOpt().path
    val resourcePath    = jvm.millSourcePath / "resources" / path.last
    val resourceMapPath = Path(resourcePath + ".map")
    rm(resourcePath)
    rm(resourceMapPath)
    cp(path, resourcePath)
    cp(Path(path + ".map"), resourceMapPath)
  }

  def scalaJSVersion = Config.scalaJSVersion

  def ivyDeps = super.ivyDeps() ++ Config.jsDependencies
}
