Unfluffed.Component(function(app) {
    function newGameButton() {
        return $('<button>').text('New Game').click(function() {
            var gameId = prompt('Game Name: ');
            app.client.id(function(playerId) {
                var gameId = app.uuid();
                app.publish('/game:' + gameId + '/state', {
                    state: 'waiting-for-player',
                    hero: playerId
                });
            });
        })
    }

    function joinGameButton(otherPlayerId) {
        return $('<button>').text('Join Game').click(function() {
            app.client.id(function(playerId) {
                app.publish('/game:' + gameId + '/state', {
                    state: 'started',
                    hero: otherPlayerId,
                    villain: (playerId == otherPlayerId) ? (playerId + '!') : playerId
                });
            });
        });
    }

    var actions = $('<p>');
    var games = $('<div>').addClass('games')
        .append($('<h2>').text('Games'))
        .append(actions);
    $(document.body).append(games);

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
