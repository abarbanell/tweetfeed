package de.abarbanell.tweetfeed

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter._

/**
  * Created by tobias on 13.02.17.
  */
object Main {

  def setLogLevel = {
    import org.apache.log4j.Logger
    import org.apache.log4j.Level

    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
  }

  def setTwitterKeys = {
    def $(s:String) = sys.env(s)
    System.setProperty("twitter4j.oauth.consumerKey", $("TWITTER_CONSUMER_KEY"))
    System.setProperty("twitter4j.oauth.consumerSecret", $("TWITTER_CONSUMER_SECRET"))
    System.setProperty("twitter4j.oauth.accessToken", $("TWITTER_ACCESS_TOKEN"))
    System.setProperty("twitter4j.oauth.accessTokenSecret", $("TWITTER_ACCESS_TOKEN_SECRET"))
    println("twitter keys set.")
  }

  def main(args: Array[String]): Unit = {
    // println("hello")
    setLogLevel
    setTwitterKeys

    val conf = new SparkConf().setAppName("TweetFeed").setMaster("local[2]")
    val ssc = new StreamingContext(conf,Seconds(5))

    val filters = Array("spark", "scala", "arduino", "nodejs", "raspberry")

    val twitterStream = TwitterUtils.createStream(ssc, None,  filters)

    // count by hashtags and sort
    val hashTags = twitterStream.flatMap(status => status.getText.split(" ").filter(_.startsWith("#")))

    val topCounts60 = hashTags.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(60))
      .map{case (topic, count) => (count, topic)}
      .transform(_.sortByKey(false))

    // Print popular hashtags
    topCounts60.foreachRDD(rdd => {
      val topList = rdd.take(10).toList
      val r = topList.map{case (count, tag) => s"$tag: $count"}
      println(s"Top 60 Hashtags: $r ")
    })

    twitterStream .window(Seconds(30), Seconds(30))
        .foreachRDD(rdd => {
          println("---- Top 100 Tweets -----")
          rdd.take(100).map(s => {
            val sender = s.getUser.getScreenName
            val text = s.getText
            println(s"@$sender tweeted: $text")
          })
          println("-------------------------")
        })
//      . filter{ s =>
//        s.getGeoLocation() != null
//      }
//      .map(s => (s.getGeoLocation().getLatitude(), s.getGeoLocation().getLongitude(), s.getText()))
//      .foreachRDD{rdd =>
//        geo.applyOn(rdd.take(100))
//      }

    ssc.start()

    ssc.awaitTerminationOrTimeout(5*60*1000) // timeout in ms
  }

}
