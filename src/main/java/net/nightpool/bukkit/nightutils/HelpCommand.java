package net.nightpool.bukkit.nightutils;


@Registerable(name="help", aliases = {"h"}, description="Gives help for a command")
public class HelpCommand extends CommandHandler {
	private CommandRegister register;

	public HelpCommand(NCommand com, CommandPlugin p) {
		super(com, p);
		this.register = p.getCommandRegister();
	}

	@Override
	public void call(){
		if(pos.isEmpty()){
			throw new BadArgException("Need at least one arg (the name of a command)");
		}
		Class<? extends CommandHandler> command = register.get(pos.get(0));
		if(command == null){
			p.getCommandRegister().printUsage(sender);
		}
		Registerable an = command.getAnnotation(Registerable.class);
		sender.sendMessage(an.name()+" "+an.usage());
		sender.sendMessage(an.description());
		if(an.aliases().length>0){
			sender.sendMessage("  Aliases: "+NArrayUtils.toList(an.aliases()));
		}
	}

}
