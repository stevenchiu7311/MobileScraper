var jq = document.createElement('script');
jq.innerHTML = window.android.getFileContents('jquery-latest.min.js');
document.getElementsByTagName('head')[0].appendChild(jq);
var css_path = document.createElement('script');
css_path.innerHTML = window.android.getFileContents('csspath.js');
document.getElementsByTagName('head')[0].appendChild(css_path);
var css = document.createElement('script');
css.innerHTML = window.android.getFileContents('css.js');
document.getElementsByTagName('head')[0].appendChild(css);
var style = document.createElement('style');
style.type = 'text/css';
style.innerHTML = encoded;
document.getElementsByTagName('head')[0].appendChild(style);

$(document).ready(function () {
    document.documentElement.addEventListener("click", function (e) {
        const path = cssPath(e.target);
        window.android.onElementClick(path);
    });
});

function toggleSelection(status, path) {
    var className = $(path).attr('class');
    if (className !== undefined && className.indexOf('watchdogSelected') !== -1) {
        $(path).removeClass( "watchdogSelected" );
        return false;
    } else {
        $(path).addClass( "watchdogSelected" );
        return true;
    }
}

function highlightTarget(path) {
    $(path).addClass( "watchdogSelected" );
}

function unhighlightTarget(path) {
    $(path).removeClass( "watchdogSelected");
}

function getText(path) {
    return $(path).contents().filter(function() {
      return this.nodeType == Node.TEXT_NODE;
    }).text();
}