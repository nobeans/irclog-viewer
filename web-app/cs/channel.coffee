jQuery ->

    # Button to join a channel
    (->
        $("#toggleJoinToSecretChannel").toggle ->
            $("#joinToSecretChannel").slideDown(200)
            $("#join").addClass("active")
            $("#channelName").focus()
        , ->
            $("#joinToSecretChannel").slideUp(100)
            $("#join").removeClass("active")
    )()
