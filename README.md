[![Build Status](https://travis-ci.org/earldouglas/scala-cd.svg?branch=master)](https://travis-ci.org/earldouglas/scala-cd)
[![Coverage Status](https://coveralls.io/repos/earldouglas/scala-cd/badge.png)](https://coveralls.io/r/earldouglas/scala-cd)

# Continuous deployment for Scala

*June 7, 2014*

Here I outline the steps to take to enable continuous deployment for Scala
Web projects, including GitHub push-triggered building and testing, code test
coverage, stats tracking, reporting, badging, and deployment to Heroku.

This builds on the steps outlined in [continuous integration for scala](https://github.com/earldouglas/scala-ci#continuous-integration-for-scala).

1. Create a Heroku app
1. Fork [xwp-template](https://github.com/earldouglas/xwp-template)
  * The steps below assume the project name is changed to `scala-cd` in *build.sbt*:

        ```scala
        name := "scala-cd"
        ```

1. Follow steps in [continuous integration for scala](https://github.com/earldouglas/scala-ci#continuous-integration-for-scala)
1. Make an sbt task to create a predictable symlink to the *.war* package

      ```scala
      val linkWar = taskKey[Unit]("Symlink the packaged .war file")
        
      linkWar := {
        val (art, pkg) = packagedArtifact.in(Compile, packageWar).value
        import java.nio.file.Files
        val link = (target.value / (art.name + "." + art.extension))
        link.delete
        Files.createSymbolicLink(link.toPath, pkg.toPath)
      }
      ```

1. Add encrypted Heroku api key to the Travis CI configuration:

      ```bash
      travis encrypt HEROKU_API_KEY=`heroku auth:token` --add
      ```

1. Configure Travis CI to deploy to Heroku

      ```yaml
      script: sbt container:start coveralls container:stop package linkWar
      after_success:
      - wget -qO- https://toolbelt.heroku.com/install-ubuntu.sh | sh
      - heroku plugins:install https://github.com/heroku/heroku-deploy
      - heroku deploy:war --war target/scala-cd.war --app scala-cd
      ```
