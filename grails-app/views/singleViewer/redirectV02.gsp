<script type="text/javascript">
  var match = location.pathname.match("^/irclog/the/([^/]*)/(\\d{4})(\\d{2})(\\d{2})\/?.*");
  var channel = match[1];
  var date = match[2] + "-" + match[3] + "-" + match[4];
  var permaId = (location.hash) ? location.hash.substring(1) : "";
  var redirectUrl = location.protocol + "//" + location.host + "/irclog/" + date + "/" + channel + "/" + permaId;
  location.href = redirectUrl;
</script>