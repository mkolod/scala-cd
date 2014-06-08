[![Build Status](https://travis-ci.org/earldouglas/scala-cd.svg?branch=master)](https://travis-ci.org/earldouglas/scala-cd)
[![Coverage Status](https://coveralls.io/repos/earldouglas/scala-cd/badge.png)](https://coveralls.io/r/earldouglas/scala-cd)

# Continuous deployment for Scala

*June 7, 2014*

Here I outline the steps to take to enable continuous deployment for Scala
Web projects that are based on [xsbt-web-plugin](https://github.com/earldouglas/xsbt-web-plugin/).

This builds on [continuous integration for scala](https://github.com/earldouglas/scala-ci#continuous-integration-for-scala),
adding automated *.war* file deployment to Heroku.

## Heroku

First, create a new [Heroku](http://heroku.com/) app.  In the steps below, we'll
call the app *scala-sd*.

## Scala Web project

If you don't already have a Scala Web project that you want to use, create a new
one by forking [xwp-template](https://github.com/earldouglas/xwp-template).
Note that xwp-template has a `name` setting set to `xwp-template`, but we use
the name `scala-cd` in the steps below:

*build.sbt:*

```scala
name := "scala-cd"
```

## Continuous integration

Follow steps outlined in [continuous integration for scala](https://github.com/earldouglas/scala-ci#continuous-integration-for-scala).

## Automate deployment to Heroku

When sbt packages a Web project, it creates a package file that includes is a
little hard to predict, since it includes both the project version and the Scala
version in its file name.  To make things easier, we want a predictable path to
the *.war* package, which we'll create with a simple sbt task.

*build.sbt:*

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

This creates a symbolic link *target/scala-cd.war* to the sbt-generated
artifact *target/scala-2.10/scala-cd_2.10-0.1.0-SNAPSHOT.war*.

Next, we need to provide Travis CI with our Heroku deployment credentials, so let's
encrypt them and add them to *.travis.yml*.  See the related
[Travis CI docs](http://docs.travis-ci.com/user/deployment/heroku/)
for more information.

Run the following from your project directory:

```bash
travis encrypt HEROKU_API_KEY=`heroku auth:token` --add
```

This adds an encrypted version of your Heroku API Key to *.travis.yml*:

```yaml
env:
  global:
    secure: [a long, ecnrypted string]
```

Finally, we configure Travis CI to deploy successful builds of the *master*
branch to Heroku.  Remove the old `script` line from *.travis.yml*, and replace
it with the following.

*.travis.yml:*

```yaml
script: sbt coveralls package linkWar
after_success:
- if [[ "$TRAVIS_BRANCH" == "master" ]]; then
  wget -qO- https://toolbelt.heroku.com/install-ubuntu.sh | sh ;
  heroku plugins:install https://github.com/heroku/heroku-deploy ;
  heroku deploy:war --war target/scala-cd.war --app scala-cd ;
fi
```

Now when we push changs to GitHub, Travis CI picks them up, and
[runs a build](https://travis-ci.org/earldouglas/scala-cd/builds) via
`sbt coveralls package linkWar`.  If the changes were pushed to the master
branch, Travis CI pushes the packaged *.war* file [to Heroku](http://scala-cd.herokuapp.com/).
