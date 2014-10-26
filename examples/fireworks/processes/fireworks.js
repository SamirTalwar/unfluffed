Unfluffed.Process(function(app) {
    function imageElement(src) {
        var img = document.createElement('img');
        img.src = src;
        return img;
    }

    var fireworks = new Fireworks({
        bigGlow: imageElement(app.client.asset('Fireworks/images/big-glow.png')),
        smallGlow: imageElement(app.client.asset('Fireworks/images/small-glow.png'))
    });

    fireworks.initialize();

    function createFirework(event) {
        var x = event.clientX || event.changedTouches[0].clientX,
            y = event.clientY || event.changedTouches[0].clientY;
        app.publish('/firework', {
            target: {
                y: y
            },
            velocity: {
                x: (x - window.innerWidth / 2) / 100
            },
            color: Math.floor(Math.random() * 100) * 12
        });
    }

    document.addEventListener('mouseup', createFirework, true);
    document.addEventListener('touchend', createFirework, true);

    app.subscribe('/firework', function(particle) {
        fireworks.createParticle(
            particle.position, particle.target, particle.velocity, particle.color, particle.usePhysics);
    });
});
