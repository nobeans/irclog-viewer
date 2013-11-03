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

    $(".defaultFocus").focus()
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
