jQuery ->

  # Button to join a channel
  (->
    $(document).on "click", "#toggleJoinToSecretChannel.hidden", ->
      $("#joinToSecretChannel").slideDown(200)
      $("#join").addClass("active")
      $("#channelName").focus()
      $(this).toggleClass("hidden").toggleClass("shown")

    $(document).on "click", "#toggleJoinToSecretChannel.shown", ->
      $("#joinToSecretChannel").slideUp(100)
      $("#join").removeClass("active")
      $(this).toggleClass("hidden").toggleClass("shown")
  )()
