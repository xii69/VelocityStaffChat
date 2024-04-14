package me.xii69.velocitystaffchat.settings;

import com.moandjiezana.toml.Toml;
import lombok.Getter;
import me.xii69.velocitystaffchat.VelocityStaffChat;
import org.slf4j.Logger;

@Getter
public class PluginSettings {

    private final Toml toml;

    private boolean discordEnabled;
    private String prefix;
    private String webhook;
    private String toggleFormat;
    private String messageFormat;
    private String webhookMessageFormat;

    private String messageOnlyPlayers;
    private String messageNoPermission;

    public PluginSettings(VelocityStaffChat plugin) {
        this.toml = plugin.getConfiguration();
        this.load();
    }

    public void load() {
        this.prefix = toml.getString("Configuration.Prefix");
        this.toggleFormat = toml.getString("Messages.Toggle-Format");
        this.messageFormat = toml.getString("Messages.Message-Format");
        this.webhook = toml.getString("Discord.Webhook");
        this.discordEnabled = toml.getBoolean("Discord.Enabled");
        this.webhookMessageFormat = toml.getString("Discord.Message-Format");

        this.messageOnlyPlayers = toml.getString("Messages.Only-Players");
        this.messageNoPermission = toml.getString("Messages.No-Permission");
    }
}
