jQuery ->
  #--------------------------------------------------
  # Model
  #--------------------------------------------------
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
        console.log "Received data", data
        @list.removeAll()
        ko.utils.arrayForEach data, (summary) =>
          @list.push new Summary(summary)
        @updateLastUpdatedDate()
        @event("loaded")

    @updateLastUpdatedDate: ->
      @lastUpdatedDate($.format.date(new Date(), 'yyyy-MM-dd'))

  #--------------------------------------------------
  # View Model
  #--------------------------------------------------
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
          console.log "Reloaded summary list", event

      @connectWebsocket()

    dateOfBefore: (nDaysAgo) ->
      $.format.date($.dateOfBefore(nDaysAgo), 'MM/dd(ddd)')

    connectWebsocket: ->
      # unsupported browser
      return unless window.WebSocket

      # do nothing if already opened
      return if @socket and !@socket.closed

      # connecting
      @socket = new WebSocket("ws://localhost:8898/irclog/summary/#{$('#token').val()}")

      # setup handlers
      @socket.onmessage = (event) =>
        console.log "WebSocket received data", event.data
        json = $.parseJSON(event.data)
        ko.utils.arrayForEach json, (newSummary) ->
          console.log "newSummary: ", newSummary
          # The channel which hasn't been displayed in a screen yet is just ignored.
          ko.utils.arrayForEach SummaryList.list(), (targetSummary) ->
            if targetSummary.channelId() == newSummary.channelId
              targetSummary.update newSummary

      @socket.onopen = (event) =>
        console.log "WebSocket opened", event.data

      @socket.onclose = (event) =>
        console.log "WebSocket closed", event.data

      console.log "WebSocket connected", @socket

  #--------------------------------------------------
  # Setup
  #--------------------------------------------------
  ko.applyBindings new SummaryListViewModel(), $(".summary-statement .list")[0]
  SummaryList.load()
