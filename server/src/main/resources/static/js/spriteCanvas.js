class spriteCanvas {

    constructor(elementId, height, width, pixelSize) {
        this.canvas = document.getElementById(elementId);
        this.canvas.height = height * pixelSize;
        this.canvas.width = width * pixelSize;

        this.heightInPixels = height;
        this.widthInPixels = width;
        this.pixelSize = pixelSize;

        this._shouldBeDashed = true;

        this.tilesRows = this.createTiles("#FFFFFF");

        this.drawCanvasWithTiles();

        this.selectedColor = "#ff0000";

        this.mode = "draw";

        this.isDrawing = false;

        // Add the event listeners for mousedown, mousemove, and mouseup
        this.canvas.addEventListener('mousedown', e => {
            let gridPositionOfClick = this.findXAndYGridPositionsOfClick(e.pageX , e.pageY);

            if (this.mode === "draw") {
                this.drawTileAtLocation(gridPositionOfClick);
                this.isDrawing = true;
            }
            else if (this.mode === "fill") {
                this.canvasFillFromPosition(gridPositionOfClick)
            }
            else if (this.mode === "colorPicker") {
                this.selectColorAt(gridPositionOfClick);
            }
        });

        this.canvas.addEventListener('mousemove', e => {
            let gridPositionOfClick = this.findXAndYGridPositionsOfClick(e.pageX , e.pageY);

            if (this.isDrawing === true) {
                this.drawTileAtLocation(gridPositionOfClick);
            }
        });

        this.canvas.addEventListener('mouseout', e => {
            this.isDrawing = false;
        });

        this.canvas.addEventListener('mouseup', e => {

            let gridPositionOfClick = this.findXAndYGridPositionsOfClick(e.pageX , e.pageY);


            if (this.isDrawing === true) {
                this.drawTileAtLocation(gridPositionOfClick);
                this.isDrawing = false;
            }
        });

    }

    shouldBeDashed() {
        return this.pixelSize > 20 && this._shouldBeDashed === true;
    }

    setShouldBeDashed(shouldBeDashed) {
        this._shouldBeDashed = shouldBeDashed;
    }

    setAttachedColorSelector(colorSelector) {
        this.colorSelector = colorSelector;
    }
    /** valid modes currently: draw, fill */
    setMode(mode) {
        this.mode = mode;
    }

    createTiles(defaultColor) {
        let tileRows = [];

        for (let widthCount = 0; widthCount < this.widthInPixels; widthCount++){
            let tileRow = [];
            for (let heightCount = 0; heightCount < this.heightInPixels; heightCount++){
                tileRow.push(new spriteCanvasTile(widthCount, heightCount, defaultColor))
            }

            tileRows.push(tileRow);
        }

        return tileRows;
    }

    drawCanvasWithTiles() {
        this.tilesRows.forEach((tileRow) => {
            tileRow.forEach( (tile) => {
                this.drawTile(tile);
            });
        });
    }

    drawTile(tile) {
        let ctx = this.canvas.getContext("2d");

        if (this.shouldBeDashed()) {
            ctx.setLineDash([6, 6]);
        } else {
            ctx.setLineDash([]);
            ctx.strokeStyle = tile.color;
        }
        ctx.beginPath();
        ctx.fillStyle = tile.color;
        ctx.rect(tile.xStart * this.pixelSize, tile.yStart * this.pixelSize, this.pixelSize, this.pixelSize);
        ctx.stroke();
        ctx.fill();
    }

    // negative to make canvas smaller positive makes it smaller
    resizeScaleOfPixels(sizeToAddToPixelSize) {
        this.pixelSize = this.pixelSize + sizeToAddToPixelSize;

        if(this.pixelSize <= 0) {
            this.pixelSize = 1; // minimum of 1 is a good idea
        }
        this.canvas.height = this.heightInPixels * this.pixelSize;
        this.canvas.width = this.widthInPixels * this.pixelSize;

        this.drawCanvasWithTiles();
    }

    drawTileAtLocation(gridLocation) {
        let tile = this.tilesRows[gridLocation.x][gridLocation.y];
        tile.color = this.selectedColor;
        this.drawTile(tile);
    }

    canvasFillFromPosition(gridLocation) {
        let startTile = this.tilesRows[gridLocation.x][gridLocation.y];

        let colorToFill = startTile.color;

        let tilesToColor = [startTile];
        let visitedTiles = new Map(); //this is tiles + value if they needed to be visited or not.

        // this will give the tiles directly surrounding this tile.
        let transformations = [[1, 0], [-1, 0], [0, 1], [0, -1]];
        while(tilesToColor.length > 0) {
            let tileToVisit = tilesToColor.pop();

            tileToVisit.color = this.selectedColor;
            this.drawTile(tileToVisit);

            transformations.forEach((transformation) => {
                let x = tileToVisit.xStart + transformation[0];
                let y = tileToVisit.yStart + transformation[1];

                if(this.isCoordinateInCanvas(x,y)) {
                    let potentialTileToVisit = this.tilesRows[x][y];

                    if (!visitedTiles.has(potentialTileToVisit)) {

                        if (potentialTileToVisit.color === colorToFill) {
                            visitedTiles.set(potentialTileToVisit, true);
                            tilesToColor.push(potentialTileToVisit);
                        }
                        else {
                            visitedTiles.set(potentialTileToVisit, false);
                        }
                    }
                }
            });
        }
    }

    selectColorAt(gridLocation) {
        console.log(this.tilesRows[gridLocation.x][gridLocation.y].color);
        this.colorSelector.value = this.tilesRows[gridLocation.x][gridLocation.y].color;
    }

    isCoordinateInCanvas(x, y) {
        if (x < 0 || x >= this.widthInPixels) {
            return false;
        } else if (y < 0 || y >= this.heightInPixels) {
            return false;
        }
        else {
            return true;
        }
    }

    findXAndYGridPositionsOfClick(xOfClick, yOfClick){
        let xcoord = xOfClick - this.canvas.offsetLeft;
        let ycoord = yOfClick - this.canvas.offsetTop;

        // this is to get the top left corner of the pixel that was clicked.
        xcoord -= (xcoord % this.pixelSize);
        ycoord -= (ycoord % this.pixelSize);

        let x = xcoord / this.pixelSize;
        let y = ycoord / this.pixelSize;
        return {x: x, y: y}
    }

    //i dont know javascript so this is probably a bad way of doing this
    //idea create temp canvas with the right width and height then create little pixel images and fill in.
    getPngDataUrlOfSprite() {
        let scaledCanvas = document.createElement("canvas");
        scaledCanvas.width = this.widthInPixels;
        scaledCanvas.height = this.heightInPixels;
        scaledCanvas.background = "#FFFFFF";
        let ctx = scaledCanvas.getContext("2d");

        let id = ctx.createImageData(1, 1);
        let d = id.data;

        for (let x = 0; x < this.widthInPixels; x++) {
            for (let y = 0; y < this.heightInPixels; y++) {
                let tile = this.tilesRows[x][y];

                let colorResult = hexToRgb(tile.color);

                d[0] = colorResult.r;
                d[1] = colorResult.g;
                d[2] = colorResult.b;
                d[3] = 255;
                ctx.putImageData(id, x, y);
            }
        }

        return scaledCanvas.toDataURL("image/png");
    }
}

class spriteCanvasTile {
    constructor(xStart, yStart, color){
        this.xStart = xStart;
        this.yStart = yStart;
        this.color = color;
    }
}

// I stole this from stack overflow. if it doesnt work fight me
function hexToRgb(hex) {
    // Expand shorthand form (e.g. "03F") to full form (e.g. "0033FF")
    let shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i;
    hex = hex.replace(shorthandRegex, function(m, r, g, b) {
        return r + r + g + g + b + b;
    });

    let result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16)
    } : null;
}