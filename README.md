[![Build Status](https://travis-ci.org/earldouglas/scala-cd.svg?branch=master)](https://travis-ci.org/earldouglas/scala-cd)
[![Coverage Status](https://coveralls.io/repos/earldouglas/scala-cd/badge.png)](https://coveralls.io/r/earldouglas/scala-cd)

# Continuous deployment for Scala

*June 7, 2014*

**Outline:**

1. Create a Heroku app
1. Fork xwp-template
1. Follow steps in [scala-ci](https://github.com/earldouglas/scala-ci#continuous-integration-for-scala)
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
  * Change `script`:
        ```yaml
        script: sbt coveralls package linkWar
        ```
  * Deploy after successfully packaging:
        ```yaml
        after_success:
        - wget -qO- https://toolbelt.heroku.com/install-ubuntu.sh | sh
        - heroku plugins:install https://github.com/heroku/heroku-deploy
        - heroku deploy:war --war target/scala-cd.war --app scala-cd
        ```
