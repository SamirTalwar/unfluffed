(function() {
    var client = new Faye.Client('/bayeux');

    window.Unfluffed = {
        App: {
            publish: client.publish.bind(client),
            subscribe: client.subscribe.bind(client)
        },
        Process: function(setup) {
            setup(window.Unfluffed.App);
        }
    };
}());
