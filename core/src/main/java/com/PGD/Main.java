package com.PGD;

import java.awt.Point;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends ApplicationAdapter {
    // Map dimensions
    private int mapWidth = 100;
    private int mapHeight = 100;
    private final boolean DEBUG = false;

    // Game objects
    private SpriteBatch batch;
    private TiledMap tiledMap;

    // Camera and renderer
    private static OrthographicCamera camera;
    private TiledMapRenderer renderer;

    // Textures
    private Texture tileSet;
    private Texture wallSet;

    // Window dimensions
    private static int windowWidth;
    private static int windowHeight;

    // Player and enemy
    private Collider collider;

    // Map and input
    private Map map;

    // Input processor
    private GameInputProcessor gameInputProcessor;

    // Player and animations
    private PlayerAnimation playerAnimation;
    private Player player;

    // Enemy manager
    private EnemyManager enemyManager;

    // Shape renderer for debug
    private ShapeRenderer shapeRenderer;

    // Stages for pause and main menu
    private Stage pauseStage, mainMenuStage, endStage;
    private Label endStagLabel;

    // Skin for UI
    private Skin skin;
    private boolean isPauseVisible = false;
    private boolean mainMenu = true;

    // Minimap
    private OrthographicCamera minimapCamera;
    private ShapeRenderer minimapShapeRenderer;

    // Health bar
    private BitmapFont healthFont;

    // Animation timer
    private float aniTimer;

    // These store the values of each map generation so they appear in options menu
    private String genType;
    private int mapIterations;
    private int fillThreshold;
    private boolean disablePassages;
    private boolean disableWalls;
    private int propDensity;
    private int itemDensity;
    private int enemyDensity;
    private int caBirthRate;
    private int caSurvivalRate;

    // End game variables
    private static boolean isEndVisible = false; // Signals that current level has been exited.

    /*
     * Redraws the map with the current settings
     */
    @Override
    public void create() {

        tileSet = new Texture(Gdx.files.internal("PrimaryDugeonAssets/atlas_floor-16x16.png"));
        wallSet = new Texture(Gdx.files.internal("PrimaryDugeonAssets/atlas_walls_low-16x16.png"));

        // Floor Textures added to the MapCellFactory
        TextureRegion[][] splitFloorTiles = TextureRegion.split(tileSet, 16, 16);
        MapCellFactory.getCellType(CellState.TILE, splitFloorTiles[0][0], true);
        MapCellFactory.getCellType(CellState.TILE_VAR1, splitFloorTiles[0][1], true);
        MapCellFactory.getCellType(CellState.TILE_VAR2, splitFloorTiles[0][2], true);
        MapCellFactory.getCellType(CellState.TILE_VAR3, splitFloorTiles[1][1], true);
        MapCellFactory.getCellType(CellState.TILE_LEFT, splitFloorTiles[1][0], true);
        MapCellFactory.getCellType(CellState.TILE_RIGHT, splitFloorTiles[1][2], true);
        MapCellFactory.getCellType(CellState.TILE_UP_LEFT, splitFloorTiles[2][1], true);
        MapCellFactory.getCellType(CellState.TILE_UP_RIGHT, splitFloorTiles[2][0], true);
        MapCellFactory.getCellType(CellState.TILE_LIGHT, splitFloorTiles[0][0], true);
        MapCellFactory.getCellType(CellState.HALF_LEFT, splitFloorTiles[5][5], true);
        MapCellFactory.getCellType(CellState.HALF_RIGHT, splitFloorTiles[5][4], true);
        MapCellFactory.getCellType(CellState.BLANK, splitFloorTiles[6][6], false);
        MapCellFactory.getCellType(CellState.FULL_FLOOR, splitFloorTiles[1][1], true);

        // Wall Texture and dimensions added to wall factory
        TextureRegion[][] wallSplitTiles = TextureRegion.split(wallSet, 16, 16);
        WallCellFactory.getWallType(0, wallSplitTiles[3][2], 16, 16, 0, 0);
        WallCellFactory.getWallType(1, wallSplitTiles[2][0], 6, 16, 5, 0);
        WallCellFactory.getWallType(2, wallSplitTiles[3][2], 11, 16, 0, 0);
        WallCellFactory.getWallType(3, wallSplitTiles[2][3], 11, 16, 0, 0);
        WallCellFactory.getWallType(4, wallSplitTiles[3][2], 16, 16, 0, 0);
        WallCellFactory.getWallType(5, wallSplitTiles[2][1], 11, 16, 5, 0);
        WallCellFactory.getWallType(6, wallSplitTiles[3][2], 16, 16, 0, 0);
        WallCellFactory.getWallType(7, wallSplitTiles[2][2], 16, 16, 0, 0);
        WallCellFactory.getWallType(8, wallSplitTiles[0][0], 6, 16, 5, 0);
        WallCellFactory.getWallType(9, wallSplitTiles[1][0], 6, 16, 5, 0);
        WallCellFactory.getWallType(10, wallSplitTiles[0][3], 11, 16, 0, 0);
        WallCellFactory.getWallType(11, wallSplitTiles[1][3], 11, 16, 0, 0);
        WallCellFactory.getWallType(12, wallSplitTiles[0][1], 11, 16, 5, 0);
        WallCellFactory.getWallType(13, wallSplitTiles[1][1], 11, 16, 5, 0);
        WallCellFactory.getWallType(14, wallSplitTiles[0][2], 16, 16, 0, 0);
        WallCellFactory.getWallType(15, wallSplitTiles[1][2], 16, 16, 0, 0);

        // Item factory
        TextureRegion[] torch = new TextureRegion[8];
        torch[0] = new TextureRegion(new Texture(Gdx.files.internal("items/torch_1.png")));
        torch[1] = new TextureRegion(new Texture(Gdx.files.internal("items/torch_2.png")));
        torch[2] = new TextureRegion(new Texture(Gdx.files.internal("items/torch_3.png")));
        torch[3] = new TextureRegion(new Texture(Gdx.files.internal("items/torch_4.png")));
        torch[4] = new TextureRegion(new Texture(Gdx.files.internal("items/torch_5.png")));
        torch[5] = new TextureRegion(new Texture(Gdx.files.internal("items/torch_6.png")));
        torch[6] = new TextureRegion(new Texture(Gdx.files.internal("items/torch_7.png")));
        torch[7] = new TextureRegion(new Texture(Gdx.files.internal("items/torch_8.png")));

        Animation<TextureRegion> torchAnimation = new Animation<>(0.25f, torch);

        Texture closedChest = new Texture(Gdx.files.internal("items/chest_golden_closed.png"));
        Texture openChest = new Texture(Gdx.files.internal("items/chest_golden_open_empty.png"));

        ItemFactory.getItem("chest", closedChest,
                15, 12, 0, 0, false, true, openChest, null);
        ItemFactory.getItem("torch", null,
                8, 15, 4, 1, true, false, null, torchAnimation);
        ItemFactory.getItem("box", new Texture(Gdx.files.internal("items/box.png")),
                16, 20, 0, 0, true, false, null, null);
        ItemFactory.getItem("skull", new Texture(Gdx.files.internal("items/skull.png")),
                4, 4, 5, 5, true, false, null, null);

        Texture closedDoor = new Texture(Gdx.files.internal("items/door_closed.png"));
        Texture openDoor = new Texture(Gdx.files.internal("items/door_open.png"));

        ItemFactory.getItem("door", closedDoor,
                25, 27, 5, 0, false, true, openDoor, null);

        aniTimer = 0f;

        windowWidth = Gdx.graphics.getWidth();
        windowHeight = Gdx.graphics.getHeight();

        // Initial map
        genType = "Cellurlar Automata";
        mapIterations = 15;
        fillThreshold = 30;
        disablePassages = false;
        disableWalls = false;
        propDensity = 20;
        itemDensity = 20;
        enemyDensity = 50;
        caBirthRate = 5;
        caSurvivalRate = 3;

        batch = new SpriteBatch();

        redrawMap();

        pauseStage = new Stage(new ScreenViewport());
        mainMenuStage = new Stage(new ScreenViewport());
        endStage = new Stage(new ScreenViewport());

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table mainMenuTable = new Table();
        mainMenuTable.setFillParent(true);

        Image logo = new Image(new Texture(Gdx.files.internal("CAGGlogo.png")));
        TextButton playButton = new TextButton("Play", skin);
        TextButton mainMenuQuitButton = new TextButton("Quit", skin);

        mainMenuTable.add(logo).width(400).height(200).pad(10).row();
        mainMenuTable.add(playButton).pad(10).width(200).row();
        mainMenuTable.add(mainMenuQuitButton).pad(10).width(200).row();

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainMenu = false;
                Gdx.input.setInputProcessor(gameInputProcessor);
            }
        });

        mainMenuQuitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table pauseTable = new Table();
        pauseTable.setFillParent(true);

        Image keyMap = new Image(new Texture(Gdx.files.internal("KeyMap.png")));
        TextButton resumeButton = new TextButton("Resume", skin);
        TextButton optionsButton = new TextButton("Options", skin);
        TextButton mainMenuButton = new TextButton("Main Menu", skin);
        TextButton gameQuitButton = new TextButton("Quit", skin);

        pauseTable.add(keyMap).width(519).height(393).pad(10).row();
        pauseTable.add(resumeButton).pad(10).width(200).row();
        pauseTable.add(optionsButton).pad(10).width(200).row();
        pauseTable.add(mainMenuButton).pad(10).width(200).row();
        pauseTable.add(gameQuitButton).pad(10).width(200).row();

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isPauseVisible = false;
                Gdx.input.setInputProcessor(gameInputProcessor);
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showOptionsMenu();
            }
        });

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isPauseVisible = false;
                mainMenu = true;
                Gdx.input.setInputProcessor(mainMenuStage);
            }
        });

        gameQuitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table endScreenTable = new Table();
        endScreenTable.setFillParent(true);

        TextButton endPlayButton = new TextButton("Play", skin);
        TextButton endOptionsButton = new TextButton("Options", skin);
        TextButton endGameQuitButton = new TextButton("Quit", skin);

        // Label must be stored to it may be updated with players final score.
        endStagLabel = new Label("", skin);

        endScreenTable.add(endStagLabel).pad(10).center().row();
        endScreenTable.add(endPlayButton).pad(10).width(200).row();
        endScreenTable.add(endOptionsButton).pad(10).width(200).row();
        endScreenTable.add(endGameQuitButton).pad(10).width(200).row();

        endPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isEndVisible = false;
                redrawMap();
                Gdx.input.setInputProcessor(gameInputProcessor);
            }
        });

        endOptionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showOptionsMenu();
            }
        });

        endGameQuitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        mainMenuStage.addActor(mainMenuTable);
        pauseStage.addActor(pauseTable);
        endStage.addActor(endScreenTable);

        Gdx.input.setInputProcessor(mainMenuStage);

        minimapCamera = new OrthographicCamera();
        minimapCamera.setToOrtho(false, mapWidth * 16, mapHeight * 16);
        minimapShapeRenderer = new ShapeRenderer();
        minimapCamera.zoom = 1.0f;

        shapeRenderer = new ShapeRenderer();

        healthFont = new BitmapFont();
        healthFont.getData().setScale(1.5f); // Make text a bit bigger
    }

    private void renderScore() {
        // Create a font and set its scale
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2.0f); // Make text bigger

        batch.setProjectionMatrix(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()).combined);
        batch.begin();
        font.draw(batch, "Score: " + player.getScore(), -windowWidth + 10, -windowHeight + font.getLineHeight());
        batch.end();

        // Dispose of the font
        font.dispose();
    }

    private void renderHealthBar() {
        // Reset projection matrix to screen coordinates
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix().setToOrtho2D(0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        float x = 10;
        float y = Gdx.graphics.getHeight() - 30;
        float width = 200;
        float height = 20;

        // Draw the red health bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Background (darker red)
        shapeRenderer.setColor(0.5f, 0, 0, 1);
        shapeRenderer.rect(x, y, width, height);
        // Current health (brighter red)
        shapeRenderer.setColor(1, 0, 0, 1);
        float healthPercentage = (float) player.getCurrentHealth() / player.getMaxHealth();
        shapeRenderer.rect(x, y, width * healthPercentage, height);
        shapeRenderer.end();

        // Draw the text
        batch.begin();
        String healthText = player.getCurrentHealth() + "/" + player.getMaxHealth();
        // Center the text in the health bar
        GlyphLayout layout = new GlyphLayout(healthFont, healthText);
        float textX = x + (width - layout.width) / 2;
        float textY = y + (height + layout.height) / 2;
        healthFont.setColor(1, 1, 1, 1); // White text
        healthFont.draw(batch, healthText, textX, textY);
        batch.end();
    }

    private void renderMinimap() {
        float minimapSize = 200; // Fixed size
        float minimapX = Gdx.graphics.getWidth() - minimapSize - 20;
        float minimapY = Gdx.graphics.getHeight() - minimapSize - 20;

        float viewportX = Gdx.graphics.getWidth();
        float viewportY = Gdx.graphics.getHeight();

        // Draw background panel
        shapeRenderer.setProjectionMatrix(new OrthographicCamera(viewportX, viewportY).combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.85f);
        shapeRenderer.rect(minimapX - 10, minimapY - 10, minimapSize + 20, minimapSize + 20);
        shapeRenderer.end();

        // Set viewport for minimap
        Gdx.gl.glViewport((int) minimapX, (int) minimapY, (int) minimapSize, (int) minimapSize);

        // Update minimap camera to follow player
        minimapCamera.position.set(player.getPositionX(), player.getPositionY(), 0);
        minimapCamera.zoom = 0.5f; // Adjust this value to show more or less area around player
        minimapCamera.update();

        // Draw floor tiles
        minimapShapeRenderer.setProjectionMatrix(minimapCamera.combined);
        minimapShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        minimapShapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
        MapCell[][] mapCells = map.getMap();
        for (int x = 0; x < mapHeight; x++) {
            for (int y = 0; y < mapWidth; y++) {
                if (mapCells[x][y].isFloor()) {
                    minimapShapeRenderer.rect(x * 16, y * 16, 16, 16);
                }
            }
        }
        minimapShapeRenderer.end();

        // Draw walls
        minimapShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        minimapShapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1);
        WallCell[][] wallCells = map.getWallCells();
        for (int x = 0; x < mapHeight; x++) {
            for (int y = 0; y < mapWidth; y++) {
                if (wallCells[x][y].getCellType() != null) {
                    minimapShapeRenderer.rect(x * 16, y * 16, 16, 16);
                }
            }
        }
        minimapShapeRenderer.end();

        // Draw items
        minimapShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Item item : map.getItems()) {
            switch (item.getType().getName()) {
                case "chest":
                    minimapShapeRenderer.setColor(1, 0.84f, 0, 0.9f);
                    minimapShapeRenderer.circle(item.getX() + 8, item.getY() + 8, 8);
                    break;
                case "torch":
                    minimapShapeRenderer.setColor(1, 0.5f, 0, 0.9f);
                    minimapShapeRenderer.circle(item.getX() + 8, item.getY() + 8, 6);
                    break;
                case "box":
                    minimapShapeRenderer.setColor(0.7f, 0.5f, 0.3f, 0.9f);
                    minimapShapeRenderer.rect(item.getX() + 4, item.getY() + 4, 10, 10);
                    break;
                case "skull":
                    minimapShapeRenderer.setColor(0.9f, 0.9f, 0.9f, 0.9f);
                    minimapShapeRenderer.circle(item.getX() + 8, item.getY() + 8, 6);
                    break;
                case "door":
                    minimapShapeRenderer.setColor(1f, 0.0f, 1f, 0.0f);
                    minimapShapeRenderer.rect(item.getX(), item.getY(), 20, 20);
                    break;
            }
        }
        minimapShapeRenderer.end();

        // Draw player (centered)
        minimapShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        minimapShapeRenderer.setColor(1, 0, 0, 1);
        minimapShapeRenderer.circle(player.getPositionX(), player.getPositionY(), 8);
        minimapShapeRenderer.end();

        // Draw enemies
        minimapShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Enemy enemy : enemyManager.getAllEnemies()) {
            minimapShapeRenderer.setColor(0, 1, 0, 1);
            minimapShapeRenderer.circle(enemy.getPositionX(), enemy.getPositionY(), 8);
        }

        minimapShapeRenderer.end();

        // Reset viewport
        Gdx.gl.glViewport(0, 0, (int) viewportX, (int) viewportY);
    }

    private void showOptionsMenu() {
        Stage currentStage = pauseStage;
        if (isEndVisible) {
            currentStage = endStage;
        }

        // Create options window
        Window optionsWindow = new Window("Options", skin);

        // Text Fields for user input
        TextField widthTextField = new TextField(String.valueOf(mapWidth), skin);
        TextField heightTextField = new TextField(String.valueOf(mapHeight), skin);
        TextField iterationsTextField = new TextField(String.valueOf(mapIterations), skin);
        TextField fillRegionsTextField = new TextField(String.valueOf(fillThreshold), skin);
        TextField decorateRegionsPropsTextField = new TextField(String.valueOf(propDensity), skin);
        TextField decorateRegionsItemTextField = new TextField(String.valueOf(itemDensity), skin);
        TextField decorateRegionsEnemyTextField = new TextField(String.valueOf(enemyDensity), skin);
        TextField birthRateTextField = new TextField(String.valueOf(caBirthRate), skin);
        TextField survivalRateTextField = new TextField(String.valueOf(caSurvivalRate), skin);

        // Selection box for map generation type
        SelectBox<String> genTypeSelect = new SelectBox<>(skin);
        genTypeSelect.setItems("Cellurlar Automata", "Random Walk", "Binary Space Partitioning", "Random Boxes");
        genTypeSelect.setAlignment(Align.center);
        genTypeSelect.setSelected(genType);

        // Check boxes for refinement algorithms
        CheckBox connectRegionsCheck = new CheckBox("", skin);
        connectRegionsCheck.getImage().scaleBy(1);
        CheckBox setWallsCheck = new CheckBox("", skin);
        setWallsCheck.getImage().scaleBy(1);

        TextButton changeButton = new TextButton("Create Map", skin);
        TextButton backButton = new TextButton("Back", skin);

        // Add input validation (so only numbers and backspace can pe pressed)
        TextField.TextFieldListener numberListener = (TextField textField, char c) -> {
            if (!Character.isDigit(c) && c != '\b') {
                textField.setText(textField.getText().replaceAll("[^0-9]", ""));
            }
        };

        widthTextField.setTextFieldListener(numberListener);
        heightTextField.setTextFieldListener(numberListener);
        iterationsTextField.setTextFieldListener(numberListener);
        fillRegionsTextField.setTextFieldListener(numberListener);
        decorateRegionsPropsTextField.setTextFieldListener(numberListener);
        decorateRegionsItemTextField.setTextFieldListener(numberListener);
        decorateRegionsEnemyTextField.setTextFieldListener(numberListener);
        birthRateTextField.setTextFieldListener(numberListener);
        survivalRateTextField.setTextFieldListener(numberListener);

        // Layout the window
        optionsWindow.add(new Label("Map Width:", skin)).center();
        optionsWindow.add(widthTextField).width(100).pad(5).row();

        optionsWindow.add(new Label("Map Height:", skin)).center();
        optionsWindow.add(heightTextField).width(100).pad(5).row();

        optionsWindow.add(new Label("Map Processing Iterations:", skin)).center();
        optionsWindow.add(iterationsTextField).width(100).pad(5).row();

        optionsWindow.add(new Label("Fill Regions Threshold: ", skin)).center();
        optionsWindow.add(fillRegionsTextField).width(100).pad(5).row();

        optionsWindow.add(new Label("Map Prop Density: ", skin)).center();
        optionsWindow.add(decorateRegionsPropsTextField).width(100).pad(5).row();

        optionsWindow.add(new Label("Map Item Density: ", skin)).center();
        optionsWindow.add(decorateRegionsItemTextField).width(100).pad(5).row();

        optionsWindow.add(new Label("Map Enemy Density: ", skin)).center();
        optionsWindow.add(decorateRegionsEnemyTextField).width(100).pad(5).row();

        optionsWindow.add(new Label("Disable Passages: ", skin)).center();
        optionsWindow.add(connectRegionsCheck).width(100).pad(5).row();

        optionsWindow.add(new Label("Disable Walls/Collison: ", skin)).center();
        optionsWindow.add(setWallsCheck).width(100).pad(5).row();

        optionsWindow.add(new Label("Map Generation Type:", skin)).center();
        optionsWindow.add(genTypeSelect).pad(5).row();

        optionsWindow.add(new Label("CA Birth Rate:", skin)).center();
        optionsWindow.add(birthRateTextField).width(100).pad(5).row();

        optionsWindow.add(new Label("CA Survival Rate:", skin)).center();
        optionsWindow.add(survivalRateTextField).width(100).pad(5).row();

        // Create a table for buttons
        Table buttonTable = new Table();
        buttonTable.add(changeButton).pad(5).width(150);
        buttonTable.add(backButton).pad(5).width(80);
        optionsWindow.add(buttonTable).colspan(2).pad(5).row();

        optionsWindow.pack();

        // Change button listener
        changeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    mapWidth = Integer.parseInt(widthTextField.getText());
                    mapHeight = Integer.parseInt(heightTextField.getText());
                    mapIterations = Integer.parseInt(iterationsTextField.getText());
                    fillThreshold = Integer.parseInt(fillRegionsTextField.getText());
                    propDensity = Integer.parseInt(decorateRegionsPropsTextField.getText());
                    itemDensity = Integer.parseInt(decorateRegionsItemTextField.getText());
                    enemyDensity = Integer.parseInt(decorateRegionsEnemyTextField.getText());
                    caBirthRate = Integer.parseInt(birthRateTextField.getText());
                    caSurvivalRate = Integer.parseInt(survivalRateTextField.getText());

                    disablePassages = connectRegionsCheck.isChecked();
                    disableWalls = setWallsCheck.isChecked();
                    genType = genTypeSelect.getSelected();

                    // Add some basic validation (Could generate a map above 300, but performance
                    // suffers on a low hardware device, so has been capped)
                    if (mapWidth >= 10 && mapWidth <= 300 && mapHeight >= 10 && mapHeight <= 300) {
                        redrawMap();
                        optionsWindow.remove();
                        isPauseVisible = false;
                        isEndVisible = false;
                        Gdx.input.setInputProcessor(gameInputProcessor);
                    } else {
                        // Show error message if values are out of bounds
                        Dialog dialog = new Dialog("Error", skin);
                        dialog.text("Please enter values between 10 and 300");
                        dialog.button("OK");

                        if (isPauseVisible) {
                            dialog.show(pauseStage);
                        } else {
                            dialog.show(endStage);
                        }

                    }
                } catch (NumberFormatException e) {
                    // Show error message if input is invalid
                    Dialog dialog = new Dialog("Error", skin);
                    dialog.text("Please enter valid numbers");
                    dialog.button("OK");

                    if (isPauseVisible) {
                        dialog.show(pauseStage);
                    } else {
                        dialog.show(endStage);
                    }
                }
            }
        });

        // Add back button listener
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                optionsWindow.remove();
            }
        });

        // Center the window on screen
        optionsWindow.setPosition(
                (currentStage.getWidth() - optionsWindow.getWidth()) / 2,
                (currentStage.getHeight() - optionsWindow.getHeight()) / 2);

        optionsWindow.setModal(true);

        // Add minimum size to prevent tiny window
        optionsWindow.setWidth(Math.max(optionsWindow.getWidth(), 200));
        optionsWindow.setHeight(Math.max(optionsWindow.getHeight(), 250));

        currentStage.addActor(optionsWindow);
    }

    private void redrawMap() {
        // Clean up old resources
        if (tiledMap != null) {
            tiledMap.dispose();
        }

        // Create new map with new dimensions
        map = new Map(mapHeight, mapWidth);

        // Conduct main generation
        switch (genType) {
            case "Cellurlar Automata":
                map.generateMapCA(mapIterations, caBirthRate, caSurvivalRate);
                break;
            case "Random Walk":
                map.generateMapRW(mapIterations);
                break;
            case "Binary Space Partitioning":
                map.generateMapBSP(mapIterations);
                break;
            case "Random Boxes":
                map.generateMapRB(mapIterations);
                break;
        }

        map.fillRegions(fillThreshold);
        if (!disablePassages)
            map.connectRegions();

        map.decorateRegions(propDensity, itemDensity, enemyDensity);

        // Create new player animation
        playerAnimation = new PlayerAnimation();

        // Set start & exit positions
        Point points = map.getFurthestPoints();
        if (points != null) {
            // Position player at the start point
            int startX = points.x * 16 + 8;
            int startY = points.y * 16 + 8;
            player = new Player(startX, startY, playerAnimation);
        }

        // Create new enemy manager and populate
        enemyManager = new EnemyManager(player);

        for (Point position : map.getEnemyLocations()) {
            int pointX = position.x * 16 + 8;
            int pointY = position.y * 16 + 12;
            enemyManager.addEnemy(pointX, pointY);
        }

        if (!disableWalls)
            map.setWalls();

        // Set collider
        collider = new Collider(enemyManager);

        // Generate new map tiles
        tiledMap = map.drawMap(collider);
        renderer = new OrthogonalTiledMapRenderer(tiledMap);

        // Create and set new input processor
        gameInputProcessor = new GameInputProcessor(player);
        Gdx.input.setInputProcessor(gameInputProcessor);

        // Reset and update camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, windowWidth, windowHeight);
        camera.position.set(player.getPositionX(), player.getPositionY(), 1);
        camera.update();
    }

    @Override
    public void render() {
        if (mainMenu) {
            ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
            mainMenuStage.act(Gdx.graphics.getDeltaTime());
            mainMenuStage.draw();
        } else if (isPauseVisible) {
            // Render menu
            ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
            pauseStage.act(Gdx.graphics.getDeltaTime());
            pauseStage.draw();
        } else if (isEndVisible) {
            // Render end screen
            Gdx.input.setInputProcessor(endStage);
            endStagLabel.setText("Your Final Score: " + player.getScore());
            ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
            endStage.act(Gdx.graphics.getDeltaTime());
            endStage.draw();
        } else {
            ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            player.update(Gdx.graphics.getDeltaTime(), collider, map);
            enemyManager.updateEnemies(Gdx.graphics.getDeltaTime(), collider, map);

            camera.position.set(player.getPositionX(), player.getPositionY(), 1);
            camera.update();

            renderer.setView(camera);
            renderer.render();

            batch.setProjectionMatrix(camera.combined);

            batch.begin();

            ProjectileManager.Update(batch, collider, Gdx.graphics.getDeltaTime());

            aniTimer += Gdx.graphics.getDeltaTime();
            for (Item item : map.getItems()) {
                item.draw(batch, aniTimer);
            }

            playerAnimation.render(batch, Gdx.graphics.getDeltaTime());
            enemyManager.renderEnemyAnimations(batch, Gdx.graphics.getDeltaTime());

            FXAnimation.playAnimation(batch, Gdx.graphics.getDeltaTime());

            batch.end();

            if (DEBUG) {
                shapeRenderer.setProjectionMatrix(camera.combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

                for(Arrow arrow : ProjectileManager.GetArrows()){
                    Rectangle rc = arrow.getRectangle();
                    shapeRenderer.rect(rc.x, rc.y, rc.width, rc.height);
                }

                for (Rectangle rect : collider.getFixedColliders()) {
                    shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
                }
                for (Item item : map.getItems()) {
                    Rectangle itemRect = item.getHitBox();
                    shapeRenderer.rect(itemRect.x, itemRect.y, itemRect.width, itemRect.height);
                }
                shapeRenderer.rect(player.getCollider().x, player.getCollider().y, player.getCollider().width,
                        player.getCollider().height);
                shapeRenderer.rect(player.getHitBox().x, player.getHitBox().y, player.getHitBox().width,
                        player.getHitBox().height);

                for (Enemy enemy : enemyManager.getAllEnemies()) {
                    shapeRenderer.rect(enemy.getCollider().x, enemy.getCollider().y, enemy.getCollider().width,
                            enemy.getCollider().height);

                    shapeRenderer.rect(enemy.getHitBox().x, enemy.getHitBox().y, enemy.getHitBox().width,
                            enemy.getHitBox().height);
                }

                shapeRenderer.end();
            }

            renderMinimap();
            renderScore();
            renderHealthBar();

            if (player.isDead()) {
                endLevel();
            }

            super.render();
        }

        // Add menu toggle button
        if (!mainMenu && !isEndVisible && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPauseVisible = !isPauseVisible;
            Gdx.input.setInputProcessor(isPauseVisible ? pauseStage : gameInputProcessor);
        }
    }

    public static void increaseView() {
        if (windowWidth > 240 && windowHeight > 240) {

            if (camera.zoom > 1)
                camera.zoom -= 0.2f;
            camera.update();
            camera.setToOrtho(false, windowWidth, windowHeight);
        }
    }

    public static void decreaseView() {
        camera.zoom += 0.2f;
        camera.update();
        camera.setToOrtho(false, windowWidth, windowHeight);
    }

    /**
     * Trigger when the player interacts with door, ending the current level.
     */
    public static void endLevel() {
        isEndVisible = true;
    }

    @Override
    public void resize(int width, int height) {
        windowWidth = Gdx.graphics.getWidth() / 2;
        windowHeight = Gdx.graphics.getHeight() / 2;

        camera.setToOrtho(false, windowWidth, windowHeight);
        pauseStage.getViewport().update(width, height, true);
        mainMenuStage.getViewport().update(width, height, true);
        endStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        healthFont.dispose();
    }

    public boolean isMainMenu() {
        return mainMenu;
    }

    public boolean isHealthBarVisible() {
        return !mainMenu && !isPauseVisible && !isEndVisible;
    }

    public boolean isMinimapVisible() {
        return !mainMenu && !isPauseVisible && !isEndVisible;
    }

    public boolean isScoreVisible() {
        return !mainMenu && !isPauseVisible && !isEndVisible;
    }

    public void startGame() {
        mainMenu = false;
        isPauseVisible = false;
        // Additional game start logic
    }

    public void togglePause() {
        if (!mainMenu) {
            isPauseVisible = !isPauseVisible;
        }
    }

    public boolean isPauseVisible() {
        return isPauseVisible;
    }

    /**
     * Check to see if end screen is currently displayed for testing purposes
     */
    public boolean isEndVisible() {
        return isEndVisible;
    }

}