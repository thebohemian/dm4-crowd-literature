angular.module("crowd").filter("datetime", function(js) {

    var monthNames = [undefined, "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]

    return function(from, to) {
        if (!isDateSet(from.date)) {
            return ""
        }
        return dateRange(from.date, to.date) + timeRange(from.time, to.time);
    }

    // ---

    function date(date) {
        return monthNames[date.month] + " " + date.day + ", " + date.year;
    }

    function time(time) {
        return time.hour + ":" + (time.minute < 10 ? "0" : "") + time.minute;
    }

    function dateRange(date1, date2) {
        return date(date1) + (isDateSet(date2) && !equals(date1, date2) ? " - " + date(date2) : "");
    }

    function timeRange(time1, time2) {
        return isTimeSet(time1) ? "<br>" + time(time1) + (isTimeSet(time2) ? "-" + time(time2) : "") : "";
    }

    // ---

    function isDateSet(date) {
        return date.month && date.day && date.year;
    }

    function isTimeSet(time) {
        return time.hour && time.minute != undefined;
    }

    function equals(date1, date2) {
        return date1.month == date2.month && date1.day == date2.day && date1.year == date2.year;
    }
})
