package net.nightpool.bukkit.uhcplugin.utils;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlayerIterator implements Iterator<OfflinePlayer> {

    private Iterator<String> it;

    public PlayerIterator(Iterator<String> iterator) {
        this.it = iterator;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public OfflinePlayer next() {
        return Bukkit.getOfflinePlayer(it.next());
    }

    @Override
    public void remove() {
        it.remove();
    }

}
