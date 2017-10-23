package com.djrapitops.plugin.command.bukkit;

import com.djrapitops.plugin.command.ISender;
import com.djrapitops.plugin.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Class that is used to wrap a SubCommand implementation into executable
 * command by Bukkit.
 *
 * @author Rsl1122
 * @see SubCommand
 * @since 2.0.0
 */
public class BukkitCommand implements CommandExecutor {

    private final SubCommand subCmd;

    public BukkitCommand(SubCommand subCmd) {
        this.subCmd = subCmd;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ISender iSender;
        iSender = new BukkitCMDSender(sender);
        return subCmd.onCommand(iSender, label, args);
    }
}
