## IRCLogViewer

This is a simple viewer of IRC log archives.

It provides following features:

* Simple viewer of logs on a channel at a day.
* Search service.
* Log arcumulation service by IRC bot, powered by [Girbot](https://github.com/nobeans/gircbot).
* Pushing information from a server by using Websocket powered by [Vert.x](http://vertx.io/).
* Weekly summary view.
* User authentication and authrization powered by [Spring Security Core Plugin](http://grails.org/plugin/spring-security-core)
* Recent topic list.
* etc.

## Getting start

To try it, clone this repository from github and execute the following command:

```
$ grails -Dircbot.disable=true run-app
```

So the application will be started, and you can access to `http://localhost:8080/irclog`.

## Code Status

* [![Build Status](https://travis-ci.org/nobeans/irclog-viewer.svg?branch=master)](https://travis-ci.org/nobeans/irclog-viewer)

## License

IRCLogViewer is released under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0)

