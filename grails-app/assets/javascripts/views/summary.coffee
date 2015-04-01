#= encoding UTF-8
#= require jquery-2.1.3.js
#= require jquery.dateFormat-1.0.js
#= require knockout-2.2.1.js

jQuery ->
  # Trigger only for the target page
  return if $(document.body).data('controller') != 'summary'

  #--------------------------------------------------
  # Model
  #--------------------------------------------------
  class Topic
    constructor: (topic) ->
      @permaId = ko.observable topic.permaId
      @time = ko.observable new Date(topic.time)
      @channelName = ko.observable topic.channelName
      @nick = ko.observable topic.nick
      @message = ko.observable topic.message

      @event = ko.observable("initialized") #=> "updated"

  class TopicList
    @maxSize: 5

    @list: ko.observableArray()

    @event: ko.observable("not_initialized") #=> "initialized", "loaded", "added"

    @load: ->
      $.getJSON '/irclog/summary/topicList', (data) =>
        console.log "Received topicList", data
        @list.removeAll()
        ko.utils.arrayForEach data, (topic) =>
          @list.push new Topic(topic)
        @event "loaded"

    @add: (topic) ->
      # keep length under maxSize
      @list.pop() if @list().length >= @maxSize
      @list.unshift topic

      # notify to subscriber
      @event "none" # it's required because changing to same status cannot be passed to subscriber
      @event "added"

  class Summary
    constructor: (summary) ->
      @channelId = ko.observable summary.channelId
      @channelName = ko.observable summary.channelName
      @today = ko.observable summary.today
      @yesterday = ko.observable summary.yesterday
      @twoDaysAgo = ko.observable summary.twoDaysAgo
      @threeDaysAgo = ko.observable summary.threeDaysAgo
      @fourDaysAgo = ko.observable summary.fourDaysAgo
      @fiveDaysAgo = ko.observable summary.fiveDaysAgo
      @sixDaysAgo = ko.observable summary.sixDaysAgo
      @total = ko.observable summary.total
      @latestTime = ko.observable new Date(summary.latestTime)
      @latestNick = ko.observable summary.latestNick
      @latestMessage = ko.observable summary.latestMessage

      @event = ko.observable("initialized") #=> "updated"

    update: (summary) ->
      changed = @today() != summary.today ||
        @yesterday() != summary.yesterday ||
        @twoDaysAgo() != summary.twoDaysAgo ||
        @threeDaysAgo() != summary.threeDaysAgo ||
        @fourDaysAgo() != summary.fourDaysAgo ||
        @fiveDaysAgo() != summary.fiveDaysAgo ||
        @sixDaysAgo() != summary.sixDaysAgo ||
        @total() != summary.total
      return unless changed

      @today summary.today
      @yesterday summary.yesterday
      @twoDaysAgo summary.twoDaysAgo
      @threeDaysAgo summary.threeDaysAgo
      @fourDaysAgo summary.fourDaysAgo
      @fiveDaysAgo summary.fiveDaysAgo
      @sixDaysAgo summary.sixDaysAgo
      @total summary.total
      @latestTime new Date(summary.latestTime)
      @latestNick summary.latestNick
      @latestMessage summary.latestMessage

      # notify to subscriber
      @event "none" # it's required because changing to same status cannot be passed to subscriber
      @event "updated"

      # for updating header
      SummaryList.updateLastUpdatedDate()
      console.log "Updated summary", summary

  class SummaryList
    @list: ko.observableArray()

    @event: ko.observable("not_initialized") #=> "initialized", "updated"

    @lastUpdatedDate: ko.observable()

    @load: ->
      $.getJSON '/irclog/summary/summaryList', (data) =>
        console.log "Received summaryList", data
        @list.removeAll()
        ko.utils.arrayForEach data, (summary) =>
          @list.push new Summary(summary)
        @updateLastUpdatedDate()
        @event "loaded"

    @updateLastUpdatedDate: ->
      @lastUpdatedDate($.format.date(new Date(), 'yyyy-MM-dd'))

  #--------------------------------------------------
  # View Model
  #--------------------------------------------------
  class TopicViewModel
    constructor: (@topic) ->
      @formattedTime = ko.computed => $.format.date(@topic.time(), 'yyyy-MM-dd HH:mm:ss')
      @topicLink = ko.computed => "/irclog/#{$.format.date(@topic.time(), 'yyyy-MM-dd')}/#{@topic.channelName().replace(/#/, '')}/#{@topic.permaId()}"

  class TopicListViewModel
    constructor: () ->
      @topicList = ko.observableArray()

      TopicList.event.subscribe (event) =>
        if event == "loaded"
          @topicList.removeAll()
          @topicList.push new TopicViewModel(topic) for topic in TopicList.list()
          console.log "Loaded topicList"
        if event == "added"
          @topicList.removeAll()
          @topicList.push new TopicViewModel(topic) for topic in TopicList.list()
          $(".summary-topic .list li:first").effect "highlight", 1500
          console.log "Added topicList"

      @connectWebsocket()

    connectWebsocket: ->
      # unsupported browser
      return unless window.WebSocket

      # do nothing if already opened
      return if @socket and !@socket.closed

      # connecting
      @socket = new WebSocket("ws://#{$("body").data("server-name")}:8897/irclog/topic/#{$('#topic-token').val()}")

      # setup handlers
      @socket.onmessage = (event) =>
        console.log "WebSocket for topic received data", event.data
        json = $.parseJSON(event.data)
        TopicList.add new Topic(json)

      @socket.onopen = (event) =>
        console.log "WebSocket for topic opened", event.data

      @socket.onclose = (event) =>
        console.log "WebSocket for topic closed", event.data

      console.log "WebSocket for topic connected", @socket

  class SummaryViewModel
    constructor: (@summary) ->
      @channelName       = ko.computed => @summary.channelName()
      @todayCount        = ko.computed => @formatCount @channelName(), @dateOfBefore(0), @summary.today()
      @yesterdayCount    = ko.computed => @formatCount @channelName(), @dateOfBefore(1), @summary.yesterday()
      @twoDaysAgoCount   = ko.computed => @formatCount @channelName(), @dateOfBefore(2), @summary.twoDaysAgo()
      @threeDaysAgoCount = ko.computed => @formatCount @channelName(), @dateOfBefore(3), @summary.threeDaysAgo()
      @fourDaysAgoCount  = ko.computed => @formatCount @channelName(), @dateOfBefore(4), @summary.fourDaysAgo()
      @fiveDaysAgoCount  = ko.computed => @formatCount @channelName(), @dateOfBefore(5), @summary.fiveDaysAgo()
      @sixDaysAgoCount   = ko.computed => @formatCount @channelName(), @dateOfBefore(6), @summary.sixDaysAgo()
      @totalCount        = ko.computed => @summary.total()
      @latestTime        = ko.computed => $.format.date(@summary.latestTime(), 'yyyy-MM-dd HH:mm:ss')
      @latestNick        = ko.computed => @summary.latestNick()
      @latestMessage     = ko.computed => @summary.latestMessage()

      @rowId = ko.computed => "summary-channel-" + @summary.channelId()
      @searchAllLogsLink = ko.computed => "/irclog/viewer?channel=#{@summary.channelName().replace(/#/, '%23')}&period=all&nick=&message=&_type="
      @channelLink = ko.computed => "/irclog/channel/show/#{@summary.channelId()}"

      @summary.event.subscribe (event) =>
        if event == "updated"
          $("#" + @rowId()).effect "highlight", 1500

    dateOfBefore: (nDaysAgo) ->
      $.format.date($.dateOfBefore(nDaysAgo), 'yyyy-MM-dd')

    formatCount: (channelName, date, count) ->
      if count == 0 then count else return "<a href=\"/irclog/#{date}/#{channelName.replace(/#/, '')}\">#{count}</a>"

  class SummaryListViewModel
    constructor: () ->
      @summaryList = ko.observableArray()

      # header
      @todayLabel        = ko.observable()
      @yesterdayLabel    = ko.observable()
      @twoDaysAgoLabel   = ko.observable()
      @threeDaysAgoLabel = ko.observable()
      @fourDaysAgoLabel  = ko.observable()
      @fiveDaysAgoLabel  = ko.observable()
      @sixDaysAgoLabel   = ko.observable()

      # When lastUpdatedDate is changed from previous value, it means updating header is needed.
      SummaryList.lastUpdatedDate.subscribe (event) =>
        @todayLabel        @dateOfBefore(0)
        @yesterdayLabel    @dateOfBefore(1)
        @twoDaysAgoLabel   @dateOfBefore(2)
        @threeDaysAgoLabel @dateOfBefore(3)
        @fourDaysAgoLabel  @dateOfBefore(4)
        @fiveDaysAgoLabel  @dateOfBefore(5)
        @sixDaysAgoLabel   @dateOfBefore(6)
        console.log "Updated header", event

      SummaryList.event.subscribe (event) =>
        if event == "loaded"
          @summaryList.removeAll()
          @summaryList.push new SummaryViewModel(summary) for summary in SummaryList.list()
          console.log "Loaded summaryList", event

      @connectWebsocket()

    dateOfBefore: (nDaysAgo) ->
      $.format.date($.dateOfBefore(nDaysAgo), 'MM/dd(ddd)')

    connectWebsocket: ->
      # unsupported browser
      return unless window.WebSocket

      # do nothing if already opened
      return if @socket and !@socket.closed

      # connecting
      @socket = new WebSocket("ws://#{$("body").data("server-name")}:8898/irclog/summary/#{$('#summary-token').val()}")

      # setup handlers
      @socket.onmessage = (event) =>
        console.log "WebSocket for summary received data", event.data
        json = $.parseJSON(event.data)
        ko.utils.arrayForEach json, (newSummary) ->
          console.log "newSummary: ", newSummary
          # The channel which hasn't been displayed in a screen yet is just ignored.
          ko.utils.arrayForEach SummaryList.list(), (targetSummary) ->
            if targetSummary.channelId() == newSummary.channelId
              targetSummary.update newSummary

      @socket.onopen = (event) =>
        console.log "WebSocket for summary opened", event.data

      @socket.onclose = (event) =>
        console.log "WebSocket for summary closed", event.data

      console.log "WebSocket for summary connected", @socket

  #--------------------------------------------------
  # Setup
  #--------------------------------------------------
  ko.applyBindings new TopicListViewModel(), $(".summary-topic .list")[0]
  ko.applyBindings new SummaryListViewModel(), $(".summary-statement .list")[0]
  TopicList.load()
  SummaryList.load()
