define([], function() {
    return function(array) {
        var r = [];
        (function f(t, a, n) {
            if (n == 0) return r.push(t);
            for (var i = 0; i < a[n - 1].length; i++) {
                f(t.concat(a[n - 1][i]), a, n - 1);
            }
        })([], array, array.length);
        return r;
    }
})