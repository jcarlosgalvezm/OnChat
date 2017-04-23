package addons.bots;

import addons.bots.trivial.Trivial;

/**
 * 
 * Factoria de instancies de la classe Bot
 *
 */
public class BotFactory {
	
	private BotFactory(){}
	
	private static Bot getBot(String tipus){
		if(tipus.equals("trivial"))
			return new Trivial();
		else
			return null;
	}
	
	public static boolean launch(String tipus) {
		Bot bot = getBot(tipus);
		if (bot != null) {
			Thread th_bot = new Thread(bot);
			th_bot.setName(tipus);
			th_bot.start();
			
			return true;
		}
		else
			return false;
	}

}
