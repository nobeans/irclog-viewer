jQuery ->
  console.log = ->
    null

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
      $.ajax async: false, url: '/irclog/detail/channelList', success: (data) =>
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
    @event: ko.observable "not_initialized"
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
    @event: ko.observable("not_initialized")
    @debugMode: ->
      @event.subscribe (event) =>
        console.log "Irclog:#{event}"

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
        buttonImage: "/irclog/images/calendar.png"
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
          Irclog.event "displayed"

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
