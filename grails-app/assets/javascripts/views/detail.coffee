#= encoding UTF-8
#= require jquery.js
#= require jquery.dateFormat-1.0.js
#= require knockout-2.2.1.js
#= require jquery.history.js

jQuery ->
  # Trigger only for the target page
  return if $(document.body).data('controller') != 'detail'

  #--------------------------------------------------
  # Model
  #--------------------------------------------------
  class Channel
    constructor: (@name) ->
      @shortName = @name.replace(/^#/, '')

    @list: ko.observableArray()
    @current: ko.observable()
    @updateList: ->
      # TODO append currentChannel for archived channel
      @list.removeAll()
      $.ajax async: false, url: '/irclog/detail/channelList', data: {'channel': Channel.current() }, success: (data) =>
        $.each data, (i, channelName) =>
          @list.push new Channel(channelName)

  class DateHolder
    @before: ko.observable()
    @after: ko.observable()
    @latest: ko.observable()
    @current: ko.observable()
    @updateRelatedDates: ->
      $.getJSON '/irclog/detail/relatedDateList', {'channel': Channel.current(), 'date': @current()}, (data) =>
        @before data.before
        @after data.after
        @latest data.latest
      @event "updated"
    @event: ko.observable "not_initialized" #=> "need_update", "updated"
    @debugMode: ->
      @event.subscribe (event) =>
        console.log "DateHolder:#{event}"

  class Irclog
    constructor: (@time, @nick, @message, @type, @permaId) ->
      # do nothing

    @list: ko.observableArray()
    @selectedPermaId: ko.observable()
    @updateList: ->
      return unless Channel.current() or DateHolder.current()
      $.getJSON '/irclog/detail/irclogList', {'channel': Channel.current(), 'date': DateHolder.current() }, (data) =>
        @list.removeAll()
        $.each data, (i, irclog) =>
          @list.push new Irclog(irclog.time, irclog.nick, irclog.message, irclog.type, irclog.permaId)
        @event("updated")
    @event: ko.observable("not_initialized") #=> "initialized", "need_update", "updated", "pushed_state", "changed_highlight"
    @debugMode: ->
      @event.subscribe (event) =>
        console.log "Irclog:#{event}"
    @addToList: (irclog) ->
      @list.push irclog
      @event("updated_one")

  #--------------------------------------------------
  # View Model
  #--------------------------------------------------
  class WindowViewModel
    constructor: ->
      @pageName = ko.computed =>
        "\##{Channel.current()}@#{DateHolder.current()}" + if Irclog.selectedPermaId() then "(#{Irclog.selectedPermaId().substring(0, 6)})" else ''
      @pageContextPath = ko.computed =>
        "/irclog/#{DateHolder.current()}/#{Channel.current()}" + if Irclog.selectedPermaId() then "/#{Irclog.selectedPermaId()}" else ''
      @needPushState = ko.observable(false)
      @permaLink = ko.computed =>
        "#{location.protocol}//#{location.host}#{@pageContextPath()}"
      @isSupportPushState = ko.computed =>
        History.enabled

      History.Adapter.bind window, 'statechange', =>
        state = History.getState()
        console.log 'statechange', state.data
        DateHolder.current state.data.date
        Channel.current state.data.channel
        Irclog.selectedPermaId state.data.permaId

        # avoid pushState temporarily for popstate
        @needPushState false
        DateHolder.event "need_update"
        Irclog.event "need_update"

      Irclog.event.subscribe (event) =>
        if event in ["displayed", "changed_highlight"]
          if @needPushState()
            @pushState()
            Irclog.event "pushed_state"

          # it's important doing after pushState/replaceState.
          document.title = @pageName()

        @needPushState true

    pushState: ->
      state = {date: DateHolder.current(), channel: Channel.current(), permaId: Irclog.selectedPermaId()}
      History.pushState state, @pageName(), @pageContextPath()

    @instance: new WindowViewModel()

  class ChannelListViewModel
    constructor: (channelShortName) ->
      Channel.current channelShortName

      @channelList = Channel.list
      @currentChannel = Channel.current

      # update only at initialization phase because channel list isn't changed.
      Channel.updateList()

    changeChannel: ->
      Irclog.selectedPermaId null
      Irclog.event "need_update"
      DateHolder.event "need_update"

  class DateViewModel
    constructor: (date) ->
      DateHolder.current date

      @showBeforeDate = ko.computed =>
        DateHolder.before()
      @showAfterDate = ko.computed =>
        DateHolder.after()
      @showLatestDate = ko.computed =>
        DateHolder.latest() and DateHolder.latest() != DateHolder.after()
      @currentDate = DateHolder.current

      DateHolder.event.subscribe (event) =>
        DateHolder.updateRelatedDates() if event == "need_update"

      $(".datepicker").datepicker(
        dateFormat: 'yy-mm-dd'
        showOn: "button"
        buttonImage: "/irclog/assets/calendar.png"
        buttonImageOnly: true
        showButtonPanel: true
        showOtherMonths: true
        selectOtherMonths: true
      ).change =>
        @changeDate $(".datepicker").val()

      DateHolder.event "need_update"

    toBeforeDate: ->
      @changeDate DateHolder.before()
    toAfterDate: ->
      @changeDate DateHolder.after()
    toLatestDate: ->
      @changeDate DateHolder.latest()
    changeDate: (date) ->
      DateHolder.current date
      Irclog.selectedPermaId null
      DateHolder.event "need_update"
      Irclog.event "need_update"

  class IrclogViewModel
    constructor: (@showingAllTypes, @irclog) ->
      @time = @irclog.time
      @nick = @irclog.nick
      @permaId = @irclog.permaId
      @message = ko.computed =>
        $.escapeUrl(@irclog.message)
      @highlighted = ko.computed =>
        Irclog.selectedPermaId() == @permaId
      @hovering = ko.observable(false)

      @cssClassOfRow = ko.computed =>
        cssClass = ["irclog", @irclog.type]
        cssClass.push if @isEssentialType() then 'essentialType' else 'optionType'
        cssClass.push 'highlight' if @highlighted()
        cssClass.push 'hidden' if !@showingAllTypes() and !@isEssentialType()
        cssClass.join ' '

      @cssClassOfTools = ko.computed =>
        cssClass = ['permalink']
        cssClass.push ['hidden'] unless @hovering() or @highlighted()
        cssClass.join ' '

      @cssClassOfNick = ko.computed =>
        'irclog-nick ' + @nick

    isEssentialType: ->
      $("#essentialTypes option[value=#{@irclog.type}]").size() > 0
    toggleHighlight: ->
      Irclog.selectedPermaId if @highlighted() then null else @permaId
      Irclog.event "changed_highlight"
    showTools: ->
      @hovering(true)
    hideTools: ->
      @hovering(false)

  class IrclogListViewModel
    constructor: (permaId) ->
      Irclog.selectedPermaId permaId

      @irclogList = ko.observableArray()

      @showPermaLink = ko.computed =>
        !WindowViewModel.instance.isSupportPushState()
      @permaLink = WindowViewModel.instance.permaLink

      @countEssentialTypes = ko.computed =>
        (irclog for irclog in @irclogList() when irclog.isEssentialType()).length
      @countAllTypes = ko.computed =>
        @irclogList().length

      @showingAllTypes = ko.observable(false)

      Irclog.event.subscribe (event) =>
        if event == "need_update"
          Irclog.updateList()
        else if event == "updated"
          @irclogList.removeAll()
          @irclogList.push new IrclogViewModel(@showingAllTypes, irclog) for irclog in Irclog.list()
          @connectWebsocketIfNeeds()
          Irclog.event "displayed"
        else if event == "updated_one"
          @irclogList.push new IrclogViewModel(@showingAllTypes, _.last(Irclog.list()))
          $newTr = $("tr.irclog:last-child")
          if $.isVisibleInScreen $newTr
            $newTr.effect "highlight", 1500
          else
            if $("#scrollToBottom:visible").size() == 0 # not to duplicate
              console.log "Not displayed #scrollToBottom yet"
              $("#scrollToBottom").fadeIn().click ->
                $('html,body').animate {scrollTop: $newTr.offset().top}
              $(window).scroll ->
                if $.isVisibleInScreen($newTr)
                  $(window).unbind "scroll"
                  $("#scrollToBottom").fadeOut()
                  # should highlight the target and all brother after it
                  $newTr.find("~").addBack().effect "highlight", 1500

          Irclog.event "displayed"

      DateHolder.event.subscribe (event) =>
        if event == "need_udpate"
          @closeWebsocket()

      # only at intialization phase
      initHook = Irclog.event.subscribe (event) =>
        if event == "displayed"
          initHook.dispose()

          # show all option irclog and scroll to highlighted one.
          for irclog in @irclogList() when irclog.highlighted()
            @showingAllTypes(true) unless irclog.isEssentialType()
            $("body").animate { scrollTop: $('.irclog.highlight').offset()?.top }

          Irclog.event "initialized"

      Irclog.event "need_update"

    focusPermaLink: ->
      $(".permaLink input").select()

    closeWebsocket: ->
      @socket.close() if @socket and !@socket.closed

    connectWebsocketIfNeeds: ->
      # unsupported browser
      return unless window.WebSocket

      # can connect only for today
      return unless @isToday()

      # do nothing if already opened
      return if @socket and !@socket.closed

      # connecting
      @socket = new WebSocket("ws://#{$("body").data("server-name")}:8899/irclog/detail/#{Channel.current()}/#{$('#token').val()}")

      # setup handlers
      @socket.onmessage = (event) =>
        console.log "WebSocket received data", event.data
        json = $.parseJSON(event.data)
        unless @isToday()
          console.log "It's not for today, so ignored.", json
          return
        unless json.channelName == "##{Channel.current()}"
          console.log "It's not for this channel ##{Channel.current()}, so ignored.", json
          return
        irclog = new Irclog(@formatTime(new Date(json.time)), json.nick, json.message, json.type, json.permaId)
        Irclog.addToList(irclog)

      @socket.onopen = (event) =>
        console.log "WebSocket opened", event.data

      @socket.onclose = (event) =>
        console.log "WebSocket closed", event.data

      console.log "WebSocket connected", @socket

    isToday: ->
      return DateHolder.current() == @formatDate(new Date())

    formatDate: (date) ->
      return $.format.date(date, 'yyyy-MM-dd')

    formatTime: (date) ->
      return $.format.date(date, 'HH:mm:ss')

  #--------------------------------------------------
  # Setup
  #--------------------------------------------------
  # for DEBUG
  Irclog.debugMode()
  DateHolder.debugMode()

  # initial condition from URI
  [whole, date, channelShortName, permaId] = location.pathname.match(/^.*\/(\d{4}-\d{2}-\d{2})\/([^/]*)(?:\/([^/]*))?$/)

  ko.applyBindings new ChannelListViewModel(channelShortName), $("#condition-channel")[0]
  ko.applyBindings new DateViewModel(date), $("#condition-date")[0]
  ko.applyBindings new IrclogListViewModel(permaId), $(".list")[0]
