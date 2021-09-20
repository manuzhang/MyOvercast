# MyOvercast

My [Overcast](https://overcast.fm/) listening history.

## Introduction

Since 2018, I've been addict to [Podcast](https://en.wikipedia.org/wiki/Podcast) and Overcast is my favorite and most used Podcast app.
It will be interesting to profile and share my listening history. Although Overcast doesn't support it natively, it allows me to
[exporting my data](https://overcast.fm/account/export_opml/extended) in OPML(XML). Hence, I believe it's doable and build this site,
which is also a practice of building website with Scala.

## How I Build This 

### Backend

1. Log into Overcast account and fetch data with [request-scala](https://github.com/com-lihaoyi/requests-scala)
2. Parse OPML and extract interesting fields with [scala-xml](https://github.com/scala/scala-xml)
3. Save data in json with [upickle](https://github.com/com-lihaoyi/upickle) and [os-lib](https://github.com/com-lihaoyi/os-lib)

### Frontend

Load json data and render with [Slinky](https://github.com/shadaj/slinky) (a framework to write [Scala.js](https://www.scala-js.org/) React app).
Other depenencies include [ScalaTyped](https://scalablytyped.org/) to use React UI libraries like [antd](https://ant.design/) and [scala-java-time](https://github.com/cquiroz/scala-java-time)
to work with `java.time`.

### Build

Create a daily run GitHub Action to rebuild and refresh site everyday.

## References

* [ScalablyTyped/SlinkyDemo](https://github.com/ScalablyTyped/SlinkyDemo)
* [lastcastfm/overcast-scrobbler-python](https://github.com/lastcastfm/overcast-scrobbler-python)