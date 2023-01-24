class spriteCanvas {

    constructor(elementId, height, width, pixelSize, backgroundColor) {
        this.canvas = document.getElementById(elementId);
        this.canvas.height = height * pixelSize;
        this.canvas.width = width * pixelSize;
        this.canvas.style.backgroundColor = backgroundColor;

        this.heightInPixels = height;
        this.widthInPixels = width;
        this.pixelSize = pixelSize;
        this.backgroundColor = backgroundColor;

        this.transparency = 255;

        this.tilesRows = this.createTiles();

        this.drawCanvasWithTiles();

        this.undoStack = []; //used to undo
        this.drawingChanges = new Map(); //Map is in the form key = tile value = [color, transparency] where both are previous

        this.selectedColor = '#ff0000';

        this.mode = "draw";

        this.isDrawing = false;

        this.copiedSection = null;
        this.startOfSegmentX = null; // these are used for drawing lines.
        this.startOfSegmentY = null;
        this.dispatchUndoAvailabilityEvent(false);

        // Add the event listeners for mousedown, mousemove, mouseup, mouseout.
        this.canvas.addEventListener('mousedown', e => {
            let gridPositionOfClick = this.findXAndYGridPositionsOfClick(e.pageX , e.pageY);

            // sometimes i found the boundaries/border of canvas can be clicked
            //this would cause a false positive that can be ignored
            if (this.isCoordinateInCanvas(gridPositionOfClick.x, gridPositionOfClick.y)) {
                if (this.mode === "draw") {
                    this.drawTileToLocation(gridPositionOfClick);
                    this.isDrawing = true;
                }
                else if (this.mode === "fill") {
                    this.canvasFillFromPosition(gridPositionOfClick)
                    this.endDrawing();
                }
                else if (this.mode === "colorPicker") {
                    this.selectColorAt(gridPositionOfClick);
                }
                else if (this.mode === "line") {
                    this.startLineAt(gridPositionOfClick);
                }
                else if (this.mode === "selectArea") {
                    if (this.selectedArea != null) {
                        this.unDrawAreaSelected();
                        this.SelectedAreaButtons.forEach((button) =>{
                            document.body.removeChild(button);
                        });
                    }

                    this.selectedArea =
                        [gridPositionOfClick,
                            gridPositionOfClick];
                    this.startSelectAt(gridPositionOfClick);
                    this.lastTrackedPosition = gridPositionOfClick;
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
                        this.drawTileToLocation(gridPositionOfClick);
                    }
                    else if (this.mode === "line") {
                        this.lastTrackedPosition = gridPositionOfClick;
                        this.drawLineToGridPosition(gridPositionOfClick);
                    }
                    else if (this.mode === "selectArea") {
                        if (this.selectedArea != null) {
                            this.unDrawAreaSelected();
                        }
                        this.selectedArea =
                            [{x: this.startOfSegmentX, y: this.startOfSegmentY},
                                gridPositionOfClick];
                        this.drawAroundAreaSelected();

                        this.lastTrackedPosition = gridPositionOfClick;
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
                        this.drawTileToLocation(gridPositionOfClick);
                    }
                    else if (this.mode === "line") {
                        this.drawLineToGridPosition(gridPositionOfClick);
                    }
                    else if (this.mode === "selectArea") {
                        this.drawAroundAreaSelected(gridPositionOfClick);

                        this.selectedArea =
                            [{x: this.startOfSegmentX, y: this.startOfSegmentY},
                              gridPositionOfClick];

                    }
                }

                if (this.mode === "selectArea") {
                    this.createSelectedAreaButtonsAtLocation();
                }

                this.endDrawing();
            }
        });

        this.canvas.addEventListener('mouseout', e => {
            if (this.isDrawing === true) {
                if (this.mode === "selectArea") {
                    this.createSelectedAreaButtonsAtLocation();
                }
                this.endDrawing();
            }
        });
    }


    resizeCanvasToSelectedArea() {
        let selectedAreaValues = this.getMinAndMaxXAndYFromSelectedArea();

        this.selectedArea = null;
            
        this.SelectedAreaButtons.forEach((button) =>{
            document.body.removeChild(button);
        });

        this.resizeCanvasToNewSize(
            selectedAreaValues.minX,
            selectedAreaValues.minY,
            selectedAreaValues.maxX - selectedAreaValues.minX + 1,
            selectedAreaValues.maxY - selectedAreaValues.minY + 1); //plus 1 to make inclusive
    }

    resizeCanvasToNewSize(xStart, yStart, newWidth, newHeight) {
        this.addCurrentStateToChangeHistory();

        let oldWidth = this.widthInPixels;
        let oldHeight = this.heightInPixels;

        this.widthInPixels = newWidth;
        this.heightInPixels = newHeight;

        this.dispatchCanvasResizeEvent();

        this.canvas.width = newWidth * this.pixelSize;
        this.canvas.height = newHeight * this.pixelSize;

        let newTiles = this.createTiles();
        //important to create new tiles so we can undo action easier

        let xTilesToCopy = Math.min(oldWidth - xStart, this.widthInPixels);
        let yTilesToCopy = Math.min(oldHeight - yStart, this.heightInPixels);

        for (let y = 0; y < yTilesToCopy; y++) {
            for (let x = 0; x < xTilesToCopy; x++) {
                newTiles[y][x].color = this.tilesRows[yStart + y][xStart + x].color;
                newTiles[y][x].transparency = this.tilesRows[yStart + y][xStart + x].transparency;
            }
        }

        this.tilesRows = newTiles;

        this.drawCanvasWithTiles();
    }

    endDrawing() {
        this.addDrawingChangesToUndoMap();

        this.isDrawing = false;
        this.startOfSegmentX = null;
        this.startOfSegmentY = null;
        this.lastTrackedPosition = null;
    }

    /** valid modes currently: draw, fill, colorPicker, line */
    setMode(mode) {
        this.mode = mode;
        if (this.selectedArea != null) {
            this.unDrawAreaSelected();
            this.SelectedAreaButtons.forEach((button) =>{
                document.body.removeChild(button);
            });
            this.selectedArea = null;
        }
    }

    setTransparency(transparency) {
        this.transparency = transparency;
    }

    createTiles() {
        let tileRows = [];

        for (let heightCount = 0; heightCount < this.heightInPixels; heightCount++){
            let tileRow = [];
            for (let widthCount = 0; widthCount < this.widthInPixels; widthCount++){
                tileRow.push(new spriteCanvasTile(widthCount, heightCount, this.backgroundColor, 255))
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

        if(this.selectedArea != null) {
            this.SelectedAreaButtons.forEach((button) =>{
                document.body.removeChild(button);
            });
            this.drawAroundAreaSelected();
            this.createSelectedAreaButtonsAtLocation();
        }
    }

    drawListOfTiles(tilesToDraw) {
        tilesToDraw.forEach((tileToDraw) => {
            this.drawTile(tileToDraw);
        })
    }

    copySelectedSectionOfTiles() {
        let selectedAreaValues = this.getMinAndMaxXAndYFromSelectedArea();

        this.copiedSection = [];
        for(var y = selectedAreaValues.minY; y<= selectedAreaValues.maxY; y++) {
            let row = [];
            for(var x = selectedAreaValues.minX; x<= selectedAreaValues.maxX; x++){
                let tileToCopy = this.tilesRows[y][x];
                row.push(new spriteCanvasTile(x - selectedAreaValues.minX, y - selectedAreaValues.minY, tileToCopy.color, tileToCopy.transparency))        
            }

            this.copiedSection.push(row);
        }
    }

    pasteCopiedSectionToStartOfSelectedSection() {
        let selectedAreaValues = this.getMinAndMaxXAndYFromSelectedArea();
        let changesToDraw = [];

        for(var y = 0; y < this.copiedSection.length; y++) {
            for(var x = 0; x < this.copiedSection[y].length; x++) {
                if(this.isCoordinateInCanvas(selectedAreaValues.minX + x, selectedAreaValues.minY + y)){
                    
                    let tileToChange = this.tilesRows[selectedAreaValues.minY + y][selectedAreaValues.minX + x];
                    let change = this.copiedSection[y][x];

                    if (tileToChange.color != change.color || tileToChange.transparency != change.transparency) {
                        this.addTileToDrawingChanges(tileToChange);
                        tileToChange.color = change.color;
                        tileToChange.transparency = change.transparency;

                        changesToDraw.push(tileToChange);
                    }
                }
            }
        }

        this.addDrawingChangesToUndoMap("paste");
        this.drawListOfTiles(changesToDraw);
    }

    drawTile(tile) {

        let startX = tile.xStart * this.pixelSize;
        let startY = tile.yStart * this.pixelSize;
        let width = Math.max(this.pixelSize - 1, 1); // unsure if this is the best but when pixel size was one
        let height = Math.max(this.pixelSize - 1, 1); // it would make the entire drawing disappear.

        let ctx = this.canvas.getContext("2d");

        ctx.beginPath();
        //this clears the square so it can be drawn again isnt really required unless transparency is messed wth
        ctx.clearRect(startX, startY, width, height);

        ctx.rect(startX, startY, width, height);

        if (this.pixelSize < 10) {
            ctx.strokeStyle = tile.color;
        } else {
            ctx.strokeStyle = "#ffffff"; // this will give a nice grid but if pixels are small it dominates
        }
        ctx.fillStyle = tile.color;
        ctx.globalAlpha = this.getGlobalAlphaFromTransparency(tile.transparency);

        ctx.stroke();
        ctx.fill();
    }

    //GlobalAlpha is a number between 0 and 1. 1 being not transparent 0 being transparent
    getGlobalAlphaFromTransparency(transparency) {
        return transparency / 255;
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

    drawTileToLocation(gridLocation) {
        let changesToDraw = [];

        if (this.lastTrackedPosition == null) {
            let tile = this.tilesRows[gridLocation.y][gridLocation.x];

            this.checkIfTileNeedsToBeChangedAndAddToChangeList(tile, changesToDraw);
        } else {

            let tilesToPlot = plotLine(this.lastTrackedPosition.x, this.lastTrackedPosition.y, gridLocation.x, gridLocation.y);

            tilesToPlot.forEach(tileLocation => {
                let tileToColor = this.tilesRows[tileLocation[1]][tileLocation[0]];

                this.checkIfTileNeedsToBeChangedAndAddToChangeList(tileToColor, changesToDraw);
            });
        }

        this.drawListOfTiles(changesToDraw);
        this.lastTrackedPosition = gridLocation;
    }

    checkIfTileNeedsToBeChangedAndAddToChangeList(tile, changesToDraw) {
        if (tile.color !== this.selectedColor || tile.transparency !== this.transparency) {
            this.addTileToDrawingChanges(tile);
            tile.color = this.selectedColor;
            tile.transparency = this.transparency;

            changesToDraw.push(tile);
        }
    }

    canvasFillFromPosition(gridLocation) {
        let startTile = this.tilesRows[gridLocation.y][gridLocation.x];

        if (startTile.color !== this.selectedColor || startTile.transparency !== this.transparency) {
            let colorToFill = startTile.color;
            let transparencyToFill = startTile.transparency;

            let tilesToColor = [startTile];
            let visitedTiles = new Map(); //this is tiles + value if they needed to be coloured or not.

            // this will give the tiles directly surrounding this tile.
            let transformations = [[1, 0], [-1, 0], [0, 1], [0, -1]];
            while(tilesToColor.length > 0) {
                let tileToVisit = tilesToColor.pop();

                this.addTileToDrawingChanges(tileToVisit);

                tileToVisit.color = this.selectedColor;
                tileToVisit.transparency = this.transparency;

                transformations.forEach((transformation) => {
                    let x = tileToVisit.xStart + transformation[0];
                    let y = tileToVisit.yStart + transformation[1];

                    if (this.isCoordinateInCanvas(x,y)) {
                        let potentialTileToVisit = this.tilesRows[y][x];

                        if (!visitedTiles.has(potentialTileToVisit)) {

                            if (potentialTileToVisit.color === colorToFill
                                && potentialTileToVisit.transparency === transparencyToFill) {
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

            // for this action we can just draw directly from the changes as its a one click and done
            this.drawListOfTiles(
                Array.from(this.drawingChanges.keys())
            );
        }
    }

    selectColorAt(gridLocation) {
        let tile = this.tilesRows[gridLocation.y][gridLocation.x];

        this.updateColorAndTransparency(tile.color, tile.transparency);
    }

    updateColorAndTransparency(color, transparency) {
        if (color !== this.selectedColor || transparency !== this.transparency) {
            this.drawingChanges.set("color", this.selectedColor);
            this.drawingChanges.set("transparency", this.transparency)

            this.addDrawingChangesToUndoMap("colorPicker");

            this.changeColorAndTransparencyAndDispatchEvent(color, transparency);
        }
    }

    changeColorAndTransparencyAndDispatchEvent(color, transparency){
        this.selectedColor = color
        this.transparency = transparency;

        this.canvas.dispatchEvent(new CustomEvent('colorChange'));
    }


    startLineAt(gridPositionOfClick) {
        this.startOfSegmentX = gridPositionOfClick.x;
        this.startOfSegmentY = gridPositionOfClick.y;
        this.isDrawing = true;

        this.lastTrackedPosition = gridPositionOfClick;
        this.drawLineToGridPosition(gridPositionOfClick)
    }

    drawLineToGridPosition(gridPositionOfClick) {
        this.resetTilesBackToPreviousColors(this.drawingChanges);
        this.drawListOfTiles(Array.from(this.drawingChanges.keys()));

        this.drawingChanges = new Map();

        let tilesEffected = plotLine(this.startOfSegmentX, this.startOfSegmentY, gridPositionOfClick.x, gridPositionOfClick.y);

        tilesEffected.forEach(tileCoordinates => {
            let tile = this.tilesRows[tileCoordinates[1]][tileCoordinates[0]];

            //if its already the right color no need to redraw
            if (tile.color !==this.selectedColor || tile.transparency !== this.transparency) {
                this.addTileToDrawingChanges(tile)

                tile.color = this.selectedColor;
                tile.transparency = this.transparency;
            }
        })

        this.drawListOfTiles(Array.from(this.drawingChanges.keys()));
    }


    startSelectAt(gridPositionOfClick) {
        this.startOfSegmentX = gridPositionOfClick.x;
        this.startOfSegmentY = gridPositionOfClick.y;
        this.isDrawing = true;

        this.drawAroundAreaSelected();
    }

    drawAroundAreaSelected() {
        let selectedAreaValues = this.getMinAndMaxXAndYFromSelectedArea();

        let startX = selectedAreaValues.minX * this.pixelSize;
        let startY = selectedAreaValues.minY * this.pixelSize;
        let endX = selectedAreaValues.maxX * this.pixelSize + (this.pixelSize - 1);
        let endY = selectedAreaValues.maxY * this.pixelSize + (this.pixelSize - 1); // extra part to get end of pixel instead of start.

        let ctx = this.canvas.getContext("2d");
        ctx.strokeStyle = '#f5ea00';
        ctx.beginPath();
        ctx.rect(startX, startY, endX - startX, endY - startY);
        ctx.stroke();
    }

    //find which corner is the top left one. which is the lowest versions of each and then bottom right is highest
    // these may be top left and bottom right. or top right and bottom left. just find the top left and bottom right in case.

    unDrawAreaSelected() {
        let selectedAreaValues = this.getMinAndMaxXAndYFromSelectedArea();
        let toRedraw = []

        let topLeftX = selectedAreaValues.minX;
        let topLeftY = selectedAreaValues.minY;
        let bottomRightX = selectedAreaValues.maxX;
        let bottomRightY = selectedAreaValues.maxY;

        for (let x = topLeftX; x <= bottomRightX; x++) {
            toRedraw.push(this.tilesRows[topLeftY][x]);
            toRedraw.push(this.tilesRows[bottomRightY][x]);
        }
        for(let y = topLeftY; y <= bottomRightY; y++) {
            toRedraw.push(this.tilesRows[y][topLeftX]);
            toRedraw.push(this.tilesRows[y][bottomRightX]);
        }

        for (let x = topLeftX; x <= bottomRightX; x++) {
            if (topLeftY + 1 < this.heightInPixels) {
                toRedraw.push(this.tilesRows[topLeftY + 1][x]);
            }
            if (bottomRightY + 1 < this.heightInPixels) {
                toRedraw.push(this.tilesRows[bottomRightY + 1][x]);
            }
        }
        for(let y = topLeftY; y <= bottomRightY; y++) {
            if (topLeftX + 1 < this.widthInPixels) {
                toRedraw.push(this.tilesRows[y][topLeftX + 1]);
            }
            if (bottomRightX + 1 < this.widthInPixels) {
                toRedraw.push(this.tilesRows[y][bottomRightX + 1]);
            }
        }

        for (let x = topLeftX; x <= bottomRightX; x++) {
            if (topLeftY - 1 > 0) {
                toRedraw.push(this.tilesRows[topLeftY - 1][x]);
            }
            if (bottomRightY - 1 > 0) {
                toRedraw.push(this.tilesRows[bottomRightY - 1][x]);
            }
        }
        for(let y = topLeftY; y <= bottomRightY; y++) {
            if (topLeftX - 1 > 0) {
                toRedraw.push(this.tilesRows[y][topLeftX - 1]);
            }
            if (bottomRightX - 1 > 0) {
                toRedraw.push(this.tilesRows[y][bottomRightX - 1]);
            }
        }

        this.drawListOfTiles(toRedraw);
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

    getMinAndMaxXAndYFromSelectedArea() {
        let minX = Math.min(this.selectedArea[0].x, this.selectedArea[1].x);
        let minY = Math.min(this.selectedArea[0].y, this.selectedArea[1].y);
        let maxX = Math.max(this.selectedArea[0].x, this.selectedArea[1].x);
        let maxY = Math.max(this.selectedArea[0].y, this.selectedArea[1].y);

        return {minX: minX, minY: minY, maxX: maxX, maxY: maxY};
    }

    //i dont know javascript so this is probably a bad way of doing this
    //idea create temp canvas with the right width and height then create little pixel images and fill in.
    getPngDataUrlOfSprite() {
        let scaledCanvas = document.createElement("canvas");
        scaledCanvas.width = this.widthInPixels;
        scaledCanvas.height = this.heightInPixels;
        scaledCanvas.background = "#ffffff";
        let ctx = scaledCanvas.getContext("2d");

        let id = ctx.createImageData(1, 1);
        let d = id.data;

        for (let y = 0; y < this.heightInPixels; y++) {
            for (let x = 0; x < this.widthInPixels; x++) {

                let tile = this.tilesRows[y][x];

                let colorResult = hexToRgb(tile.color);

                d[0] = colorResult.r;
                d[1] = colorResult.g;
                d[2] = colorResult.b;
                d[3] = tile.transparency;
                ctx.putImageData(id, x, y);
            }
        }

        return scaledCanvas.toDataURL("image/png");
    }


    importImageFromPngIntoTileCanvas(image) {
        let img = document.createElement("img");
        //document.body.appendChild(img);
        img.src = image;

        img.onload = () => this.getImageData(img);

    }

    getImageData(image) {
        this.addCurrentStateToChangeHistory();

        let importImageCanvas = document.createElement("canvas");

        importImageCanvas.width = image.width; //discovered this was important
        importImageCanvas.height = image.height;

        let importImageContext = importImageCanvas.getContext("2d");
        importImageContext.drawImage(image,0,0);

        let imageData = importImageContext.getImageData(0,0, image.width, image.height);

        this.widthInPixels = imageData.width;
        this.heightInPixels = imageData.height;
        this.canvas.width = this.widthInPixels * this.pixelSize;
        this.canvas.height = this.heightInPixels * this.pixelSize;

        let imageDataCursor = 0; // used to keep track of where we are in the image data datafield
        let tileRows = [];

        for (let heightCount = 0; heightCount < this.heightInPixels; heightCount++){

            let tileRow = [];
            for (let widthCount = 0; widthCount < this.widthInPixels; widthCount++){
                tileRow.push(
                    new spriteCanvasTile(
                        widthCount,
                        heightCount,
                        rgbToHex(imageData.data[imageDataCursor++], imageData.data[imageDataCursor++], imageData.data[imageDataCursor++]),
                        imageData.data[imageDataCursor++]
                    )
                )
            }

            tileRows.push(tileRow);
        }

        this.dispatchCanvasResizeEvent()
        this.tilesRows = tileRows;

        this.drawCanvasWithTiles();
    }

    dispatchCanvasResizeEvent() {
        this.canvas.dispatchEvent(new CustomEvent('canvasDimensionChange'));
    }

    dispatchUndoAvailabilityEvent(available) {
        if (available) {
            this.canvas.dispatchEvent(new CustomEvent('undoAvailability', {detail: true }));
        } else {
            this.canvas.dispatchEvent(new CustomEvent('undoAvailability', {detail: false }));
        }
    }

    createSelectedAreaButtonsAtLocation() {

        let minMax = this.getMinAndMaxXAndYFromSelectedArea();
        let xcoord = ((minMax.minX) * this.pixelSize) + this.canvas.offsetLeft;
        let ycoord = ((minMax.minY) * this.pixelSize) + this.canvas.offsetTop - 40;

        this.SelectedAreaButtons = [];
        let spriteCanvas = this;

        let resizeButton = document.createElement("button");
        resizeButton.classList.add("clickable")
        resizeButton.innerHTML = "Resize Sprite";
        resizeButton.style.position = 'absolute';
        resizeButton.style.top = ycoord + "px";
        resizeButton.style.left = xcoord + "px";

        resizeButton.onclick = function() {
            functionToResize(spriteCanvas);
        };
        document.body.appendChild(resizeButton);

        let copyButton = document.createElement("button");
        copyButton.classList.add("clickable")
        copyButton.innerHTML = "Copy";
        copyButton.style.position = 'absolute';
        copyButton.style.top =  ycoord + "px";
        let xOffset = parseInt(resizeButton.offsetWidth) + 10 + parseInt(xcoord);
        copyButton.style.left =  xOffset + "px";

        copyButton.onclick = function() {
            functionToCopy(spriteCanvas);
        };
        document.body.appendChild(copyButton);
        
        if (this.copiedSection != null) {

            let pasteButton = document.createElement("button");
            pasteButton.classList.add("clickable")
            pasteButton.innerHTML = "Paste";
            pasteButton.style.position = 'absolute';
            pasteButton.style.top = ycoord + "px";
            xOffset = parseInt(resizeButton.offsetWidth) + parseInt(copyButton.offsetWidth) + 20 + parseInt(xcoord);
            pasteButton.style.left = xOffset + "px";

            pasteButton.onclick = function() {
                functionToPaste(spriteCanvas);
            };

            document.body.appendChild(pasteButton);
            this.SelectedAreaButtons.push(pasteButton);
        }
        

        this.SelectedAreaButtons.push(resizeButton);
        this.SelectedAreaButtons.push(copyButton);
    }

    addDrawingChangesToUndoMap(actionOverride) {
        if (this.drawingChanges.size > 0) {
            if (this.undoStack.length === 0) {
                this.dispatchUndoAvailabilityEvent(true);
            }
            else if (this.undoStack.length >= 25) {
                this.undoStack.pop(); // undo limit set to 25 for some reason
            }
            let action;
            if (actionOverride) {
                action = actionOverride;
            }
            else {
                action = this.mode;
            }
            this.undoStack.unshift([action, this.drawingChanges])

            this.drawingChanges = new Map();
        }
    }

    undoLastAction() {
        if (this.undoStack.length>0) {
            if (this.undoStack.length === 1) {
               this.dispatchUndoAvailabilityEvent(false);
            }

            let undo = this.undoStack.shift();
            let actionToUndo = undo[0];
            let payloadToUndo = undo[1];

            if (actionToUndo === "draw" || actionToUndo === "line" || actionToUndo === "fill" || actionToUndo === "paste") {
                this.resetTilesBackToPreviousColors(payloadToUndo)
                this.drawListOfTiles(Array.from(payloadToUndo.keys()));
            }
            else if (actionToUndo === "colorPicker") {
                this.changeColorAndTransparencyAndDispatchEvent(
                    payloadToUndo.get("color"),
                    payloadToUndo.get("transparency")
                );
            }
            else if (actionToUndo === "spriteResize") {
                this.heightInPixels = payloadToUndo.get("height");
                this.widthInPixels = payloadToUndo.get("width");
                this.tilesRows = payloadToUndo.get("tiles");

                this.canvas.width = this.widthInPixels * this.pixelSize;
                this.canvas.height = this.heightInPixels * this.pixelSize;
                this.drawCanvasWithTiles();
                this.dispatchCanvasResizeEvent();
            }
        }
    }

    resetTilesBackToPreviousColors(mapOfTilesToReset) {
        for (const [key, value] of mapOfTilesToReset.entries()) {
            key.color = value[0];
            key.transparency = value[1];
        }
    }

    addTileToDrawingChanges(tile) {
        if (!this.drawingChanges.has(tile)) {
            this.drawingChanges.set(tile, [tile.color, tile.transparency]);
        }
    }

    addCurrentStateToChangeHistory() {
        this.drawingChanges.set("height", this.heightInPixels);
        this.drawingChanges.set("width", this.widthInPixels);
        this.drawingChanges.set("tiles", this.tilesRows);

        this.addDrawingChangesToUndoMap("spriteResize");
    }
}

class spriteCanvasTile {
    constructor(xStart, yStart, color, transparency){
        this.xStart = xStart;
        this.yStart = yStart;
        this.color = color;
        this.transparency = transparency;
    }
}

function functionToResize(spriteCanvas) {
    spriteCanvas.resizeCanvasToSelectedArea();
}

function functionToCopy(spriteCanvas) {
    spriteCanvas.copySelectedSectionOfTiles();
}

function functionToPaste(spriteCanvas) {
    spriteCanvas.pasteCopiedSectionToStartOfSelectedSection();
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

function rgbToHex(r,g,b) {
    return "#" + rgbToHexHelper(r) + rgbToHexHelper(g) + rgbToHexHelper(b);
}

function rgbToHexHelper(number) {
    let hex = Number(number).toString(16);
    if (hex.length < 2) {
        hex = "0" + hex;
    }
    return hex;
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