angular.module("crowd").filter("weblink", function(js) {
    return function(url) {
        return js.startsWith(url, "http") ? url : "http://" + url;
    }
})
