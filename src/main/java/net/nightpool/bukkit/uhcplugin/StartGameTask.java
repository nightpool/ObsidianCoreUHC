package net.nightpool.bukkit.uhcplugin;

public class StartGameTask implements Runnable {

		private int delay;
		private int elapsed;
		private UHCGame game;

		public StartGameTask(int delay, UHCGame game) {
			this.delay = delay;
			this.elapsed = 0;
			this.game = game;
		}

		@Override
		public void run() {
			elapsed+=1;
			if(elapsed < delay){
				int diff = delay-elapsed;
				if(diff > 10){
					game.p.broadcast(String.valueOf(diff)+" seconds remaining.");
				} else{
					game.p.broadcast(String.valueOf(diff)+"!");
				}
				return;
			} else{
				try{
					game.startGame();
				} catch(NullPointerException e){
					game.stopCountdown(); return;
				}
				game.p.broadcast("The game has started. Good luck!");
				game.stopCountdown();
			}
			
			
		}

	}