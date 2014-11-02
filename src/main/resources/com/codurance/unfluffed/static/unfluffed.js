(function() {
    var client = new Faye.Client('/bayeux'),
        processes = [];

    function debugFunction(name, f) {
        return function() {
            console.debug.apply(console, [name].concat(Array.prototype.slice.call(arguments)));
            return f.apply(this, arguments);
        };
    }

    window.Unfluffed = {
        App: {
            publish: debugFunction('publish', client.publish.bind(client)),
            subscribe: debugFunction('subscribe', client.subscribe.bind(client)),
            client: {
                asset: function(path) {
                    return "/application/assets/" + path
                }
            }
        },
        Process: function(process) {
            processes.push(process);
        }
    };

    window.addEventListener('load', function() {
        processes.forEach(function(process) {
            process(window.Unfluffed.App);
        });
    });
}());
