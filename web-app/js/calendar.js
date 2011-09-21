jQuery(function() {
    jQuery.datepicker.setDefaults(jQuery.datepicker.regional["ja"]);
    jQuery(".datepicker").datepicker({
        dateFormat: 'yy-mm-dd',
        showOn: "button",
        buttonImage: "/irclog/images/calendar.png",
        buttonImageOnly: true,
        showButtonPanel: true,
        showOtherMonths: true,
        selectOtherMonths: true,
    }).change(function() {
        var targetDate = jQuery(this).val().replace(/-/g, '');
        var channel = jQuery('#select-single').val().replace(/^#/, '');
        var url = '/irclog/the/' + channel + '/' + targetDate + '/';
        document.location = url;
    });
});
