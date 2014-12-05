Unfluffed.Component(function(app) {
    function newGameButton() {
        return $('<button>').text('New Game').click(function() {
            app.client.id(function(id) {
                app.publish('/game/state', {
                    state: 'waiting-for-player',
                    hero: id
                });
            });
        })
    }

    function joinGameButton(player1Id) {
        return $('<button>').text('Join Game').click(function() {
            app.client.id(function(id) {
                app.publish('/game/state', {
                    state: 'started',
                    hero: player1Id,
                    villain: (player1Id == id) ? (id + '!') : id
                });
            });
        });
    }

    var actions = $('<p>');
    $(document.body).append(actions);

    actions.append(newGameButton());

    app.subscribe('/game/state', function(data) {
        switch (data.state) {
            case 'waiting-for-player':
                actions.empty().append(joinGameButton(data.hero));
                break;
            case 'running':
                actions.empty();
                break;
            case 'won':
            case 'draw':
                actions.empty().append(newGameButton());
        }
    });
});
