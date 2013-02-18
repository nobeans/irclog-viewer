jQuery ->

    # Help caption
    (->
        $(".help-button").click ->
            $("#" + this.id + "-caption").slideToggle(100)

        $(".defaultFocus").focus()
    )()

