(function() {
    var client = new Faye.Client('/bayeux');
    var clientId;
    var components = [];

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
                id: function(onIdentification) {
                    if (clientId) {
                        onIdentification(clientId);
                    } else {
                        var subscription = client.subscribe('/framework/identification', function(data) {
                            clientId = data.id;
                            subscription.cancel();
                            onIdentification(clientId);
                        });
                        client.publish('/framework/identification', {});
                    }
                },
                asset: function(path) {
                    return "/application/assets/" + path;
                }
            }
        },
        Component: function(component) {
            components.push(component);
        }
    };

    window.addEventListener('load', function() {
        components.forEach(function(component) {
            component(window.Unfluffed.App);
        });
    });
}());
