import * as PIXI from './pixi.js';


let emojiSpriteSheetJson = GetEmojiSpriteSheetJson()

// Create the SpriteSheet from data and image
const spritesheet = new PIXI.Spritesheet(
    PIXI.BaseTexture.from(emojiSpriteSheetJson.meta.image),
    emojiSpriteSheetJson
);

// Generate all the Textures asynchronously
await spritesheet.parse();

const enemySpriteTextures = Object.entries(spritesheet.textures); 

let enemies = [];
let moveSpeed = 10
let invinceFrames = 60;


let app = new PIXI.Application({ width: 1511, height: 850});

const gamediv = document.getElementById('gameDiv'); 
gamediv.appendChild(app.view)


const startscene = CreateStartScene(app.screen.width, app.screen.height)
app.stage.addChild(startscene);

let gameScene = new PIXI.Container();
gameScene.visible = false
app.stage.addChild(gameScene);

let stian = PIXI.Sprite.from('/images/game/stian.png');
// center the sprite's anchor point
stian.anchor.set(0.5);
// move the sprite to the center of the screen
stian.x = (app.screen.width / 2) - (stian.width / 2);
stian.y = (app.screen.height / 2) - (stian.height / 2);
stian.vx = 0;
stian.vy = 0;
stian.scale.set(0.5, 0.5)

stian.hitable = false;
stian.hitableTimer = invinceFrames;
stian.health = 3;
gameScene.addChild(stian)


let candle = null;
let candleTimer = getRandomInt(25, 175)
let score = 0;
let maxscore = 38
let finalScore;
let finalMessage;


const gameOverScene = CreateGameoverScene(app.screen.width, app.screen.height)
app.stage.addChild(gameOverScene);
let gameOverTimer = 100;


const scoreStyle = new PIXI.TextStyle({
    fontFamily: "Futura",
    fontSize: 32,
    fill: "white"
  });
let scoreMessage = new PIXI.Text(GetScoreMessage(score), scoreStyle);
scoreMessage.x = 16;
scoreMessage.y = 16;


let hearts = []
let heartx= app.screen.width - 24
for(let i = 0; i < 3; i++) {
    let heart = PIXI.Sprite.from('/images/game/aliveheart.png');
    heart.scale.set(1.5)
    heart.anchor.set(0.5)
    heart.x = heartx
    heart.y = 16
    gameScene.addChild(heart)

    hearts.unshift(heart)

    heartx -= 36
}


gameScene.addChild(scoreMessage)

let deltaTilNewEnemy = 120
// Listen for animate update
app.ticker.add((delta) =>
{
    if (gameScene.visible) {
        if(stian.health > 0 && score < maxscore) {
            stian.x += stian.vx * delta
            stian.y += stian.vy * delta
        
            contain(stian, {x: 0, y: 0, width: app.screen.width, height: app.screen.height});
            
            
            // move enemies
            enemies.forEach(enemy => {
                if (!enemy.hitable) {
                    enemy.hitableTimer -= delta
                    if (enemy.hitableTimer < 0 ) {
                        enemy.hitable = true;
                    }
                }
                enemy.x += enemy.vx * delta
                enemy.y += enemy.vy * delta
        
                let collision = contain(enemy,  {x: 0, y: 0, width: app.screen.width, height: app.screen.height});
        
                if (collision == "top" || collision == "bottom") {
                    enemy.vy = enemy.vy * -1
                }
        
                if (collision == "left" || collision == "right") {
                    enemy.vx = enemy.vx * -1
                }
        
                enemy.rotation -= 0.02 * delta;
            })
        
            //check for damage
            if (stian.hitable) {
                enemies.forEach(enemy => {
    
                    if(enemy.hitable) {
                        let collided = hitTestRectangle(stian, enemy)
                        if (collided) {
                            console.log('collided with enemy at ' +  enemy.x + ',' + enemy.y)
                            
                            stian.health -= 1
            
                            let heart = hearts[stian.health]
    
                            heart.texture = PIXI.Texture.from('/images/game/deadheart.png');
                            
                            stian.hitable = false;
                            stian.hitableTimer = invinceFrames;
                            
                        }
                    }
                    
                   
                });
            }
            else {
                stian.hitableTimer -= delta
                if(stian.hitableTimer <= 0) {
                    console.log('hitable again')
                    stian.hitable = true;
                }
            }
    
            //spawn new enemies
            deltaTilNewEnemy -= delta
            if (deltaTilNewEnemy <= 0) {
                console.log(deltaTilNewEnemy)
                let newEnemy = createNewEnemy()
                newEnemy.hitableTimer = 75;
                newEnemy.hitable = false;
        
                console.log('new enemy at ' + newEnemy.x + ',' + newEnemy.y)
                gameScene.addChild(newEnemy)
                enemies.push(newEnemy)
        
                deltaTilNewEnemy = getRandomInt(250, 550)
            }
    
            if (candle == null) {
                candleTimer -= delta
                if (candleTimer <= 0) {
                    candle = createNewCandle()
                    gameScene.addChild(candle)
                }
            }
            else {
                let candlecollide = hitTestRectangle(candle, stian)
                if (candlecollide) {
                    score += 1
                    scoreMessage.text = GetScoreMessage(score)
                    candle.destroy()
                    candle = null;
    
                    candleTimer = getRandomInt(25, 150)
                }
            }
        }
        
        else if (gameOverTimer > 0){
            gameOverTimer -= delta
    
            if (gameOverTimer <= 0) {
                if(score < maxscore) {
                    finalMessage.text = "rip stan :("   
                }
                else {
                    finalMessage.text = "stan collected all his candles and escaped :')"
                }
                finalScore.text = "Final Score: " + score
                gameScene.visible = false;
                gameOverScene.visible = true;
            }
        }
    }
    
    
});


function createNewEnemy() {

    const spriteid = getRandomInt(0, enemySpriteTextures.length)

    const sprite = new PIXI.Sprite(spritesheet.textures[enemySpriteTextures[spriteid][0]]);
    sprite.anchor.set(0.5);
    sprite.x = getRandomInt(16 , (app.screen.width - 16))
    sprite.y = getRandomInt(16 , (app.screen.height - 16))
    sprite.scale.set(0.333, 0.333)
    
    sprite.vx = getRandomInt((-1 * moveSpeed) + 1 , moveSpeed - 1);
    sprite.vy = getRandomInt((-1 * moveSpeed) + 1, moveSpeed - 1);

    return sprite
}

function createNewCandle() {
    let sprite = PIXI.Sprite.from('/images/game/candle.png')
    sprite.anchor.set(0.5);

    sprite.x = getRandomInt(16 , (app.screen.width - 16))
    sprite.y = getRandomInt(16 , (app.screen.height - 16))

    return sprite;
}


function getRandomInt(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min) + min); // The maximum is exclusive and the minimum is inclusive
  }
  



function onKeyDown(key) {
    if (key.repeat) { return }
    if (key.keyCode == 37) {
        stian.vx  -= moveSpeed
    }
    if (key.keyCode == 38) {
        stian.vy -= moveSpeed
    }
    if (key.keyCode == 39) {
        stian.vx  += moveSpeed
    }
    if (key.keyCode == 40) {
        stian.vy += moveSpeed
    }
}

function onKeyUp(key) {
    if (key.repeat) { return }
    if (key.keyCode == 37) {
        stian.vx  += moveSpeed
    }
    if (key.keyCode == 38) {
        stian.vy += moveSpeed
    }
    if (key.keyCode == 39) {
        stian.vx  -= moveSpeed
    }
    if (key.keyCode == 40) {
        stian.vy -= moveSpeed
    }
}

document.addEventListener('keydown', onKeyDown);
document.addEventListener('keyup', onKeyUp);





function contain(sprite, container) {

    let collision = undefined;
    let spritehalfwidth = sprite.width / 2
    let spritehalfheight = sprite.height / 2
    //Left
    if (sprite.x - spritehalfwidth < container.x) {
      sprite.x = container.x + spritehalfwidth + 1;
      collision = "left";
    }
  
    //Top
    if (sprite.y - spritehalfheight < container.y) {
      sprite.y = container.y + spritehalfheight + 1;
      collision = "top";
    }
  
    //Right
    if (sprite.x + spritehalfwidth > container.width) {
      sprite.x = container.width - spritehalfwidth - 1;
      collision = "right";
    }
  
    //Bottom
    if (sprite.y + spritehalfheight> container.height) {
    console
      sprite.y = container.height - spritehalfheight - 1;
      collision = "bottom";
    }
  
    //Return the `collision` value
    return collision;
  }


function hitTestRectangle(r1, r2) {

    let hit, combinedHalfWidths, combinedHalfHeights, vx, vy;
  
    //hit will determine whether there's a collision
    hit = false;
  
    //Find the center points of each sprite sprites are anchored on mid
    r1.centerX = r1.x
    r1.centerY = r1.y
    r2.centerX = r2.x
    r2.centerY = r2.y
  
    //Find the half-widths and half-heights of each sprite
    r1.halfWidth = r1.width / 2;
    r1.halfHeight = r1.height / 2;
    r2.halfWidth = r2.width / 2;
    r2.halfHeight = r2.height / 2;
  
    //Calculate the distance vector between the sprites
    vx = r1.centerX - r2.centerX;
    vy = r1.centerY - r2.centerY;
  
    //Figure out the combined half-widths and half-heights
    combinedHalfWidths = r1.halfWidth + r2.halfWidth;
    combinedHalfHeights = r1.halfHeight + r2.halfHeight;
  
    //Check for a collision on the x axis
    if (Math.abs(vx) < combinedHalfWidths) {
  
      //A collision might be occurring. Check for a collision on the y axis
      if (Math.abs(vy) < combinedHalfHeights) {
  
        //There's definitely a collision happening
        hit = true;
      } else {
  
        //There's no collision on the y axis
        hit = false;
      }
    } else {
  
      //There's no collision on the x axis
      hit = false;
    }
  
    //`hit` will be either `true` or `false`
    return hit;
  };



function CreateStartScene(appwidth, appheight) {
    let scene = new PIXI.Container();
    
    scene.visible = true;

    //Create the text sprite and add it to the `gameOver` scene
    const style = new PIXI.TextStyle({
        fontFamily: "Futura",
        fontSize: 128,
        fill: "white"
    });
    let message = new PIXI.Text("Stians Torment", style);
    message.x = (appwidth / 2) - (message.width / 2);
    message.y =  94;
    scene.addChild(message);

    let logo = PIXI.Sprite.from('/images/game/stian.png');
    logo.anchor.set(0.5)
    logo.scale.set(1.5)
    logo.x = appwidth / 2;
    logo.y = 350;
    scene.addChild(logo)

    let rightTopCandleStart = appwidth / 2 + 100;

    while (rightTopCandleStart < appwidth) {
        let candle = PIXI.Sprite.from('/images/game/candle.png');
        candle.anchor.set(0.5)

        candle.x = rightTopCandleStart;
        candle.y = logo.y - 50;
        scene.addChild(candle)

        rightTopCandleStart += 100
    }

    let rightBotCandleStart = appwidth / 2 + 150;

    while (rightBotCandleStart < appwidth) {
        let candle = PIXI.Sprite.from('/images/game/candle.png');
        candle.anchor.set(0.5)
        candle.x = rightBotCandleStart;
        candle.y = logo.y + 50;
        scene.addChild(candle)

        rightBotCandleStart += 100
    }

    let leftTopCandleStart = appwidth / 2 - 100;

    while (leftTopCandleStart > 0) {
        let candle = PIXI.Sprite.from('/images/game/candle.png');
        candle.anchor.set(0.5)

        candle.x = leftTopCandleStart;
        candle.y = logo.y - 50;
        scene.addChild(candle)

        leftTopCandleStart -= 100
    }

    let leftBotCandleStart = appwidth / 2 - 150;

    while (leftBotCandleStart > 0) {
        let candle = PIXI.Sprite.from('/images/game/candle.png');
        candle.anchor.set(0.5)

        candle.x = leftBotCandleStart;
        candle.y = logo.y + 50;
        scene.addChild(candle)

        leftBotCandleStart -= 100
    }

    const style2 = new PIXI.TextStyle({
        fontFamily: "Futura",
        fontSize: 32,
        fill: "white"
      });
    let storyText = new PIXI.Text("Oh no its Stain's birthday. But his worst nightmare is his colleagues attention :( :'(", style2);
    storyText.anchor.set(0.5)
    storyText.x = (appwidth / 2);
    storyText.y = appheight / 2 + 64;
    scene.addChild(storyText)

    let storyText2 = new PIXI.Text("Can you guide Styan to collect his 38 birthday candles and escape before people notice his birthday hat", style2);
    storyText2.anchor.set(0.5)
    storyText2.x = (appwidth / 2);
    storyText2.y = storyText.y + 36;
    scene.addChild(storyText2)


    let startButton = new PIXI.Text("Click here to start.", style2)
    startButton.anchor.set(0.5)
    startButton.x = appwidth / 2
    startButton.y = storyText2.y + 150
    startButton.eventMode = 'static';
    startButton.cursor = 'pointer';
    startButton
        .on('pointerdown', start)
    scene.addChild(startButton)

    let startButton2 = new PIXI.Text("Click for Unlimted score mode.", style2)
    startButton2.anchor.set(0.5)
    startButton2.x = appwidth / 2
    startButton2.y = startButton.y + 100
    startButton2.eventMode = 'static';
    startButton2.cursor = 'pointer';
    startButton2
        .on('pointerdown', startunlim)
    scene.addChild(startButton2)

    return scene
}

function CreateGameoverScene(appwidth, appheight){
     //Create the `gameOver` scene
    let scene = new PIXI.Container();
    

    //Make the `gameOver` scene invisible when the game first starts
    scene.visible = false;

    //Create the text sprite and add it to the `gameOver` scene
    const style = new PIXI.TextStyle({
        fontFamily: "Futura",
        fontSize: 64,
        fill: "white"
    });
    finalMessage = new PIXI.Text("rip stan :(", style);
    finalMessage.x = (appwidth / 2)- 500;
    finalMessage.y = appheight / 2 - 32;
    scene.addChild(finalMessage);

    //Create the text sprite and add it to the `gameOver` scene
    const style2 = new PIXI.TextStyle({
        fontFamily: "Futura",
        fontSize: 32,
        fill: "white"
    });
    finalScore = new PIXI.Text("", style2);
    finalScore.x = (appwidth / 2);
    finalScore.y = appheight / 2 + 32;
    scene.addChild(finalScore);


    let resetbutton = new PIXI.Text("retry?", style2)
    resetbutton.x = appwidth / 2
    resetbutton.y = (appheight / 2) + 90 
    resetbutton.eventMode = 'static';
    resetbutton.cursor = 'pointer';
    resetbutton
        .on('pointerdown', reset)
    scene.addChild(resetbutton)

    let reds = maxscore - score;
    let scoreText = new PIXI.Text("🟩".repeat(score) + "🟥".repeat(reds), style);
    scoreText.x = (appwidth / 2) - 600;
    scoreText.y = (appheight / 2) - 50;
    scene.addChild(scoreText);    

    return scene
}


function start() {
    startscene.visible = false
    gameScene.visible = true
}

function startunlim() {
    maxscore = 100000000000000
    startscene.visible = false
    gameScene.visible = true
}

function reset() {
    maxscore = 38
    console.log('reseting')
    // move the sprite to the center of the screen
    stian.x = app.screen.width / 2;
    stian.y = app.screen.height / 2;
    stian.health = 3
    invinceFrames = 60;


    //reset enemies
    enemies.forEach(enemy => {enemy.destroy()})
    enemies = [];
    deltaTilNewEnemy = 120


    //reset scoring
    if (candle != null) {
        candle.destroy()
        candle = null;
    }
    candleTimer = getRandomInt(50, 200)
    score = 0;
    
    //reset ui
    scoreMessage.text = GetScoreMessage(score)

    hearts.forEach(heart => {
        heart.texture = PIXI.Texture.from('/images/game/aliveheart.png');
    })


    gameOverTimer = 100;
    startscene.visible = true;
    gameScene.visible = false;
    gameOverScene.visible = false

}


function GetEmojiSpriteSheetJson() {

    return {frames: {

        '2959616fee94ae00.png':
        {
            frame: {x:0,y:0,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'angeeree.jpg':
        {
            frame: {x:128,y:0,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'antivax.png':
        {
            frame: {x:0,y:128,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'backlog.png':
        {
            frame: {x:128,y:128,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'corn.png':
        {
            frame: {x:0,y:256,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'emblaoye.png':
        {
            frame: {x:128,y:256,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'emblatheeye.png':
        {
            frame: {x:0,y:384,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'emobrett.png':
        {
            frame: {x:128,y:384,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'equinor.png':
        {
            frame: {x:0,y:512,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'factory.png':
        {
            frame: {x:128,y:512,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'haaland.png':
        {
            frame: {x:0,y:640,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'kawaii.png':
        {
            frame: {x:128,y:640,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'larsteams.png':
        {
            frame: {x:0,y:768,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'moria.png':
        {
            frame: {x:128,y:768,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'mousebrett.png':
        {
            frame: {x:0,y:896,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'petterrock.png':
        {
            frame: {x:128,y:896,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'precious.png':
        {
            frame: {x:0,y:1024,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'productionize.png':
        {
            frame: {x:128,y:1024,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'stianwave.png':
        {
            frame: {x:0,y:1152,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'tihi.png':
        {
            frame: {x:128,y:1152,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'tonywave.png':
        {
            frame: {x:0,y:1280,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'torarnepog.png':
        {
            frame: {x:128,y:1280,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        },
        'voldemort.png':
        {
            frame: {x:0,y:1408,w:128,h:128},
            rotated: false,
            trimmed: false,
            spriteSourceSize: {x:0,y:0,w:128,h:128},
            sourceSize: {w:128,h:128}
        }},
        meta: {
            version: 1.1,
            image: '/images/game/emojiSprites.png',
            format: 'RGBA8888',
            size: {w:256,h:1536},
            scale: 1,
        }
        }
}

function GetScoreMessage(score){
    return "Score: " + score
}
