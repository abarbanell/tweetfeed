# TweetFeed - spark / scala version	

listen to a twitter stream with a local Spark application, inspired by one of the samples in Andy Petrella's 
[Spark Notebook](http://spark-notebook.io/)



## Raspberry Pi ?

experiment - can we run this on the Raspberry Pi?

platform: Raspberry Pi 3 with 1 GB memory, Jessie version of Raspbian. sbt 0.13.1 and jdk 1.8 installed

You need to restrict the memory for this with sbt -mem xxx (where xxx is MB) :

```
tobias@rpi06:~/github/abarbanell/tweetfeed $ sbt run -mem 256
[info] Loading project definition from /home/tobias/github/abarbanell/tweetfeed/project
[info] Set current project to IdeaProjects (in build file:/home/tobias/github/abarbanell/tweetfeed/)
[info] Updating {file:/home/tobias/github/abarbanell/tweetfeed/}tweetfeed...
...
```

and you need time for everything to load the first time, easily 5 min or more tp start, when you haven't
run a scala app before and all libraries need to be fetched by sbt.


