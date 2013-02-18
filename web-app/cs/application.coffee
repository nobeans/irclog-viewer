jQuery ->
  (->
    $('#spinner')
      .ajaxStart ->
        $(@).fadeIn()
      .ajaxStop ->
        $(@).fadeOut()
  )()

