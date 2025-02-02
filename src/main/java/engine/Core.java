package engine;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import entity.Ship;
import screen.GameScreen;
import screen.HighScoreScreen;
import screen.ScoreScreen;
import screen.Screen;
import screen.TitleScreen;
import screen.ShipScreen;

/**
 * Implements core game logic.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public final class Core {

	/** Width of current screen. */
	private static final int WIDTH = 690;
	/** Height of current screen. */
	private static final int HEIGHT = 820;
	/** Max fps of current screen. */
	private static final int FPS = 60;

	/** Max lives. */
	private static final int MAX_LIVES = 3;
	/** Levels between extra life. */
	private static final int EXTRA_LIFE_FREQUENCY = 3;
	/** Total number of levels. */
	private static final int NUM_LEVELS = 9;

	
	/** Difficulty settings for level 1. */
	private static final GameSettings SETTINGS_LEVEL_1 =
			new GameSettings(5, 5, 2, 1000);
	/** Difficulty settings for level 2. */
	private static final GameSettings SETTINGS_LEVEL_2 =
			new GameSettings(5, 5, 50, 2500);
	/** Difficulty settings for level 3. */
	private static final GameSettings SETTINGS_LEVEL_3 =
			new GameSettings(6, 5, 40, 1500);
	/** Difficulty settings for level 4. */
	private static final GameSettings SETTINGS_LEVEL_4 =
			new GameSettings(6, 6, 30, 1500);
	/** Difficulty settings for level 5. */
	private static final GameSettings SETTINGS_LEVEL_5 =
			new GameSettings(7, 6, 20, 1000);
	/** Difficulty settings for bonus level*/
	private static final GameSettings SETTINGS_LEVEL_6_BONUS =
			new GameSettings(16, 7, 50, 2100000);
	/** Difficulty settings for level 7. */
	private static final GameSettings SETTINGS_LEVEL_7 =
			new GameSettings(7, 7, 10, 1000);
	/** Difficulty boss settings for level 7. */
	private static final GameSettings SETTINGS_LEVEL_8_BOSS =
			new GameSettings(8, 7, 2, 500);
	/** Difficulty settings for level 9. */
	private static final GameSettings SETTINGS_LEVEL_9 =
			new GameSettings(8, 7, 2, 500);

	/** Frame to draw the screen on.*/
	private static Frame frame;
	/** Screen currently shown. */
	private static Screen currentScreen;
	/** Difficulty settings list. */
	private static List<GameSettings> gameSettings;
	/** Application logger. */
	private static final transient Logger LOGGER = Logger.getLogger(Core.class
			.getSimpleName());
	/** Logger handler for printing to disk. */
	private static Handler fileHandler;
	/** Logger handler for printing to console. */
	private static ConsoleHandler consoleHandler;
	/** Flag to check if it's the main or restart. */
	public static boolean flag_main = false;
	public static boolean flag_restart = false;
	/** Audio background music*/
	public static Audio backgroundMusic = new Audio("src/main/resources/bgm.wav", true);
	/** Audio effect sound*/
	public static Sound effectSound = new Sound();

	/** returnCode */
	public static final int MAIN_MENU = 5;
	public static final int PLAY = 4;
	public static final int LOAD = 3;
	public static final int HIGH_SCORES = 2;
	public static final int CUSTOM = 1;
	public static final int EXIT = 0;
	public static final int RESTART = 8;

	/**
	 * Test implementation.
	 * 
	 * @param args
	 *            Program args, ignored.
	 */
	public static void main(final String[] args) throws IOException, ClassNotFoundException {
		try {
			LOGGER.setUseParentHandlers(false);

			fileHandler = new FileHandler("log");
			fileHandler.setFormatter(new MinimalFormatter());

			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new MinimalFormatter());

			LOGGER.addHandler(fileHandler);
			LOGGER.addHandler(consoleHandler);
			LOGGER.setLevel(Level.ALL);

		} catch (Exception e) {
			// TODO handle exception
			e.printStackTrace();
		}
		backgroundMusic.decrease();

		frame = new Frame(WIDTH, HEIGHT);
		DrawManager.getInstance().setFrame(frame);
		int width = frame.getWidth();
		int height = frame.getHeight();

		gameSettings = new ArrayList<GameSettings>();
		gameSettings.add(SETTINGS_LEVEL_1);
		gameSettings.add(SETTINGS_LEVEL_2);
		gameSettings.add(SETTINGS_LEVEL_3);
		gameSettings.add(SETTINGS_LEVEL_4);
		gameSettings.add(SETTINGS_LEVEL_5);
		gameSettings.add(SETTINGS_LEVEL_6_BONUS);
		gameSettings.add(SETTINGS_LEVEL_7);
		gameSettings.add(SETTINGS_LEVEL_8_BOSS);
		gameSettings.add(SETTINGS_LEVEL_9);


		DesignSetting designSetting = new DesignSetting(DrawManager.SpriteType.Ship);
		GameState gameState;

		int returnCode = MAIN_MENU;
		do {
			flag_main = false;

			gameState = new GameState(1, 0, MAX_LIVES, 0, 0,3, new int[]{15, 15, 15, 15},0);

			switch (returnCode) {
				case MAIN_MENU:
				// Main menu.
				currentScreen = new TitleScreen(width, height, FPS);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " title screen at " + FPS + " fps.");

				backgroundMusic.start();
				returnCode = frame.setScreen(currentScreen,0);

				LOGGER.info("Closing title screen.");
				break;
				case PLAY:
				// Game & score.
					do {
						// One extra live every few levels.
						boolean bonusLife = gameState.getLevel()
								% EXTRA_LIFE_FREQUENCY == 0
								&& gameState.getLivesRemaining() < MAX_LIVES;

						currentScreen = new GameScreen(gameState,
								gameSettings.get(gameState.getLevel() - 1),
								bonusLife, designSetting, width, height, FPS, frame);
						LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
								+ " game screen at " + FPS + " fps.");
						frame.setScreen(currentScreen, 0);
						LOGGER.info("Closing game screen.");
						if (flag_main)
							break;
						if (flag_restart) {
							flag_restart = false;
							continue;
						}

						gameState = ((GameScreen) currentScreen).getGameState();

						gameState = new GameState(gameState.getLevel() + 1,
								gameState.getScore(),
								gameState.getLivesRemaining(),
								gameState.getBulletsShot(),
								gameState.getShipsDestroyed(),
								gameState.getBoomTimes(),
								gameState.getSkillCool(),
								gameState.getUltimateTimes());

					} while (gameState.getLivesRemaining() > 0
							&& gameState.getLevel() <= NUM_LEVELS);

					effectSound.roundEndSound.start();
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " score screen at " + FPS + " fps, with a score of "
							+ gameState.getScore() + ", "
							+ gameState.getLivesRemaining() + " lives remaining, "
							+ gameState.getBulletsShot() + " bullets shot and "
							+ gameState.getShipsDestroyed() + " ships destroyed.");
					currentScreen = new ScoreScreen(width, height, FPS, gameState);
					returnCode = frame.setScreen(currentScreen, 0);
					LOGGER.info("Closing score screen.");
					break;
				case HIGH_SCORES:
					// High scores.
					currentScreen = new HighScoreScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " high score screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen, 0);
					LOGGER.info("Closing high score screen.");
					break;
				case RESTART:
					// Game & score. (Restart)

					do {
						gameState = new GameState(gameState.getLevel(),
								gameState.getScore(),
								gameState.getLivesRemaining(),
								gameState.getBulletsShot(),
								gameState.getShipsDestroyed(),
								gameState.getBoomTimes(),
								gameState.getSkillCool(),
								gameState.getUltimateTimes());

						// One extra live every few levels.
						boolean bonusLife = gameState.getLevel()
								% EXTRA_LIFE_FREQUENCY == 0
								&& gameState.getLivesRemaining() < MAX_LIVES;

						currentScreen = new GameScreen(gameState,
								gameSettings.get(gameState.getLevel() - 1),
								bonusLife, designSetting, width, height, FPS, frame);

						LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
								+ " game screen at " + FPS + " fps.");
						frame.setScreen(currentScreen, 0);
						LOGGER.info("Closing game screen.");
						if (flag_main)
							break;
						if (flag_restart) {
							flag_restart = false;
							continue;
						}

						gameState = ((GameScreen) currentScreen).getGameState();

						gameState = new GameState(gameState.getLevel() + 1,
								gameState.getScore(),
								gameState.getLivesRemaining(),
								gameState.getBulletsShot(),
								gameState.getShipsDestroyed(),
								gameState.getBoomTimes(),
								gameState.getSkillCool(),
								gameState.getUltimateTimes());

					} while (gameState.getLivesRemaining() > 0
							&& gameState.getLevel() <= NUM_LEVELS);

					effectSound.roundEndSound.start();
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " score screen at " + FPS + " fps, with a score of "
							+ gameState.getScore() + ", "
							+ gameState.getLivesRemaining() + " lives remaining, "
							+ gameState.getBulletsShot() + " bullets shot and "
							+ gameState.getShipsDestroyed() + " ships destroyed.");
					currentScreen = new ScoreScreen(width, height, FPS, gameState);
					returnCode = frame.setScreen(currentScreen, 0);
					LOGGER.info("Closing score screen.");
					break;
				case LOAD:
					//load game & score

					boolean isFirst = false;
					boolean load = false;

					currentScreen = FileManager.getInstance().loadGame();

					do {
						// One extra live every few levels.
						if (isFirst) {
							boolean bonusLife = gameState.getLevel()
									% EXTRA_LIFE_FREQUENCY == 0
									&& gameState.getLivesRemaining() < MAX_LIVES;

							currentScreen = new GameScreen(gameState,
									gameSettings.get(gameState.getLevel() - 1),
									bonusLife, designSetting, width, height, FPS, frame);
						}

						isFirst = true;

						LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
								+ " game screen at " + FPS + " fps.");
						if (load == false) {
							frame.setScreen(currentScreen, 1);
							load = true;
						} else {
							frame.setScreen(currentScreen, 0);
						}
						LOGGER.info("Closing game screen.");
						if (flag_main)
							break;
						if (flag_restart) {
							flag_restart = false;
							continue;
						}

						gameState = ((GameScreen) currentScreen).getGameState();

						gameState = new GameState(gameState.getLevel() + 1,
								gameState.getScore(),
								gameState.getLivesRemaining(),
								gameState.getBulletsShot(),
								gameState.getShipsDestroyed(),
								gameState.getBoomTimes(),
								gameState.getSkillCool(),
								gameState.getUltimateTimes());

					} while (gameState.getLivesRemaining() > 0
							&& gameState.getLevel() <= NUM_LEVELS);
					effectSound.roundEndSound.start();
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " score screen at " + FPS + " fps, with a score of "
							+ gameState.getScore() + ", "
							+ gameState.getLivesRemaining() + " lives remaining, "
							+ gameState.getBulletsShot() + " bullets shot and "
							+ gameState.getShipsDestroyed() + " ships destroyed.");
					currentScreen = new ScoreScreen(width, height, FPS, gameState);
					returnCode = frame.setScreen(currentScreen, 0);
					LOGGER.info("Closing score screen.");


					break;
				case CUSTOM:
					//Custom
					currentScreen = new ShipScreen(width, height, FPS, designSetting);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " Ship screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen, 0);
					LOGGER.info("Closing high score screen.");
					break;

				default:
					break;
			}

		} while (returnCode != EXIT);

		fileHandler.flush();
		fileHandler.close();
		backgroundMusic.stop();
		System.exit(EXIT);
	}

	/**
	 * Constructor, not called.
	 */
	private Core() {

	}

	/**
	 * Controls access to the logger.
	 * 
	 * @return Application logger.
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Controls access to the drawing manager.
	 * 
	 * @return Application draw manager.
	 */
	public static DrawManager getDrawManager() {
		return DrawManager.getInstance();
	}

	/**
	 * Controls access to the input manager.
	 * 
	 * @return Application input manager.
	 */
	public static InputManager getInputManager() {
		return InputManager.getInstance();
	}

	/**
	 * Controls access to the file manager.
	 * 
	 * @return Application file manager.
	 */
	public static FileManager getFileManager() {
		return FileManager.getInstance();
	}

	/**
	 * Controls creation of new cooldowns.
	 * 
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @return A new cooldown.
	 */
	public static Cooldown getCooldown(final int milliseconds) {
		return new Cooldown(milliseconds);
	}

	/**
	 * Controls creation of new cooldowns with variance.
	 * 
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @param variance
	 *            Variation in the cooldown duration.
	 * @return A new cooldown with variance.
	 */
	public static Cooldown getVariableCooldown(final int milliseconds,
			final int variance) {
		return new Cooldown(milliseconds, variance);
	}
}