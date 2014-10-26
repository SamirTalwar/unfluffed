(function() {
    var client = new Faye.Client('/bayeux'),
        processes = [];

    window.Unfluffed = {
        App: {
            publish: client.publish.bind(client),
            subscribe: client.subscribe.bind(client),
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
