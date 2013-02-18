jQuery ->

    # Highlight of search key
    (->
        highlightSearchedWord = (type) ->
            searchKey = $('#search-' + type).val()
            return unless searchKey

            keys = searchKey.replace(/(^\s+)|(\s+$)/g, "").replace(/\s+/g," ").replace(/\$/g, "\$").split(' ')
            $(keys).each ->
                $('td.irclog-' + type).highlight(this)

        highlightSearchedWord('nick')
        highlightSearchedWord('message')
    )()

    # Enter = submit
    (->
        $("input,select").keydown (e) ->
            if e.keyCode == 13
                $("form[name=search]").submit()
                false
    )()

    # Calendar to select a day
    (->
        $.datepicker.setDefaults($.datepicker.regional["ja"])
        $("input.datepicker").datepicker({
            dateFormat: 'yy-mm-dd',
            showOn: "button",
            buttonImage: "/irclog/images/calendar.png",
            buttonImageOnly: true,
            showButtonPanel: true,
            showOtherMonths: true,
            selectOtherMonths: true,
        })

        updateDatepicker = ->
            if $("#search-period").val() == "oneday"
                $("#period-oneday-select").show()
                $("input.datepicker").removeAttr('disabled')
            else
                $("#period-oneday-select").hide()
                $("input.datepicker").attr('disabled', 'disabled')

        updateDatepicker() # just after loading
        $("#search-period").change(updateDatepicker)
    )()

