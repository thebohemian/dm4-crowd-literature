angular.module("crowd").constant("js", {

    startsWith: function(str, prefix) {
        return str.substr(0, prefix.length) == prefix;
    }
})
