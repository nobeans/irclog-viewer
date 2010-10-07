/*--------------------------------------------------------------------------*
 *  
 *  wordBreak JavaScript Library for Opera & Firefox
 *  
 *  MIT-style license. 
 *  
 *  2008 Kazuma Nishihata 
 *  http://www.to-r.net
 *
 *  2009-02-01 modified by nobeans.
 *  
 *--------------------------------------------------------------------------*/

(function() {
    if (window.opera || navigator.userAgent.indexOf("Firefox") != -1) {
        var breakChar = String.fromCharCode(8203);
        var recursiveParse = function(pNode) {
            var children = pNode.childNodes;
            for (var i = 0; i < children.length; i++) {
                var cNode = children[i];
                var cNodeType = cNode.nodeType;
                if (cNodeType == Node.ELEMENT_NODE) {
                    recursiveParse(cNode);
                } else if (cNodeType == Node.TEXT_NODE) {
                    var cNodeValue = cNode.nodeValue;
                    if (cNodeValue.match(/[^\n ]/)) {
                        cNode.nodeValue = cNodeValue.replace("", breakChar, "g");
                    }
                }
            }
        }
        var wordBreak = function() {
            $$(".wordBreak").each(function(ele) {
                recursiveParse(ele);
            });
        }
        Event.observe(window, "load", wordBreak);
    }
})();

