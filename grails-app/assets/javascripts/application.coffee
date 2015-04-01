#= encoding UTF-8
#= require jquery-2.1.3.js
#= require underscore.js
#= require_self
#= require_tree views

# this should be fastter than all
console.log_ = console.log
console.log = ->
  null

jQuery ->
  (->
    $('#spinner')
      .ajaxStart -> $(@).fadeIn()
      .ajaxStop -> $(@).fadeOut()
  )()

  # Help caption
  (->
    $(".help-button").click ->
      $("#" + this.id + "-caption").slideToggle(100)
  )()

  # Link with empty referer header (dereferrer)
  (->
    $.extend
      openLink: (target) ->
        url = target.href
        w = window.open()
        w.document.write('<meta http-equiv="refresh" content="0;url=' + url + '">')
        w.document.close()
        return false
  )()

  # Escape URL
  (->
    $.extend
      escapeUrl: (url) ->
        return _.escape(url).replace(/(https?:\/\/[-_.!~*'()a-zA-Z0-9;\/\?:@&=+$,%#]+)/g, '<a href="$1" onclick="$.openLink(this); return false">$1</a>')
  )()

  # Check whether it's visible in screen
  (
    $.extend
      isVisibleInScreen: ($element) ->
        distanceFromTop = $element.offset().top - $(window).height()
        return $(window).scrollTop() > distanceFromTop
  )()

  # Date utils
  (
    $.extend
      dateOfBefore: (nDaysAgo) ->
        date = new Date()
        date.setDate(date.getDate() - nDaysAgo)
        return date
  )()
