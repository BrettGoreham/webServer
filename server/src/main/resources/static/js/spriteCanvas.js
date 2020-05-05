class spriteCanvas {

    constructor(elementId, height, width, pixelSize) {
        this.canvas = document.getElementById(elementId);
        this.canvas.height = height * pixelSize;
        this.canvas.width = width * pixelSize;

        this.heightInPixels = height;
        this.widthInPixels = width;
        this.pixelSize = pixelSize;

        this._shouldBeDashed = true;

        this.tilesRows = this.createTiles("#ffffff");

        this.drawCanvasWithTiles();

        this.selectedColor = '#ff0000';

        this.mode = "draw";

        this.isDrawing = false;

        this.startOfSegmentX = null; // these are used for drawing lines.
        this.startOfSegmentY = null;

        // Add the event listeners for mousedown, mousemove, mouseup, mouseout.
        this.canvas.addEventListener('mousedown', e => {
            let gridPositionOfClick = this.findXAndYGridPositionsOfClick(e.pageX , e.pageY);

            // sometimes i found the boundaries/border of canvas can be clicked
            //this would cause a false positive that can be ignored
            if (this.isCoordinateInCanvas(gridPositionOfClick.x, gridPositionOfClick.y)) {
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
                else if (this.mode === "line") {
                    this.startLineAt(gridPositionOfClick);
                }
            }
        });

        this.canvas.addEventListener('mousemove', e => {
            let gridPositionOfClick = this.findXAndYGridPositionsOfClick(e.pageX , e.pageY);

            if (this.isDrawing === true) {
                if ((this.lastTrackedPosition.x !== gridPositionOfClick.x ||
                        this.lastTrackedPosition.y !== gridPositionOfClick.y)
                        && this.isCoordinateInCanvas(gridPositionOfClick.x, gridPositionOfClick.y)) {

                    if (this.mode === "draw") {
                        this.drawTileAtLocation(gridPositionOfClick);
                    } else if (this.mode === "line") {
                        this.lastTrackedPosition = gridPositionOfClick;
                        this.drawLineToGridPosition(gridPositionOfClick);
                    }
                }
            }
        });

        this.canvas.addEventListener('mouseup', e => {
            let gridPositionOfClick = this.findXAndYGridPositionsOfClick(e.pageX , e.pageY);

            if (this.isDrawing === true) {
                if ((this.lastTrackedPosition.x !== gridPositionOfClick.x
                        || this.lastTrackedPosition.y !== gridPositionOfClick.y)
                    && this.isCoordinateInCanvas(gridPositionOfClick.x, gridPositionOfClick.y)) {

                    if(this.mode === "draw") {
                        this.drawTileAtLocation(gridPositionOfClick);
                    }
                    else if (this.mode === "line") {
                        this.drawLineToGridPosition(gridPositionOfClick);
                    }

                    this.lastTrackedPosition = gridPositionOfClick;
                }

                this.endDrawing();
            }
        });

        this.canvas.addEventListener('mouseout', e => {
            if (this.isDrawing === true) {
                this.endDrawing();
            }
        })
    }

    endDrawing() {
        this.isDrawing = false;
        this.startOfSegmentX = null;
        this.startOfSegmentY = null;
        this.lastTrackedPosition = null;
        this.tempLineWithOriginalColor = null;
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
    /** valid modes currently: draw, fill, colorPicker, line */
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

        if(this.shouldBeDashed()) {
            ctx.stroke();
        }
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
        if(this.lastTrackedPosition == null) {
            let tile = this.tilesRows[gridLocation.x][gridLocation.y];
            tile.color = this.selectedColor;
            this.drawTile(tile);
        } else {

            let tilesToPlot = plotLine(this.lastTrackedPosition.x, this.lastTrackedPosition.y, gridLocation.x, gridLocation.y);

            tilesToPlot.forEach(tileLocation => {
                let tileToColor = this.tilesRows[tileLocation[0]][tileLocation[1]];
                tileToColor.color = this.selectedColor;
                this.drawTile(tileToColor);
            });
        }

        this.lastTrackedPosition = gridLocation;
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
        this.colorSelector.value = this.tilesRows[gridLocation.x][gridLocation.y].color;
        this.selectedColor = this.colorSelector.value;
    }


    startLineAt(gridPositionOfClick) {
        this.startOfSegmentX = gridPositionOfClick.x;
        this.startOfSegmentY = gridPositionOfClick.y;
        this.isDrawing = true;

        this.lastTrackedPosition = gridPositionOfClick;
        this.drawLineToGridPosition(gridPositionOfClick)
    }

    drawLineToGridPosition(gridPositionOfClick) {

        if (this.tempLineWithOriginalColor != null) { // reset last line.
            for (const [key, value] of this.tempLineWithOriginalColor.entries()) {
                key.color = value;

                this.drawTile(key);
            }

            this.tempLineWithOriginalColor = null;
        }

        let tilesEffected = plotLine(this.startOfSegmentX, this.startOfSegmentY, gridPositionOfClick.x, gridPositionOfClick.y);

        this.tempLineWithOriginalColor = new Map();

        tilesEffected.forEach(tileCoordinates => {
            let tile = this.tilesRows[tileCoordinates[0]][tileCoordinates[1]];

            //if its already the right color fuck it.
            if (tile.color !== this.selectedColor) {
                this.tempLineWithOriginalColor.set(tile, tile.color);

                tile.color = this.selectedColor;
                this.drawTile(tile);
            }
        })
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

function plotLineLow(x0, y0, x1, y1) {
    let dx = x1 - x0;
    let dy = y1 - y0;
    let yIncrement = 1;
    if (dy < 0) {
        yIncrement = -1;
        dy = -1 * dy;
    }

    let D = (2 * dy) - dx;
    let y = y0;

    let output = [];
    for (let x = x0; x <= x1; x++) {
        output.push([x,y]);
        if (D > 0) {
            y = y + yIncrement;
            D = D - (2 * dx);
        }
        D = D + (2 * dy);
    }
    return output;
}

//listen im not going to pretend that i know whats going on here.
// this is from https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm#All_cases
// idk i just wanted to draw a line man
function plotLineHigh(x0, y0, x1, y1) {
    let dx = x1 - x0;
    let dy = y1 - y0;
    let xIncrement = 1;
    if (dx < 0) {
        xIncrement = -1;
        dx = -1 * dx;
    }

    let D = 2 * dx - dy;
    let x = x0;

    let output = [];

    for (let y = y0; y <= y1; y++) {
        output.push([x,y]);
        if (D > 0) {
            x = x + xIncrement;
            D = D - (2 * dy);
        }
        D = D + (2 * dx);
    }

    return output;
}

function plotLine(x0, y0, x1, y1) {
    if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
        if (x0 > x1) {
            return plotLineLow(x1, y1, x0, y0);
        }
        else {
            return plotLineLow(x0, y0, x1, y1);
        }
    }
    else {
        if (y0 > y1) {
            return plotLineHigh(x1, y1, x0, y0);
        }
        else {
            return plotLineHigh(x0, y0, x1, y1);
        }
    }
}