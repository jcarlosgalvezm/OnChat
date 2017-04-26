package addons.bots;

import java.io.IOException;

import addons.bots.trivial.Trivial;

/**
 * 
 * Factoria de instancies de la classe Bot
 *
 */
public class BotFactory {
	
	private BotFactory(){}
	
	private static Bot getBot(String tipus) throws IOException, ClassNotFoundException{
		if(tipus.equals("trivial"))
			return new Trivial("Hola @Channel, escriviu !trivial.iniciar per començar el joc");
		else
			return null;
	}
	
	public static boolean launch(String tipus) throws IOException, ClassNotFoundException {
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
